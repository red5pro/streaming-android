package red5pro.org.testandroidproject.tests.SubscribeAuthTest;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.media.R5AudioController;

import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by toddanderson on 4/5/17.
 */

public class SubscribeAuthTest extends SubscribeTest {

    public void Subscribe() {

        String auth = "username=" + TestContent.GetPropertyString("username") + ";";
        auth += "password=" + TestContent.GetPropertyString("password") + ";";

        //Create the configuration from the tests.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("subscribe_buffer_time"),
                auth);
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

}
