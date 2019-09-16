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
package red5pro.org.testandroidproject.tests.SubscribeTwoStreamTest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 4/13/16.
 */
public class SubscribeTwoStreamTest extends SubscribeTest {
    protected R5Stream secondSubscribe;
    protected R5VideoView secondDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.double_view, container, false);

        //Create the configuration from the tests.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("subscribe_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(getActivity().getPackageName());

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);
        subscribe.setListener(this);

        subscribe.audioController = new R5AudioController();
        subscribe.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //find the view and attach the stream
        display = (R5VideoView) view.findViewById(R.id.videoView1);
        display.attachStream(subscribe);

        display.showDebugView(TestContent.GetPropertyString("debug_view").equals("true"));

        subscribe.play(TestContent.GetPropertyString("stream1"));


        secondDisplay = (R5VideoView) view.findViewById(R.id.videoView2);


        final R5ConnectionListener listener = this;
        final Handler root = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(4000);

                    Looper.prepare();

                    root.post(new Runnable() {
                        @Override
                        public void run() {
                            R5Configuration config2 = new R5Configuration(R5StreamProtocol.RTSP,
                                    TestContent.GetPropertyString("host"),
                                    TestContent.GetPropertyInt("port"),
                                    TestContent.GetPropertyString("context"),
                                    TestContent.GetPropertyFloat("subscribe_buffer_time"));
                            config2.setLicenseKey(TestContent.GetPropertyString("license_key"));
                            config2.setBundleID(getActivity().getPackageName());

                            R5Connection secondConnection = new R5Connection(config2);

                            secondSubscribe = new R5Stream(secondConnection);
                            secondSubscribe.setListener(listener);

                            secondDisplay.attachStream(secondSubscribe);
                            secondDisplay.showDebugView(TestContent.GetPropertyString("debug_view").equals("true"));

                            secondSubscribe.audioController = new R5AudioController();
                            secondSubscribe.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");
                            secondSubscribe.play(TestContent.GetPropertyString("stream2"));
                        }
                    });



                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }

    @Override
    public void onStop() {

        if(secondSubscribe != null)
            secondSubscribe.stop();

        super.onStop();
    }
}
