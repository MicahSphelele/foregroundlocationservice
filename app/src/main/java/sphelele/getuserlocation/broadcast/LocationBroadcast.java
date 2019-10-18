package sphelele.getuserlocation.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import sphelele.getuserlocation.apputils.CommonData;

public class LocationBroadcast extends BroadcastReceiver {

    private static final String TAG = "@LocationBroadcast";
    private LocationBroadcastListener listener;

    public LocationBroadcast(LocationBroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive");

        //Get the action from the intent
        String action=intent.getAction();

        if(action!=null){

            if(action.equals(CommonData.AppEnum.LOCATION_BROADCAST_START_ACTION.toString())){
                listener.isForegroundServiceRunning(State.STARTED_AND_WAITING.toString());
                Log.d(TAG,"LOCATION_BROADCAST_START_ACTION");
            }

            if(action.equals(CommonData.AppEnum.LOCATION_BROADCAST_STOP_ACTION.toString())){
                listener.isForegroundServiceRunning(State.STOPPED_AND_WAITING.toString());
                Log.d(TAG,"LOCATION_BROADCAST_STOP_ACTION");
            }

            if(action.equals(CommonData.AppEnum.LOCATION_BROADCAST_DONE_ACTION.toString())){
                listener.isForegroundServiceRunning(State.DONE.toString());
                Log.d(TAG,"LOCATION_BROADCAST_DONE_ACTION");
            }

        }
    }

    //Interface to capture the events
    public interface LocationBroadcastListener{

        void isForegroundServiceRunning(String  state);

    }

    //LocationBroadcast Enum data
    public enum State{
        STARTED_AND_WAITING("started_and_waiting"),
        STOPPED_AND_WAITING("stopped_and_waiting"),
        DONE("done");

        private final String text;


        State(final String text) {
            this.text = text;
        }


        @SuppressWarnings("NullableProblems")
        @Override
        public String toString() {
            return text;
        }
    }
}
