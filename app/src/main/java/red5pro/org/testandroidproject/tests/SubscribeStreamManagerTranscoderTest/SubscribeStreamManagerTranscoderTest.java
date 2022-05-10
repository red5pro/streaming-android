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
package red5pro.org.testandroidproject.tests.SubscribeStreamManagerTranscoderTest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.view.R5VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.SubscribeStreamManagerTest.SubscribeStreamManagerTest;
import red5pro.org.testandroidproject.tests.TestContent;

public class SubscribeStreamManagerTranscoderTest extends SubscribeStreamManagerTest {

    protected ViewGroup buttonContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.subscribe_sm_transcoder_test, container, false);

        display = (R5VideoView) view.findViewById(R.id.videoView);
        buttonContainer = (ViewGroup) view.findViewById(R.id.buttonContainer);

        requestProvisions(TestContent.GetPropertyString("stream1"));

        return view;

    }

    protected void requestProvisions (final String streamNameGUID) {
        final Context context = this.getActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //url format: https://{streammanagerhost}:{port}/streammanager/api/2.0/admin/event/meta/{scopeName}/{streamName}?action=subscribe
                    String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
                    String version = TestContent.GetPropertyString("sm_version");
                    String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
                    String token = TestContent.GetPropertyString("sm_access_token");
                    String url = protocol + "://" +
                            TestContent.GetPropertyString("host") + port + "/streammanager/api/" + version +
                            "/admin/event/meta/" +
                            TestContent.GetPropertyString("context") + "/" +
                            streamNameGUID + "?action=subscribe&accessToken=" + token;

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(new HttpGet(url));
                    StatusLine statusLine = response.getStatusLine();

                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        out.close();

                        final JSONObject json = new JSONObject(responseString);
                        final JSONObject data = json.getJSONObject("data");
                        final JSONObject meta = data.getJSONObject("meta");
                        final JSONArray streamList = meta.getJSONArray("stream");

                        if( streamList != null ){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showStreamList(streamList);
                                }
                            });
                        } else {
                            System.out.println("Provisions not returned");
                        }
                    } else {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        out.close();

                        final JSONObject j = new JSONObject(responseString);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Error");
                                    alertDialog.setMessage(j.getString("errorMessage"));
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }

                                    );
                                    alertDialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    protected void showStreamList (JSONArray streamList) {
        final Context context = this.getActivity();

        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Button btn = (Button)v;
                final String name = String.valueOf(btn.getText());
                startSubscriber(name);
                buttonContainer.removeAllViews();
            }

        };
        for (int i = 0; i < streamList.length(); i++) {
            try {
                JSONObject stream = streamList.getJSONObject(i);
                String name = stream.getString("name");
                Button btn = new Button(context);
                btn.setText(name);
                btn.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0, 1));
                buttonContainer.addView(btn);
                btn.setOnClickListener(clickListener);

            } catch (JSONException e) {
                System.out.println("Error in provisions: " + e.getMessage());
            }
        }

    }

    protected void startSubscriber (final String streamName) {
        final Context context = this.getActivity();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //url format: https://{streammanagerhost}:{port}/streammanager/api/2.0/event/{scopeName}/{streamName}?action=subscribe
                    String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
                    String version = TestContent.GetPropertyString("sm_version");
                    String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
                    String url = protocol + "://" +
                            TestContent.GetPropertyString("host") + port + "/streammanager/api/" + version + "/event/" +
                            TestContent.GetPropertyString("context") + "/" +
                            streamName + "?action=subscribe";

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(new HttpGet(url));
                    StatusLine statusLine = response.getStatusLine();

                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
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
                                    subscribeToManager(outURL, streamName);
                                }
                            });
                        } else {
                            System.out.println("Server address not returned");
                        }
                    } else{
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        out.close();

                        final JSONObject j = new JSONObject(responseString);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Error");
                                    alertDialog.setMessage(j.getString("errorMessage"));
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }

                                    );
                                    alertDialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void subscribeToManager( String url, String name ){

        //Create the configuration from the tests.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                url,
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("subscribe_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(getActivity().getPackageName());

		String params = TestContent.getConnectionParams();
		if (params != null) {
			config.setParameters(params);
		}

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);
        subscribe.setListener(this);

        subscribe.audioController = new R5AudioController();
        subscribe.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //find the view and attach the stream
        display.attachStream(subscribe);

        display.showDebugView(TestContent.GetPropertyBool("debug_view"));

        subscribe.play(name, TestContent.GetPropertyBool("hwAccel_on"));

        edgeShow = new TextView(display.getContext());
        FrameLayout.LayoutParams position = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        edgeShow.setLayoutParams(position);

        ((FrameLayout)display.getParent()).addView(edgeShow);

        edgeShow.setText("Connected to: " + url, TextView.BufferType.NORMAL);
        edgeShow.setBackgroundColor(Color.LTGRAY);
    }

}
