package sphelele.getuserlocation.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import sphelele.getuserlocation.apputils.App;
import sphelele.getuserlocation.apputils.AppSession;
import sphelele.getuserlocation.apputils.AppUtils;
import sphelele.getuserlocation.apputils.CommonData;

import static sphelele.getuserlocation.apputils.App.CHANNEL_ID;

public class AppLocationService extends IntentService {

    private static final String TAG = "@LocationService";

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;

    public AppLocationService() {
        super("LocationService");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //Initialise FusedLocationProviderClient
        client = LocationServices.getFusedLocationProviderClient(App.getInstance().getApplicationContext());

        //Set Notification when the service is called
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Location")
                    .setContentText("Getting your location...")
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .build();
            startForeground(10112, notification);
        }

        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);

                if(result!=null){//NullPointer check
                    Log.i(TAG,"onLocationResult : " + result.getLastLocation().getLatitude()+","+result.getLastLocation().getLongitude());

                    //Save user latitude
                    AppSession.getInstance(App.getInstance().getApplicationContext()).setPreferenceString(CommonData.AppEnum.LATITUDE.toString(),
                            String.valueOf(result.getLastLocation().getLatitude()));

                    //Save user longitude
                    AppSession.getInstance(App.getInstance().getApplicationContext()).setPreferenceString(CommonData.AppEnum.LONGITUDE.toString(),
                            String.valueOf(result.getLastLocation().getLongitude()));

                    //Save user address
                    AppSession.getInstance(App.getInstance().getApplicationContext()).setPreferenceString(CommonData.AppEnum.ADDRESS.toString(),
                            AppUtils.getLocationAddress(App.getInstance().getApplicationContext(),result.getLastLocation().getLatitude(),result.getLastLocation().getLongitude()));

                    //Send event LOCATION_BROADCAST_DONE_ACTION
                    Intent intent_1 = new Intent(CommonData.AppEnum.LOCATION_BROADCAST_DONE_ACTION.toString());
                    sendBroadcast(intent_1);
                    client.removeLocationUpdates(this);
                    stopSelf();//Stop Service on results returned
                }


            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.d(TAG,"onLocationAvailability : " + locationAvailability.isLocationAvailable());
            }
        };

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Send event LOCATION_BROADCAST_START
        Intent intent_1 = new Intent(CommonData.AppEnum.LOCATION_BROADCAST_START_ACTION.toString());
        sendBroadcast(intent_1);

        Log.d(TAG,"onHandleIntent Running");

        for (int i=0;i<10;i++){//For loop represents a long running task

            Log.d(TAG,"onHandleIntent : " + i);
            SystemClock.sleep(1000);
        }

        Log.d(TAG,"onHandleIntent Done Running");

        //Request location update
        requestLocationUpdate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Send event LOCATION_BROADCAST_STOP
        Intent intent_1 = new Intent(CommonData.AppEnum.LOCATION_BROADCAST_STOP_ACTION.toString());
        sendBroadcast(intent_1);
        Log.d(TAG,"onDestroy Running");
    }

    private void requestLocationUpdate(){
        Log.d(TAG,"requestLocationUpdate");

        //Perform location request
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
