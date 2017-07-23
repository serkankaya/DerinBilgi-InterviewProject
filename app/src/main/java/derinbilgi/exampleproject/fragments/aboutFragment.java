package derinbilgi.exampleproject.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import derinbilgi.exampleproject.MyApi;
import derinbilgi.exampleproject.R;
import derinbilgi.exampleproject.models.aboutModel;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class aboutFragment extends Fragment {
    private MyApi myApi;
    TextView aboutText;
    ImageView aboutImage;
    String value;
    String companyName,promotion,email,phoneNumber,photo;
    private Retrofit retrofit;
public aboutFragment() {
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
        getAboutInformation(value);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview=inflater.inflate(R.layout.fragment_about, container, false);
        getAboutInformation(value);
        aboutText= (TextView) rootview.findViewById(R.id.aboutText);
        aboutImage= (ImageView) rootview.findViewById(R.id.aboutImage);

        return rootview;
    }
    private void getAboutInformation(String value) {
        final String root = Environment.getExternalStorageDirectory().toString();
        Call<aboutModel> call = myApi.getAboutInformation(value);
        call.enqueue(new Callback<aboutModel>() {
            @Override
            public void onResponse(Call<aboutModel> call, Response<aboutModel> response) {
                if (response.isSuccessful()) {
                    Log.d("About Side", "Başarılı");
                    companyName=response.body().getCompanyName().toString();
                    promotion=response.body().getPromotion().toString();
                    email=response.body().getEmail().toString();
                    phoneNumber=response.body().getPhoneNumber().toString();
                    /*photo=response.body().getPhoto().toString();*/
                    System.out.println(companyName+"\n\n"+promotion+"\n\n"+email+"\n\n"+phoneNumber/*+"\n"+photo*/);
                    aboutText.setText(companyName+"\n"+promotion+"\n"+email+"\n"+phoneNumber/*+"\n"+photo*/);
                    if (response.body().getPhoto()==null){
                        aboutImage.setImageDrawable(getResources().getDrawable(R.drawable.company));
                    }else {
                        String Base64CompanyImage=response.body().getPhoto();
                        Boolean saved=false;
                        String root = Environment.getExternalStorageDirectory().toString();
                        try {
                            File folder = new File(Environment.getExternalStorageDirectory() + "/DerinBilgiPdf");
                            boolean success = true;
                            if (!folder.exists()) {
                                success = folder.mkdir();
                            }
                            if (success) {
                                // Do something on success
                                FileOutputStream fos = new FileOutputStream(root+"/DerinBilgiPdf/company.jpg");
                                System.out.println(root);
                                fos.write(Base64.decode(Base64CompanyImage, Base64.NO_WRAP));
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
                            File imgFile = new  File(root+"/DerinBilgiPdf/company.jpg");
                            if(imgFile.exists()){
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                aboutImage.setImageBitmap(myBitmap);
                            }

                        }


                    }

                } else {
                    Log.d("response", "Başarısız");

                }
            }
            @Override
            public void onFailure(Call<aboutModel> call, Throwable t) {
                Log.d("response", "onFailure");
                Log.d("response", "onFailure" + t.getMessage());
                Log.d("response", "onFailure" + t.getCause());

            }
        });
    }

}
