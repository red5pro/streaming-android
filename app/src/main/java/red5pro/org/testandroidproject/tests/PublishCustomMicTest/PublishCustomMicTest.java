package red5pro.org.testandroidproject.tests.PublishCustomMicTest;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 12/22/17.
 */

public class PublishCustomMicTest extends PublishTest {

    @Override
    protected void publish(){
        String b = getActivity().getPackageName();

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
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

        if(TestContent.GetPropertyBool("video_on")) {
            //attach a camera video source
            cam = openFrontFacingCameraGingerbread();
            cam.setDisplayOrientation((camOrientation + 180) % 360);

            camera = new R5Camera(cam, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
            camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
            camera.setOrientation(camOrientation);
            camera.setFramerate(TestContent.GetPropertyInt("fps"));
        }

        if(TestContent.GetPropertyBool("audio_on")) {
            //attach the custom microphone
            R5Microphone mic = new gainWobbleMic();
            publish.attachMic(mic);
        }

        preview.attachStream(publish);

        if(TestContent.GetPropertyBool("video_on"))
            publish.attachCamera(camera);

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.setListener(this);
        publish.publish(TestContent.GetPropertyString("stream1"), R5Stream.RecordType.Live);

        if(TestContent.GetPropertyBool("video_on")) {
            cam.startPreview();
        }

    }

    public class gainWobbleMic extends R5Microphone {

        private float gain = 1.0f;
        private int mod = 1;
        private double lastTime = 0;

        @Override
        public void processData(byte[] samples, double streamtimeMill) {

            modifyGain(streamtimeMill - lastTime);
            lastTime = streamtimeMill;

            int s;
            for(int i = 0; i < samples.length; i++){

                 s = (int) (samples[i] * gain);
                 samples[i] = (byte) Math.min(s, 0xff);
            }

            super.processData(samples, streamtimeMill);
        }

        private void modifyGain(double time){
            //causes the gain to increase to double volume and decrease to 0 volume, then back
            gain += mod * (time/2000);
            if( gain >= 2 || gain <= 0 ){
                System.out.println("gain at: " + gain);
                gain = Math.max(2.0f * mod, 0.0f);
                mod *= -1;
            }
        }
    }
}
