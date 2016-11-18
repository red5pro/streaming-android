package red5pro.org.testandroidproject.tests.PublishOrientationTest;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.red5pro.streaming.source.R5Camera;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class PublishOrientationTest extends PublishTest {

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onPublishTouch(event);
            }
        });
    }

    private boolean onPublishTouch( MotionEvent e ) {

        if( e.getAction() == MotionEvent.ACTION_UP  && publish != null) {
            R5Camera publishCam = (R5Camera)publish.getVideoSource();
            publishCam.setOrientation( publishCam.getOrientation() + 90 );
        }

        return true;
    }
}
