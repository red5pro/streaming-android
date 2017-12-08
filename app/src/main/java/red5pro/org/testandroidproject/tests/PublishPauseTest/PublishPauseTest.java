package red5pro.org.testandroidproject.tests.PublishPauseTest;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.util.Hashtable;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

public class PublishPauseTest extends PublishTest {

    protected int muteEnum = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        publish.setListener(this);
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        super.onConnectionEvent(r5ConnectionEvent);
        if(r5ConnectionEvent == R5ConnectionEvent.START_STREAMING ) {
            preview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        muteEnum = muteEnum + 1;
                        if (muteEnum > 3) {
                            muteEnum = 0;
                        }

                        switch (muteEnum) {
                            case 1:
                                // mute audio
                                publish.restrainAudio(true);
                                publish.restrainVideo(false);
                                Log.d("PublisherPause", "Mute Audio");
                                break;
                            case 2:
                                // mute video
                                publish.restrainAudio(false);
                                publish.restrainVideo(true);
                                Log.d("PublisherPause", "Mute Video");
                                break;
                            case 3:
                                // mute audio & video
                                publish.restrainAudio(true);
                                publish.restrainVideo(true);
                                Log.d("PublisherPause", "Mute Audio & Video");
                                break;
                            case 0:
                                // unmute audio & video
                                publish.restrainAudio(false);
                                publish.restrainVideo(false);
                                Log.d("PublisherPause", "Umute all");
                                break;
                        }

                    }

                    return true;
                }
            });
        }
    }
}
