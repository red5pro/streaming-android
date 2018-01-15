package red5pro.org.testandroidproject.tests.SubscribeBackgroundTest;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 12/6/17.
 */

public class SubscribeService extends Service {

    private R5Stream subscribe;
    private R5VideoView display;
    private Notification holderNote;
    private final SubscribeServiceBinder mBinder = new SubscribeServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        startSubscribe();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void startSubscribe(){

        if( subscribe != null){
            subscribe.stop();
        }

        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("subscribe_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID( getPackageName() );

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        if(display != null) {
            attachDisplay();
        }

        subscribe.audioController = new R5AudioController();
        subscribe.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        subscribe.play(TestContent.GetPropertyString("stream1"));
    }

    private void attachDisplay(){
        display.attachStream(subscribe);
        display.showDebugView(TestContent.GetPropertyBool("debug_view"));
    }

    public void setDisplay( R5VideoView view ){
        if(view == null){
            return;
        }
        else {

            display = view;

            if (subscribe != null) {
                attachDisplay();
            } else startSubscribe();
        }
    }

    public void setDisplayOn(boolean setOn){

        if(!setOn){
            subscribe.deactivate_display();

            if(holderNote == null){
                holderNote = (new Notification.Builder(getApplicationContext()))
                        .setContentTitle("R5Testbed")
                        .setContentText("Streaming from the background")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .build();
                startForeground(7335776, holderNote);
            }
        }
        else {
            if(holderNote != null){
                stopForeground(true);
                holderNote = null;
            }

            subscribe.activate_display();
        }
    }

    @Override
    public void onDestroy() {

        if(holderNote != null) {
            stopForeground(true);
            holderNote = null;
        }

        if(subscribe != null){
            subscribe.stop();
        }

        super.onDestroy();
    }

    class SubscribeServiceBinder extends Binder {
        SubscribeService getService(){
            return SubscribeService.this;
        }
    }
}
