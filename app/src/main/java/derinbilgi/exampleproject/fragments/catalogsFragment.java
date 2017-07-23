package derinbilgi.exampleproject.fragments;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import derinbilgi.exampleproject.adapters.catalogCodeAdapter;
import derinbilgi.exampleproject.MyApi;
import derinbilgi.exampleproject.activities.pdfViewer;
import derinbilgi.exampleproject.R;
import derinbilgi.exampleproject.models.catalogCodeArrayModel;
import derinbilgi.exampleproject.models.catalogCode;
import derinbilgi.exampleproject.models.catalogFile;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static derinbilgi.exampleproject.activities.mainActivity.proCheck;
import static derinbilgi.exampleproject.activities.mainActivity.proDownload;

public class catalogsFragment extends Fragment {
    RecyclerView recyclerList;
    GridLayoutManager gridLayoutManager;
    private List<catalogCode> data_list;
    private catalogCodeAdapter adapter;

    private StaggeredGridLayoutManager _sGridLayoutManager;
    private MyApi myApi;
    String value;
    int activatePdfCount=0;
    String oneActivatePdfFileCode;
    Map<String, String> data = new HashMap<>();
    private Retrofit retrofit;
    public catalogsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();
        value=getString(R.string.key);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApi.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        myApi = retrofit.create(MyApi.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.fragment_catalogs, container, false);
        recyclerList= (RecyclerView) rootview.findViewById(R.id.recycler_catalog_list);
        data_list=new ArrayList<>();
        proCheck = new ProgressDialog(getContext());
        proCheck.setMessage(getString(R.string.checkPdfMessage));
        proCheck.show();

        getCatalogCodes(value);

        gridLayoutManager=new GridLayoutManager(getActivity(),2);
        _sGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerList.setLayoutManager(_sGridLayoutManager);
        adapter = new catalogCodeAdapter(getActivity(),data_list);
        recyclerList.setAdapter(adapter);
        recyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

        });
        return rootview;
    }

    private void getCatalogCodes(String value) {

        final String root = Environment.getExternalStorageDirectory().toString();
        Call<catalogCodeArrayModel> call = myApi.getCatalogCode(value);
        call.enqueue(new Callback<catalogCodeArrayModel>() {

            @Override
            public void onResponse(Call<catalogCodeArrayModel> call, Response<catalogCodeArrayModel> response) {
                if (response.isSuccessful()) {

                    Log.d("response", "Başarılı");
                    activatePdfCount=response.body().getCatalogCodes().size();

                    if (activatePdfCount==1){
                        oneActivatePdfFileCode=response.body().getCatalogCodes().get(0).getCatalogCodes();
                    }
                    else {
                        oneActivatePdfFileCode="0";
                    }
                    for (int i = 0; i <activatePdfCount ; i++) {
                        String CatalogCode=response.body().getCatalogCodes().get(i).getCatalogCodes();
                        catalogCode data2=new catalogCode(CatalogCode);
                        data_list.add(data2);
                        Log.d("response : ", " " +CatalogCode );
                        data.put("kod",CatalogCode);
                        data.put("musteriId","3");
                        System.out.println("LOL : "+i+"\n");
                        File file = new File(root+"/DerinBilgiPdf/"+CatalogCode+".pdf");
                        if(file.exists()){

                            if (activatePdfCount==1){
                                Intent intent=new Intent(getActivity(), pdfViewer.class);
                                intent.putExtra("oneActivatePdfFileCode",CatalogCode);
                                startActivity(intent);

                            }
                        }else{
                            proDownload = new ProgressDialog(getContext());
                            proDownload.setMessage(getString(R.string.downloadingMessage));
                            proDownload.show();
                            saveCatalogFiles(data,CatalogCode);
                            data.clear();
                        }
                    }
                    proCheck.dismiss();
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("responseGetCatalogCode", "Failed");
                }
            }
            @Override
            public void onFailure(Call<catalogCodeArrayModel> call, Throwable t) {
                Log.d("responseGetCatalogCode", "onFailure");
                Log.d("responseGetCatalogCode", "onFailure" + t.getMessage());
                Log.d("responseGetCatalogCode", "onFailure" + t.getCause());

            }
        });
    }
    private void saveCatalogFiles(Map<String, String> catalogCodeMap, final String code) {
        Call<catalogFile> call = myApi.getCatalogFile(value,catalogCodeMap);
        call.enqueue(new Callback<catalogFile>() {
            @Override
            public void onResponse(Call<catalogFile> call, Response<catalogFile> response) {
                if (response.isSuccessful()) {
                    String Base64Pdf=response.body().getCatalogFile();
                    Boolean oldPdf=response.body().getCatalogOld();
                    String catalogCode=response.body().getCatalogCode();
                    Log.d("responseFile :", "File Success");
                    Boolean saved=false;
                    String root = Environment.getExternalStorageDirectory().toString();
                    if (oldPdf){
                        System.out.println("Old Pdf File Code : "+catalogCode);
                    }else {
                        try {
                            File folder = new File(Environment.getExternalStorageDirectory() + "/DerinBilgiPdf");
                            boolean success = true;
                            if (!folder.exists()) {
                                success = folder.mkdir();
                            }
                            if (success) {
                                // Do something on success
                                FileOutputStream fos = new FileOutputStream(root+"/DerinBilgiPdf/"+code+".pdf");
                                System.out.println(root);
                                fos.write(Base64.decode(Base64Pdf, Base64.NO_WRAP));
                                fos.close();
                                saved=true;
                            } else {
                                // Do something else on failure
                                saved=false;
                            }
                        } catch (Exception e) {
                            saved=false;
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (saved){
                            proDownload.dismiss();
                            adapter.notifyDataSetChanged();
                            if (activatePdfCount==1){
                                Intent intent=new Intent(getActivity(), pdfViewer.class);
                                intent.putExtra("oneActivatePdfFileCode",oneActivatePdfFileCode);
                                startActivity(intent);
                            }
                        }

                    }

                } else {
                    Log.d("responseFile", "File Failed");
                }
            }
            @Override
            public void onFailure(Call<catalogFile> call, Throwable t) {
                Log.d("responseFile", "onFailure");
                Log.d("responseFile", "onFailure" + t.getMessage());
                Log.d("responseFile", "onFailure" + t.getCause());

            }
        });

    }
}
