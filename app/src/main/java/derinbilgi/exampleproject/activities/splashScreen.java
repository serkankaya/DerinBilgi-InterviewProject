package derinbilgi.exampleproject.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import derinbilgi.exampleproject.R;

public class splashScreen extends AppCompatActivity {
    private boolean grantAll= false;
    private static final int REQUEST_MULTIPLE = 1;

    private String[] permissons = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WAKE_LOCK
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (checkAllPermission())
            {
                if (checkInternetConnection())
                {
                    startMain();
                }else {
                    alertInternet(this);
                }
            }else
            {
                askPermission(null);
            }
        }else
        {
            if (checkInternetConnection())
            {
                startMain();
            }else {
                alertInternet(this);
                }

        }

    }
    private boolean checkAllPermission()
    {
        grantAll=true;
        for (String permisson : permissons) {
            if (ContextCompat.checkSelfPermission(this, permisson) != PackageManager.PERMISSION_GRANTED) {
                grantAll = false;
                break;
            }
        }
        return grantAll;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_MULTIPLE)
        {
            grantAll=true;
            for (int i = 0; i < permissions.length ; i++) {
                if( grantResults[i] != PackageManager.PERMISSION_GRANTED)
                {
                    grantAll = false;
                    break;
                }
            }
            if(grantAll)
            {
                if (checkInternetConnection())
                {
                    startMain();
                }else {
                    alertInternet(splashScreen.this);
                }

            }else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(splashScreen.this);
                builder.setTitle(getString(R.string.warning));
                builder.setMessage(getString(R.string.givepermission));
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        askPermission(null);

                    }
                });
                builder.show();
            }
        }
    }
    public void askPermission(View view) {
        ActivityCompat.requestPermissions(this,permissons,REQUEST_MULTIPLE);
    }
    private void startMain()
    {

                startActivity(new Intent(this,mainActivity.class));
                finish();

    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    public void alertInternet(Activity activity){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setTitle(getString(R.string.err_internet_connection_title));
        alertDialogBuilder
                .setMessage(getString(R.string.err_internet_connection))
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                                onDestroy();
                                finish();
                                System.exit(0);
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
