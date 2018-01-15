package red5pro.org.testandroidproject.tests.SubscribeBackgroundTest;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;

/**
 * Created by davidHeimann on 12/6/17.
 */

public class SubscribeBackgroundTest extends TestDetailFragment {

    private boolean shouldClean = false;
    private R5VideoView display;
    private Intent subIntent;
    private SubscribeService subService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bg_subscribe_test, container, false);

        //find the view and attach the stream
        display = (R5VideoView) view.findViewById(R.id.videoView);

        //Bind to service - will create if doesn't exist
        subIntent = new Intent(getActivity(), SubscribeService.class);
        detectToStartService();

        Button endButton = (Button) view.findViewById(R.id.endButton);
        endButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( event.getAction() == MotionEvent.ACTION_UP ){
                    shouldClean = true;
                    getActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void detectToStartService(){
        boolean found = false;
        ActivityManager actManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        try {
            for (ActivityManager.RunningServiceInfo serviceInfo : actManager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceInfo.service.getClassName().equals(SubscribeService.class.getName())) {
                    found = true;
                }
            }
        }catch (NullPointerException e){}

        if(!found){
            getActivity().startService(subIntent);
        }

        getActivity().bindService(subIntent, subServiceConnection, Context.BIND_IMPORTANT);
    }

    private ServiceConnection subServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            subService = ((SubscribeService.SubscribeServiceBinder)service).getService();

            subService.setDisplay(display);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            subService = null;
        }
    };

    @Override
    public void onResume() {
        if(subService != null)
        {
            subService.setDisplayOn(true);
        }
        super.onResume();
    }

    @Override
    public void onStop() {

        if(subService != null) {
            subService.setDisplayOn(false);
        }

        if(shouldClean()) {
            getActivity().unbindService(subServiceConnection);
            getActivity().stopService(subIntent);
            subService = null;
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(subService != null){
            getActivity().unbindService(subServiceConnection);
            getActivity().stopService(subIntent);
            subService = null;
        }

        super.onDestroy();
    }

    @Override
    public Boolean shouldClean() {
        return shouldClean;
    }
}
