package sphelele.getuserlocation.apputils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppSession {

    private SharedPreferences preferences;

    private AppSession(Context context){

        preferences=context.getSharedPreferences("getuserlocation_session", Context.MODE_PRIVATE);
    }

    public static synchronized AppSession getInstance(Context context){
        AppSession instance=null;
        //noinspection ConstantConditions
        if(instance==null){
            instance=new AppSession(context);
        }

        return instance;
    }

    public void setPreferenceString(String key, String value){
        Log.d("@LocationService","Writing to getuserlocation_session : " + value + " ,Key : " + key);
        this.preferences.edit().putString(key, value).apply();
    }

    public String getPreferenceString(String key){
        return this.preferences.getString(key,"");
    }

    public void clearPreferenceString(String key){
        Log.d("@LocationService","Clearing from getuserlocation_session,  Key : " + key);
        this.preferences.edit().putString(key, "").apply();
    }

}
