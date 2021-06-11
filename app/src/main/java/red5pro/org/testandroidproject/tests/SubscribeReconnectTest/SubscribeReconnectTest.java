//
// Copyright © 2015 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
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
import com.red5pro.streaming.view.R5VideoView;

import java.util.concurrent.ExecutionException;

import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class SubscribeReconnectTest extends SubscribeTest  {


    public boolean stopped = false;
    public int reconnectDelay = 5;

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

    protected void reconnect () {
        if(stopped) return;

        if (subscribe != null) {
            subscribe.removeListener();
        }
        Subscribe();
        SetupListener();
    }

    protected void delayReconnect (int delay) {

        final SubscribeReconnectTest subscribeTest = this;
        Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                subscribeTest.reconnect();
            }
        }, delay);

    }

    public void SetupListener(){

        final SubscribeReconnectTest subscribeTest = this;
        final R5ConnectionListener additionalListener = this;
        final R5Stream subscriber = this.subscribe;
        final R5VideoView view = this.display;

        subscribe.setListener(new R5ConnectionListener() {
            @Override
            public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

                final R5ConnectionListener me = this;
                additionalListener.onConnectionEvent(r5ConnectionEvent);

                if (r5ConnectionEvent == R5ConnectionEvent.CLOSE && !SubscribeReconnectTest.this.stopped) {

                    Handler h = new Handler(Looper.getMainLooper());
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(!stopped) {
                                reconnectDelay = 5;
                                subscribeTest.reconnect();
                            }

                        }
                    }, reconnectDelay);

                }
                else if (r5ConnectionEvent == R5ConnectionEvent.NET_STATUS && r5ConnectionEvent.message.equals("NetStream.Play.UnpublishNotify")) {

                    reconnectDelay = 1000;
                    Handler h = new Handler(Looper.getMainLooper());

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            subscriber.setListener(null);
                            subscriber.stop();
                            view.attachStream(null);
                            subscribeTest.delayReconnect(reconnectDelay);

                        }
                    }, reconnectDelay);

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
        subscribe.setListener(null);
        super.onStop();
        stopped = true;
    }

}
