package red5pro.org.testandroidproject.tests.PublishLocalRecordTest;

import com.red5pro.streaming.R5Stream;

import java.util.HashMap;
import java.util.Map;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 7/28/17.
 */

public class PublishLocalRecordTest extends PublishTest {

    @Override
    protected void publish() {
        super.publish();

        Map<String, Integer> props = new HashMap<>();
        props.put(R5Stream.VideoBitrateKey, TestContent.GetPropertyInt("bitrate") * 2);
        props.put(R5Stream.AudioBitrateKey, publish.getAudioSource().getBitRate() * 2);

        publish.beginLocalRecordingWithProperties(getActivity().getApplicationContext(), "r5pro/testRecord", props);
    }
}
