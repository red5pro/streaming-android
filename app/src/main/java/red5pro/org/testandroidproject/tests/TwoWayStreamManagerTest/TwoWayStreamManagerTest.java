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
package red5pro.org.testandroidproject.tests.TwoWayStreamManagerTest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.view.R5VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.TestContent;
import red5pro.org.testandroidproject.tests.TwoWayTest.TwoWayTest;

public class TwoWayStreamManagerTest extends TwoWayTest {

    protected boolean cleanUp = false;
    protected TextView publishEdgeShow;
    protected TextView subscribeEdgeShow;
    protected Thread callThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.twoway_test, container, false);

        preview = (R5VideoView)rootView.findViewById(R.id.videoPreview);
        display = (R5VideoView)rootView.findViewById(R.id.videoView);

        callForServer(TestContent.GetPropertyString("stream1"),"broadcast", new StreamStarter() {
            @Override
            public void passURL(String url) {
                publishToManager(url);
            }
        });
        makeListCall();

        return rootView;
    }

    private void callForServer(final String streamName, final String action, final StreamStarter starter){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    // start by delaying for a second just to be sure other activities
                    // are cleaned up before stream could run
                    Thread.sleep(1000);

                    //url format: https://{streammanagerhost}:{port}/streammanager/api/2.0/event/{scopeName}/{streamName}?action=broadcast
                    String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
                    String version = TestContent.GetPropertyString("sm_version");
                    String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
                    String url = protocol + "://" +
                            TestContent.GetPropertyString("host") + port + "/streammanager/api/" + version + "/event/" +
                            TestContent.GetPropertyString("context") + "/" + streamName + "?action=" + action;

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(new HttpGet(url));
                    StatusLine statusLine = response.getStatusLine();

                    if (statusLine.getStatusCode() == HttpStatus.SC_OK && !cleanUp) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        out.close();

                        JSONObject data = new JSONObject(responseString);
                        final String outURL = data.getString("serverAddress");

                        if( !outURL.isEmpty() ){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    starter.passURL(outURL);
                                }
                            });
                        }
                        else {
                            System.out.println("Server address not returned");
                        }
                    }
                    else{
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void publishToManager( String url ){
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                url,
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(getActivity().getPackageName());

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);
        publish.audioController.sampleRate =  TestContent.GetPropertyInt("sample_rate");

        publish.setListener(this);
        publish.client = this;

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        if(TestContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            attachMic();
        }

        preview.attachStream(publish);

        if(TestContent.GetPropertyBool("video_on"))
            attachCamera();

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.publish(TestContent.GetPropertyString("stream1"), getPublishRecordType());

        isPublishing = true;

        if(TestContent.GetPropertyBool("video_on"))
            cam.startPreview();

        publishEdgeShow = new TextView(preview.getContext());
        FrameLayout.LayoutParams position = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        publishEdgeShow.setLayoutParams(position);
        subscribeEdgeShow.setBackgroundColor(0x80ffffff);//translucent white, for the black text

        ((FrameLayout)preview.getParent()).addView(publishEdgeShow);

        publishEdgeShow.setText("Pub Connected to: " + url, TextView.BufferType.NORMAL);
        publishEdgeShow.setBackgroundColor(Color.LTGRAY);
    }

    private void makeListCall(){

        if(callThread != null) {
            callThread.interrupt();
            callThread = null;
        }

        callThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted() ) {
                    try {

                        String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
                        String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
                        String urlStr = protocol + "://" + TestContent.GetPropertyString("host") + port + "/streammanager/api/2.0/event/list";

                        URL url = new URL(urlStr);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        String responseString = "error: somehow string not assigned to?";
                        try {
                            if (urlConnection.getResponseCode() == 200 && !Thread.interrupted()) {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                StringBuilder stringBuilder = new StringBuilder();
                                String line;
                                while ((line = bufferedReader.readLine()) != null) {
                                    stringBuilder.append(line);
                                }
                                responseString = stringBuilder.toString().replaceAll("\\s+", "");
                                bufferedReader.close();
                            } else {
                                responseString = "error: http issue, response code - " + urlConnection.getResponseCode();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            urlConnection.disconnect();
                        }

                        if (!Thread.interrupted() && !cleanUp) {
                            if (!responseString.startsWith("error")) {
                                JSONArray list = new JSONArray(responseString);

                                for (int i = 0; i < list.length(); i++) {
                                    if (list.getJSONObject(i).getString("name").equals(TestContent.GetPropertyString("stream2"))) {
                                        callForServer(TestContent.GetPropertyString("stream2"), "subscribe", new StreamStarter() {
                                            @Override
                                            public void passURL(String url) {
                                                subscribeToManager(url);
                                            }
                                        });
                                        return;
                                    }
                                }
                            } else {
                                System.out.println(responseString);
                            }

                            Thread.sleep(1000);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        callThread.start();
    }

    private void subscribeToManager(String url){
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                url,
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("subscribe_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(getActivity().getPackageName());

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //find the view and attach the stream
        display.attachStream(subscribe);

        display.showDebugView(TestContent.GetPropertyBool("debug_view"));

        //Unlike basic subscription, two-way needs echo cancellation, which needs the subscriber and publisher
        //to use the same Audio Controller - instead of recreating it for stability, we delay the subscriber
        subscribe.play(TestContent.GetPropertyString("stream2"));

        subscribeEdgeShow = new TextView(display.getContext());
        FrameLayout.LayoutParams position = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subscribeEdgeShow.setLayoutParams(position);
        subscribeEdgeShow.setBackgroundColor(0x80ffffff);//translucent white, for the black text

        ((FrameLayout)display.getParent()).addView(subscribeEdgeShow);

        subscribeEdgeShow.setText("Sub Connected to: " + url, TextView.BufferType.NORMAL);
        subscribeEdgeShow.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onStop() {
        cleanUp = true;
        if(callThread != null){
            callThread.interrupt();
        }
        super.onStop();
    }

    private interface StreamStarter{
        void passURL(String url);
    }
}

