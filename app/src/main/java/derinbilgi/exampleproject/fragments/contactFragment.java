package derinbilgi.exampleproject.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import derinbilgi.exampleproject.MyApi;
import derinbilgi.exampleproject.R;
import derinbilgi.exampleproject.activities.mainActivity;
import derinbilgi.exampleproject.models.shopArrayModel;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;


public class contactFragment extends Fragment {
    double Longitude = 0, Latitude = 0;
    private LocationManager locationManager;
    private LocationListener listener;
    private Location location;
    MapView mMapView;
    private GoogleMap googleMap;
    private MyApi myApi;
    String value;
    int firstChangeLocation = 0;
    private Retrofit retrofit;


    public contactFragment() {
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
        value = getString(R.string.key);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApi.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        myApi = retrofit.create(MyApi.class);
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Longitude = location.getLongitude();
                Latitude = location.getLatitude();
                mainActivity.getMainLat = location.getLatitude();
                mainActivity.getMainLng = location.getLongitude();
                firstChangeLocation++;
                System.out.println("lat : " + Latitude + "\n" + "Long :" + Longitude);
                if (firstChangeLocation == 1) {
                    LatLng currentLocation = new LatLng(Latitude, Longitude);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
               /* Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);*/
              /*  Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
                intent.putExtra("enabled", true);
                startActivity(intent);*/
                /*turnGPSOn();*/
                /*LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                if(!gps_enabled && !network_enabled) {
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setMessage(getContext().getResources().getString(R.string.gps_network_not_enabled));
                    dialog.setPositiveButton(getContext().getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton(getContext().getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub

                        }
                    });
                    dialog.show();
                }*/

            }
        };
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                } catch (Exception ex) {

                } finally {

                }
            }
        };
        th.start();
        System.out.println("lat : " + Latitude + "\n" + "Long :" + Longitude);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                getShopInformation(value);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Criteria criteria = new Criteria();
                    //get last location ... you can use for first time location information...
                    location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                    if (location != null) {
                        LatLng latLang = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 9);
                        googleMap.animateCamera(cameraUpdate);
                    }
                }


            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void getShopInformation(String value) {
        final ArrayList<String> shopNames = new ArrayList<>();
        final ArrayList<String> Lats = new ArrayList<>();
        final ArrayList<String> Lngs = new ArrayList<>();

        Call<shopArrayModel> call = myApi.getShopInformation(value);
        call.enqueue(new Callback<shopArrayModel>() {

            @Override
            public void onResponse(Call<shopArrayModel> call, Response<shopArrayModel> response) {
                if (response.isSuccessful()) {
                    Criteria criteria = new Criteria();
                    Log.d("Contact Side", "Success");
                    ArrayList<Double> distances=new ArrayList<Double>();
                    Double nearestDistance;
                    for (int i = 0; i < response.body().getShopsModelList().size(); i++) {
                        String shopName = response.body().getShopsModelList().get(i).getShopName();
                        String lat = response.body().getShopsModelList().get(i).getLat();
                        String lng = response.body().getShopsModelList().get(i).getLng();
                        shopNames.add(shopName);
                        Lats.add(lat);
                        Lngs.add(lng);
                        System.out.println("Mağazalar : " + shopName + " " + lat + " " + lng);

                        if (!(lat == null) || !(lng == null)) {
                            if (location != null) {
                                distances.add(distance(location.getLatitude(),location.getLongitude(),Double.valueOf(Lats.get(i)),Double.valueOf(Lngs.get(i))));
                            }
                        }
                    }
                    location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                    if (location != null) {
                    nearestDistance=distances.get(0);
                    for (int i = 0; i < distances.size(); i++) {
                        if (distances.get(i) < nearestDistance) {
                            nearestDistance = distances.get(i);
                        }
                    }
                    System.out.println("Nearest Distance : "+nearestDistance);

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //get last location ... you can use for first time location information...
                        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                        mainActivity.getMainLat=location.getLatitude();
                        mainActivity.getMainLng=location.getLongitude();

                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nearestmarker);

                            for (int j = 0; j < shopNames.size(); j++) {
                                if (!(Lats.get(j) == null) || !(Lngs.get(j) == null)) {
                                    LatLng shops = new LatLng(Double.valueOf(Lats.get(j)), Double.valueOf(Lngs.get(j)));

                                    if (distance(location.getLatitude(),location.getLongitude(),Double.valueOf(Lats.get(j)),Double.valueOf(Lngs.get(j)))==nearestDistance){
                                        googleMap.addMarker(new MarkerOptions().position(shops).title(shopNames.get(j)).snippet("En Yakın Konumdaki Gri Market")).setIcon(icon);
                                    }else {
                                        googleMap.addMarker(new MarkerOptions().position(shops).title(shopNames.get(j)).snippet("Gri Market"));
                                    }


                                }
                            }
                        }
                    }
                } else {
                    Log.d("responseShopInformation", "Failed");
                }
            }
            @Override
            public void onFailure(Call<shopArrayModel> call, Throwable t) {
                Log.d("responseShopInformation", "onFailure");
                Log.d("responseShopInformation", "onFailure" + t.getMessage());
                Log.d("responseShopInformation", "onFailure" + t.getCause());
            }
        });
    }
    //Calculate Distance...
    public double distance(Double latitude, Double longitude, double e, double f) {
        double d2r = Math.PI / 180;

        double dlong = (longitude - f) * d2r;
        double dlat = (latitude - e) * d2r;
        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(e * d2r)
                * Math.cos(latitude * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367 * c;
        return d;

    }



}
