package red5pro.org.testandroidproject.tests.SubscribeImageTest;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class SubscribeImageTest extends SubscribeTest {
    ImageView screenShot;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        display.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onSubscribeTouch(event);
            }
        });
    }

    private boolean onSubscribeTouch( MotionEvent e ){

        if( e.getAction() == MotionEvent.ACTION_DOWN ){
            if( screenShot != null ){
                ((FrameLayout)display.getParent()).removeView(screenShot);
            }
            screenShot = new ImageView( display.getContext() );
            FrameLayout.LayoutParams position = new FrameLayout.LayoutParams( display.getWidth()/2, display.getHeight()/2 );
            position.setMargins( display.getWidth()/2, display.getHeight()/2, 0, 0 );
            screenShot.setLayoutParams(position);

            screenShot.setScaleType(ImageView.ScaleType.FIT_CENTER);
            screenShot.setImageBitmap(subscribe.getStreamImage());

            ((FrameLayout)display.getParent()).addView(screenShot);
        }

        return true;
    }
}
