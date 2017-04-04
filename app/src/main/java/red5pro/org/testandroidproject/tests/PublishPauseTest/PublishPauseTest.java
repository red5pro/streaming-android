package red5pro.org.testandroidproject.tests.PublishPauseTest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

/**
 * Created by toddanderson on 4/3/17.
 */

public class PublishPauseTest extends PublishTest {

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

    private void postNotification (String message) {

        final String str = message;
        final Context context = this.preview.getContext();
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CharSequence text = str;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();


            }
        });

    }

    private boolean onSubscribeTouch( MotionEvent e ){

        if(e.getAction() == MotionEvent.ACTION_DOWN ) {
            boolean hasAudio = !this.publish.getAudioPaused();
            boolean hasVideo = !this.publish.getVideoPaused();

            if (hasAudio && hasVideo) {
                this.publish.setAudioPaused(true);
                this.publish.setVideoPaused(false);
                postNotification("Pausing Audio");

            }
            else if (hasVideo && !hasAudio) {
                this.publish.setAudioPaused(false);
                this.publish.setVideoPaused(true);
                postNotification("Pausing Video");
            }
            else if (!hasVideo && hasAudio) {
                this.publish.setAudioPaused(true);
                this.publish.setVideoPaused(true);
                postNotification("Pausing Audio/Video");
            }
            else {
                this.publish.setAudioPaused(false);
                this.publish.setVideoPaused(false);
                postNotification("Resuming Audio/Video");
            }

        }

        return true;
    }

}
