package red5pro.org.testandroidproject.tests.PublishAspectTest;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

/**
 * Created by davidHeimann on 11/20/17.
 */

public class PublishAspectTest extends PublishTest {

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onPublishTouch(event);
            }
        });
    }

    private boolean onPublishTouch(MotionEvent e ) {

        if( e.getAction() == MotionEvent.ACTION_DOWN ) {
            int sMode = publish.getScaleMode();

            sMode++;
            if(sMode == 3) sMode = 0;

            publish.setScaleMode(sMode);
        }

        return true;
    }
}
