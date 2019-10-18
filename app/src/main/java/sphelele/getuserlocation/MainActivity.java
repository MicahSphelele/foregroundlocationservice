package sphelele.getuserlocation;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import sphelele.getuserlocation.apputils.AppSession;
import sphelele.getuserlocation.apputils.AppUtils;
import sphelele.getuserlocation.apputils.CommonData;
import sphelele.getuserlocation.broadcast.LocationBroadcast;
import sphelele.getuserlocation.services.AppLocationService;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationBroadcast.LocationBroadcastListener {

    private static final String TAG = "@MainActivity";

    private TextView txt_message;
    private ProgressBar progress;
    private ProgressBar progressBar;
    private Button btn;
    private Button btn_service;
    private TextView txt_coord;
    private TextView txt_address;
    private TextView txt_last_address;

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private boolean is_run_once=false;
    private boolean is_run=false;

    private LocationBroadcast locationBroadcast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise FusedLocationProviderClient
        client = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        txt_message = findViewById(R.id.txt_message);
        txt_message.setText(String.format("%s","Foreground Service currently not running."));
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);

        btn_service = findViewById(R.id.btn_service);
        btn_service.setOnClickListener(this);

        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        txt_coord=findViewById(R.id.txt_coord);
        txt_address=findViewById(R.id.txt_location);

        txt_last_address=findViewById(R.id.txt_last_address);
        txt_last_address.setOnClickListener(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager!=null){//NullPointer check

            boolean isProviderGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isProverNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isProviderGPS || isProverNetwork){//Check if providers are turned on
                Toast.makeText(this,"Location provider is turned on",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Location provider is turned off",Toast.LENGTH_LONG).show();
            }
        }

        //Initials the Location Callback
        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if(!is_run){//Only run this code the first time the callback runs
                    btn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    is_run=true;//When set to true block will not run
                }

                if(locationResult!=null){////NullPointer check

                    if(!is_run_once){//Only run this code the first time the callback runs
                        btn.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        is_run_once=true;//When set to true block will not run
                    }

                    Log.d(TAG,locationResult.getLastLocation().getLatitude()+","+locationResult.getLastLocation().getLongitude());

                    txt_coord.setText(String.format("%s , %s",locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude()));
                    txt_address.setText(AppUtils.getLocationAddress(MainActivity.this,locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude()));
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.d(TAG, "LocationAvailability = " + locationAvailability.isLocationAvailable());
            }
        };
    }

    @Override
    public void onClick(View v) {

        //GET LOCATION button click
        if(v==btn){
            Log.d(TAG,"onClick btn");
            if(AppUtils.isLocationPermissionEnabled(this)){

                Log.d(TAG,"isLocationPermissionEnabled = true");
                requestLocationUpdate();

            }else{

                Log.d(TAG,"isLocationPermissionEnabled = false");
                AppUtils.requestLocationPermission(this);

            }
            return;
        }

        //START LOCATION button click
        if(v==btn_service){
            Log.d(TAG,"onClick btn_service");
            if(!AppUtils.isLocationPermissionEnabled(this)){

                Log.d(TAG,"isLocationPermissionEnabled = false");
                Toast.makeText(this,"Run the Get location to activate permission first.",Toast.LENGTH_LONG).show();
                return;
            }
            Intent i = new Intent(MainActivity.this, AppLocationService.class);
            ContextCompat.startForegroundService(MainActivity.this, i);
            return;
        }

        //Get Last Saved Address text view click
        if(v==txt_last_address){
            if(!AppSession.getInstance(this).getPreferenceString(CommonData.AppEnum.ADDRESS.toString()).equals("")){
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this,R.style.MaterialThemeDialog);
                builder.setCancelable(true);
                builder.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_mylocation));
                builder.setTitle("Last Saved Address");
                builder.setMessage(AppSession.getInstance(this).getPreferenceString(CommonData.AppEnum.ADDRESS.toString()));

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.setPositiveButton("Show Maps", (dialog, which) -> {

                    String latitude=AppSession.getInstance(MainActivity.this).getPreferenceString(CommonData.AppEnum.LATITUDE.toString());
                    String longitude=AppSession.getInstance(MainActivity.this).getPreferenceString(CommonData.AppEnum.LONGITUDE.toString());
                    String address=AppSession.getInstance(MainActivity.this).getPreferenceString(CommonData.AppEnum.ADDRESS.toString());

                    String uri = String.format("geo:<%s>,<%s>?q=<%s>,<%s>(%s)",latitude,longitude,latitude,longitude,address);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                });

                builder.setNeutralButton("Clear Data", (dialog, which) -> {
                    //Clear user latitude
                    AppSession.getInstance(MainActivity.this).clearPreferenceString(CommonData.AppEnum.LATITUDE.toString());

                    //Clear user longitude
                    AppSession.getInstance(MainActivity.this).clearPreferenceString(CommonData.AppEnum.LONGITUDE.toString());

                    //Save user address
                    AppSession.getInstance(MainActivity.this).clearPreferenceString(CommonData.AppEnum.ADDRESS.toString());

                });
                builder.show();
            }else{
                Toast.makeText(this,"No Last address saved available.",Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Remove location updates
        if(client!=null){//NullPointer check
            client.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Register the receiver
        locationBroadcast=new LocationBroadcast(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CommonData.AppEnum.LOCATION_BROADCAST_START_ACTION.toString());
        intentFilter.addAction(CommonData.AppEnum.LOCATION_BROADCAST_STOP_ACTION.toString());
        intentFilter.addAction(CommonData.AppEnum.LOCATION_BROADCAST_DONE_ACTION.toString());
        registerReceiver(locationBroadcast, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Kill the receiver
        if(locationBroadcast!=null){//NullPointer check
            unregisterReceiver(locationBroadcast);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Return location request results
        if(requestCode==AppUtils.LOCATION_REQUEST){

            if(grantResults.length>0){

                boolean is_accepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;

                if(is_accepted){
                    Toast.makeText(getApplicationContext(), "Location permission granted.", Toast.LENGTH_SHORT).show();
                    requestLocationUpdate();
                }else{

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
                            showPermissionMessageDialog((dialog, which) ->  requestPermissions(new String[]{ACCESS_FINE_LOCATION}, AppUtils.LOCATION_REQUEST));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void isForegroundServiceRunning(String state) {
        //Check Foreground service state
        if(state.equals(LocationBroadcast.State.STARTED_AND_WAITING.toString())){
            txt_message.setText(String.format("%s","Foreground Service is running..."));
            progress.setVisibility(View.VISIBLE);
        }

        //Check Foreground service state
        if(state.equals(LocationBroadcast.State.STOPPED_AND_WAITING.toString())){
            txt_message.setText(String.format("%s","Foreground Service has stopped, waiting to capture address..."));
        }

        //Check Foreground service state
        if(state.equals(LocationBroadcast.State.DONE.toString())){

            if(!AppSession.getInstance(MainActivity.this).getPreferenceString(CommonData.AppEnum.ADDRESS.toString()).equals("")){
                txt_message.setText(String.format("%s","Done capturing address, check it out."));
                progress.setVisibility(View.GONE);
            }else{
                txt_message.setText(String.format("%s","Unable to capture your adress please try again."));
                progress.setVisibility(View.GONE);
            }
        }
    }

    private void showPermissionMessageDialog(DialogInterface.OnClickListener listener){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this,R.style.MaterialThemeDialog);
        builder.setCancelable(true);
        builder.setIcon(getResources().getDrawable(R.mipmap.ic_launcher));
        builder.setTitle("Location Permission Required");
        builder.setMessage("You need to allow the app access to make location request or turn the location off from the app settings.");

        builder.setNegativeButton("OK",listener);

        builder.setPositiveButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();

    }

    //Used to get last location used on Google Maps
    /*private void getLastKnownLocation(){
        btn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        client.getLastLocation().addOnSuccessListener(location -> {
            btn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if(location!=null){
                txt_coord.setText(String.format("%s , %s",location.getLatitude(),location.getLongitude()));
                txt_address.setText(AppUtils.getLocationAddress(this,location.getLatitude(),location.getLongitude()));
            }else{
                txt_coord.setText(String.format("%s","Unable to get your location"));
                btn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }*/

    //Used if you want to get location updates
    private void requestLocationUpdate(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }


}
