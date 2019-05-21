package red5pro.org.testandroidproject.tests.PublishEncryptedTest;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

public class PublishEncryptedTest extends PublishTest {

    @Override
    protected void publish(){

        String b = getActivity().getPackageName();

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.SRTP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(b);

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);

        publish.audioController.sampleRate =  TestContent.GetPropertyInt("sample_rate");

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        if(TestContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            attachMic();
        }

        preview.attachStream(publish);

        if(TestContent.GetPropertyBool("video_on")) {
            //attach a camera video source
            attachCamera();
        }

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.setListener(this);
        publish.publish(TestContent.GetPropertyString("stream1"), getPublishRecordType());

        if(TestContent.GetPropertyBool("video_on")) {
            cam.startPreview();
        }
    }
}
