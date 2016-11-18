package red5pro.org.testandroidproject.tests.SubscribeAspectTest;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.red5pro.streaming.source.R5Camera;

import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class SubscribeAspectTest extends SubscribeTest {

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        display.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onSubscribeTouch(event);
            }
        });
    }

    private boolean onSubscribeTouch( MotionEvent e ) {

        if( e.getAction() == MotionEvent.ACTION_DOWN ) {
            int sMode = subscribe.getScaleMode();

            sMode++;
            if(sMode == 3) sMode = 0;

            subscribe.setScaleMode(sMode);
        }

        return true;
    }
}
