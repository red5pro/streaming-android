package red5pro.org.testandroidproject.tests.SubscribeTest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;
import red5pro.org.testandroidproject.tests.SubscribeNoViewTest.SubscribeNoViewTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/9/16.
 */
public class SubscribeTest extends TestDetailFragment implements R5ConnectionListener {
    protected R5VideoView display;
    protected R5Stream subscribe;

    @Override
    public void onConnectionEvent(R5ConnectionEvent event) {
        Log.d("Subscriber", ":onConnectionEvent " + event.name());
        if (event.name() == R5ConnectionEvent.LICENSE_ERROR.name()) {
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog alertDialog = new AlertDialog.Builder(SubscribeTest.this.getActivity()).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("License is Invalid");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK",
                            new DialogInterface.OnClickListener()

                            {
                                public void onClick (DialogInterface dialog,int which){
                                    dialog.dismiss();
                                }
                            }

                    );
                    alertDialog.show();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.subscribe_test, container, false);

        //find the view and attach the stream
        display = (R5VideoView) view.findViewById(R.id.videoView);

        Subscribe();

        return view;
    }

    public void Subscribe(){

        //Create the configuration from the tests.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("subscribe_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(getActivity().getPackageName());

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);

        subscribe.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        subscribe.client = this;
        subscribe.setListener(this);

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //display.setZOrderOnTop(true);
        display.attachStream(subscribe);

        display.showDebugView(TestContent.GetPropertyBool("debug_view"));

        subscribe.audioController = new R5AudioController();

        subscribe.play(TestContent.GetPropertyString("stream1"));

    }

    protected void updateOrientation(int value) {
        value += 90;
        Log.d("SubscribeTest", "update orientation to: " + value);
        display.setStreamRotation(value);
    }

    public void onMetaData(String metadata) {
        Log.d("SubscribeTest", "Metadata receieved: " + metadata);
        String[] props = metadata.split(";");
        for (String s : props) {
            String[] kv = s.split("=");
            if (kv[0].equalsIgnoreCase("orientation")) {
                updateOrientation(Integer.parseInt(kv[1]));
            }
        }
    }

    public void onStreamSend(String msg){
        Log.d("SubscribeTest", "GOT MSG");
    }

    @Override
    public void onStop() {
        if(subscribe != null) {
            subscribe.stop();
        }

        super.onStop();
    }
}
