package red5pro.org.testandroidproject.tests.PublishRemoteCallTest;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.util.Hashtable;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

/**
 * Created by davidHeimann on 4/25/16.
 */
public class PublishRemoteCallTest extends PublishTest implements R5ConnectionListener{

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        publish.setListener(this);
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        if(r5ConnectionEvent == R5ConnectionEvent.START_STREAMING ) {
            preview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Hashtable<String, String> map = new Hashtable<String, String>();
                        map.put("message", "The streamer wants your attention");
                        map.put("touchX", Float.toString(event.getRawX() / preview.getWidth()) );
                        map.put("touchY", Float.toString(event.getRawY() / preview.getHeight()) );

                        publish.send("whateverFunctionName", map);
                    }

                    return true;
                }
            });
        }
    }
}
