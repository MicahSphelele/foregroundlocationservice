package sphelele.getuserlocation.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

public class NetworkBroadcast extends BroadcastReceiver {

    private static final String TAG = "@NetworkBroadcast";
    private NetworkListener listener;

    public NetworkBroadcast(NetworkListener listener){
        this.listener=listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NETWORK","RECEIVER STARTED.");
        String action=intent.getAction();
        final ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                }
            };
            //connectivityManager.re
        }else{
            NetworkInfo net_info=null;
            if(connectivityManager!=null){
                net_info=connectivityManager.getActiveNetworkInfo();
            }

            if(action!=null){
                if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
                    if(net_info==null){//If it is in airplane mode
                        listener.onNetworkChange(false);
                        Log.d(TAG,"NETWORK ON: AIRPLANE.");
                    }else if(net_info.isConnected()){
                        listener.onNetworkChange(true);
                        Log.d(TAG,"NETWORK ON: IS CONNECTED.");
                    }else {
                        listener.onNetworkChange(false);
                        Log.d(TAG,"NETWORK ON: IS NOT CONNECTED.");
                    }
                }
            }
        }


    }

    public interface NetworkListener{

         void onNetworkChange(boolean isEnabled);
    }
}
