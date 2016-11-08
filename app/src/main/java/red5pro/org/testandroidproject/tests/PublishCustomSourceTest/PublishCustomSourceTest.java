package red5pro.org.testandroidproject.tests.PublishCustomSourceTest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 5/9/16.
 */
public class PublishCustomSourceTest extends PublishTest {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.publish_test, container, false);

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("buffer_time"));
        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        if(TestContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            R5Microphone mic = new R5Microphone();
            publish.attachMic(mic);
        }

        preview = (R5VideoView)rootView.findViewById(R.id.videoPreview);

        preview.attachStream(publish);

        CustomVideoSource source = new CustomVideoSource();
        publish.attachCamera(source);

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.publish(TestContent.GetPropertyString("stream1"), R5Stream.RecordType.Live);

        return rootView;
    }

    @Override
    public void onStop() {

        if (publish != null){

            publish.stop();
            publish = null;
        }

        super.onStop();
    }
}
