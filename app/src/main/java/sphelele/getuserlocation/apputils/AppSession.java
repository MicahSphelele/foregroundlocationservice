package sphelele.getuserlocation.apputils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppSession {

    private SharedPreferences preferences;
    private static AppSession instance=null;

    private AppSession(Context context){

        preferences = context.getSharedPreferences("getuserlocation_session", Context.MODE_PRIVATE);
        if(instance!=null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static synchronized AppSession getInstance(Context context){


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
