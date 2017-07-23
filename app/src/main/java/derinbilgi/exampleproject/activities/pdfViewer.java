package derinbilgi.exampleproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import derinbilgi.exampleproject.MyApi;
import derinbilgi.exampleproject.R;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class pdfViewer extends AppCompatActivity {

    boolean gps_enabled = false;
    boolean network_enabled = false;
    Boolean gpsOpened=false;
    private MyApi myApi;
    String value;
    Map<String, String> data = new HashMap<>();
    private Retrofit retrofit;
    PDFView pdfViewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!checkNetwork() && !checkGPS()){
            Toast.makeText(this, getString(R.string.pdf_read_warning), Toast.LENGTH_LONG).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            dialog.setMessage(getApplicationContext().getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getApplicationContext().getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    finish();
                    //get gps
                }
            });
            dialog.setNegativeButton(getApplicationContext().getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    finish();
                    mainActivity.disableLocation=true;
                }
            });
            dialog.show();

        }else {




            pdfViewer= (PDFView) findViewById(R.id.pdfView);
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


            Thread th = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            gpsOpened=true;
                            return;
                        }else {
                            gpsOpened=false;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            th.start();
            String oneActivatePdfFileCode;
            Intent iin= getIntent();
            Bundle bundle = iin.getExtras();
            if(bundle!=null)
            {
                oneActivatePdfFileCode=(String) bundle.get("oneActivatePdfFileCode");
                data.put("kod",oneActivatePdfFileCode);
                data.put("musteriId","3");
                data.put("lat", String.valueOf(mainActivity.getMainLat));
                data.put("lng",String.valueOf(mainActivity.getMainLng));
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/DerinBilgiPdf/"+oneActivatePdfFileCode+".pdf");
                pdfViewer.fromFile(file).load();
                Thread wait=new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            sendLog(value,data);
                            System.out.println("Log on way ...."+" "+ mainActivity.getMainLat + " " + mainActivity.getMainLng);
                        }catch (Exception ex){
                            ex.printStackTrace();

                        }finally {

                        }
                    }
                };
                wait.start();

            }

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if(getParentActivityIntent() == null) {
                    onBackPressed();
                    finish();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendLog(String value,Map<String, String> catalogCodeMap) {

        Call<Void> call = myApi.sendLog(value,catalogCodeMap);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("responseLOG :", "Log Sended ...");
                    Log.d("responseLOG : ", " "  /*logMessage*/);
                } else {
                    Log.d("responseLOG :", "Log Failed");

                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("responseLOG :", "onFailure ");
                Log.d("responseLOG :", "onFailure " + t.getMessage());
                Log.d("responseLOG :", "onFailure " + t.getCause());

            }
        });
    }


    public Boolean checkGPS()
    {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return gps_enabled;
        } catch(Exception ex) {}
        return null;
    }
    public Boolean checkNetwork()
    {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return network_enabled;
        } catch(Exception ex) {}
        return null;
    }


}
