package red5pro.org.testandroidproject.tests.PublishLocalRecordTest;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

/**
 * Created by davidHeimann on 7/28/17.
 */

public class PublishLocalRecordTest extends PublishTest {

    @Override
    protected void publish() {
        super.publish();

        publish.beginLocalRecording(getActivity().getApplicationContext(), "testRecord");
    }
}
