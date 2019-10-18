package sphelele.getuserlocation.apputils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AppUtils {

    public static final int LOCATION_REQUEST=1001;

    //Check if the app has location permission is enabled
    public static boolean isLocationPermissionEnabled(Context context){

        return (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    //Request user location
    public static void requestLocationPermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
    }

    //Get user address
    public static String getLocationAddress(Context context, double latitude,double longitude){
        Geocoder geocoder;
        List<Address> addressList=null;
        geocoder =new Geocoder(context, Locale.getDefault());

        String address="";

        try{
            addressList=geocoder.getFromLocation(latitude,longitude, 1);
        }catch (IOException e){
            e.printStackTrace();
        }

        if(addressList!=null){
            address =  addressList.get(0).getAddressLine(0);
        }

        return address;
    }


}
