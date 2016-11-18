package red5pro.org.testandroidproject.tests.PublishImageTest;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class PublishImageTest extends PublishTest {
    ImageView screenShot;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onSubscribeTouch(event);
            }
        });
    }

    private boolean onSubscribeTouch( MotionEvent e ){

        if( e.getAction() == MotionEvent.ACTION_DOWN ){
            if( screenShot != null ){
                ((FrameLayout)preview.getParent()).removeView(screenShot);
            }
            screenShot = new ImageView( preview.getContext() );
            FrameLayout.LayoutParams position = new FrameLayout.LayoutParams( preview.getWidth()/2, preview.getHeight()/2 );
            position.setMargins(preview.getWidth() / 2, preview.getHeight() / 2, 0, 0);
            screenShot.setLayoutParams(position);

            screenShot.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Bitmap streamImage = publish.getStreamImage();
            Matrix manip = new Matrix();
            manip.setRotate(camOrientation);
            streamImage = Bitmap.createBitmap(streamImage, 0, 0, streamImage.getWidth(), streamImage.getHeight(), manip, true );
            manip.setScale( -1f, 1f );
            streamImage = Bitmap.createBitmap(streamImage, 0, 0, streamImage.getWidth(), streamImage.getHeight(), manip, true );

            screenShot.setImageBitmap(streamImage);

            ((FrameLayout)preview.getParent()).addView(screenShot);
        }

        return true;
    }
}
