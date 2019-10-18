package sphelele.getuserlocation.apputils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    //http://www.androiddeft.com/android-uploading-file-to-server-with-progress-bar/

    public static final String CHANNEL_ID="SyncLocationService";
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        createNotification();
        //RxJavaPlugins.setErrorHandler(throwable -> Log.d("@Application", "Rx Error" + throwable.getMessage()));

    }

    public static App getInstance(){

        return instance;
    }

    private void createNotification(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID, "Sync Location", NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager!=null){
                manager.createNotificationChannel(channel);
            }

        }
    }
}
