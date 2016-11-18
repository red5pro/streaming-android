package red5pro.org.testandroidproject.tests.SubscribeReconnectTest;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Camera;

import java.util.concurrent.ExecutionException;

import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class SubscribeReconnectTest extends SubscribeTest  {


    public boolean stopped = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        final Handler rootHandler = new Handler(Looper.myLooper());


        SetupListener();


        display.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onSubscribeTouch(event);
            }
        });

        return view;
    }


    public void SetupListener(){

        subscribe.setListener(new R5ConnectionListener() {
            @Override
            public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {


                final R5ConnectionListener me = this;

                if (r5ConnectionEvent == R5ConnectionEvent.CLOSE && !SubscribeReconnectTest.this.stopped) {


                    Handler h = new Handler(Looper.getMainLooper());
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            Subscribe();
                            SetupListener();

                        }
                    }, 5);

                }
            }
        });
    }



    private boolean onSubscribeTouch( MotionEvent e ) {

        if(e.getAction() == MotionEvent.ACTION_UP) {
            if (subscribe.getStreamMode() == R5Stream.StreamMode.Susbscribe) {
                subscribe.stop();
            }
        }

        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        stopped = true;
    }

}
