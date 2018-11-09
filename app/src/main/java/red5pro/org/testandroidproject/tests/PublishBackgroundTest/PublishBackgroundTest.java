package red5pro.org.testandroidproject.tests.PublishBackgroundTest;

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

public class PublishBackgroundTest extends TestDetailFragment {
    private boolean shouldClean = false;
    private R5VideoView preview;
    private Intent pubIntent;
    private PublishService pubService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bg_publish_test, container, false);

        //find the view and attach the stream
        preview = (R5VideoView) view.findViewById(R.id.videoView);

        //Bind to service - will create if doesn't exist
        pubIntent = new Intent(getActivity(), PublishService.class);
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
                if (serviceInfo.service.getClassName().equals(PublishService.class.getName())) {
                    found = true;
                }
            }
        }catch (NullPointerException e){}

        if(!found){
            getActivity().startService(pubIntent);
        }

        getActivity().bindService(pubIntent, pubServiceConnection, Context.BIND_IMPORTANT);
    }

    private ServiceConnection pubServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pubService = ((PublishService.PublishServiceBinder)service).getService();

            pubService.setDisplay(preview);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            pubService = null;
        }
    };

    @Override
    public void onResume() {
        if(pubService != null)
            pubService.setDisplayOn(true);
        else
            detectToStartService();

        super.onResume();
    }

    @Override
    public void onStop() {

        if(shouldClean()) {
            if(pubService != null) {
                getActivity().unbindService(pubServiceConnection);
                pubService = null;
            }
            getActivity().stopService(pubIntent);
        }

        if(pubService != null) {
            pubService.setDisplayOn(false);
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(pubService != null){
            getActivity().unbindService(pubServiceConnection);
            pubService = null;
        }
        getActivity().stopService(pubIntent);

        super.onDestroy();
    }

    @Override
    public Boolean shouldClean() {
        return shouldClean;
    }
}
