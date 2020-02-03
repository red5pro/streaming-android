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
package red5pro.org.testandroidproject.tests.PublishStreamManagerTranscodeTest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.http.HttpsConnection;
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
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by toddanderson on 07/25/2018.
 */
public class PublishStreamManagerTranscodeTest extends PublishTest implements
        PublishTranscoderForm.PublishTranscoderDelegate {

    protected TextView edgeShow;
    protected ViewGroup transcoderForm;
    protected ViewGroup buttonContainer;
    protected PublishTranscoderData transcoderData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.publish_sm_transcoder_test, container, false);

        transcoderForm = rootView.findViewById(R.id.transcoderForm);
        buttonContainer = rootView.findViewById(R.id.buttonContainer);
        preview = (R5VideoView)rootView.findViewById(R.id.videoPreview);

        new PublishTranscoderForm(transcoderForm, this);

        return rootView;
    }

    private void postProvisions (final String streamName, final String json) {

        final Context context = this.getActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
                    String version = TestContent.GetPropertyString("sm_version");
                    String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
                    String url = protocol + "://" +
                            TestContent.GetPropertyString("host") + port + "/streammanager/api/" + version + "/admin/event/meta/" +
                            TestContent.GetPropertyString("context") + "/" +
                            streamName + "?accessToken=" + TestContent.GetPropertyString("sm_access_token");

                    HttpURLConnection conn = null;
                    try {
                        URL url1 = new URL(url);
                        if (protocol.equals("https")) {
                            conn = (HttpsURLConnection) url1.openConnection();
                        } else {
                            conn = (HttpURLConnection) url1.openConnection();
                        }
                        conn.setDoOutput(true);
                        conn .setDoInput(true);
                        conn.setChunkedStreamingMode(0);
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setRequestProperty("Content-type", "application/json");
                        conn.setRequestMethod("POST");

                        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                        out.write(json.getBytes("UTF-8"));
                        out.close();

                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line = null;
                        try {
                            while ((line = reader.readLine()) != null) {
                                result.append(line + "\n");
                            }
                            final JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.has("errorMessage")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                            alertDialog.setTitle("Error");
                                            alertDialog.setMessage(jsonObject.getString("errorMessage"));
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
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        transcoderForm.setVisibility(View.INVISIBLE);
                                        getProvisions(TestContent.GetPropertyString("stream1"));
                                    }
                                });
                            }
                        } catch (Exception e) {
                            throw e;
                        }
                        in.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        final String message = e.getMessage();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Error");
                                    alertDialog.setMessage(message);
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
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void getProvisions (final String guidStreamName) {

        final Context context = this.getActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
                    String version = TestContent.GetPropertyString("sm_version");
                    String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
                    String url = protocol + "://" +
                            TestContent.GetPropertyString("host") + port + "/streammanager/api/" + version + "/admin/event/meta/" +
                            TestContent.GetPropertyString("context") + "/" +
                            guidStreamName + "?accessToken=" + TestContent.GetPropertyString("sm_access_token");

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(new HttpGet(url));
                    StatusLine statusLine = response.getStatusLine();

                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        out.close();

                        JSONObject json = new JSONObject(responseString);
                        final JSONObject data = json.getJSONObject("data");
                        final JSONObject meta = data.getJSONObject("meta");
                        final JSONArray streams = meta.getJSONArray("stream");

                        if( streams.length() > 0 ){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    View.OnClickListener clickListener = new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            final Button btn = (Button)v;
                                            final String name = String.valueOf(btn.getText());
                                            getOrigin(guidStreamName, name);
                                            buttonContainer.removeAllViews();
                                        }

                                    };
                                    for (int i = 0; i < streams.length(); i++) {
                                        try {
                                            JSONObject stream = streams.getJSONObject(i);
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
                            });
                        }
                        else {
                            System.out.println("Server address not returned");
                        }
                    }
                    else{
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getOrigin (final String guidStreamName, final String variantName) {

        final Context context = this.getActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
                    String version = TestContent.GetPropertyString("sm_version");
                    String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
                    String url = protocol + "://" +
                            TestContent.GetPropertyString("host") + port + "/streammanager/api/" + version + "/event/" +
                            TestContent.GetPropertyString("context") + "/" +
                            guidStreamName + "?action=broadcast&transcode=true";

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
                                    publishToManager(outURL, variantName);
                                }
                            });
                        }
                        else {
                            System.out.println("Server address not returned");
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int getFPSFromLevel (int level) {
        ArrayList<Integer> fps = new ArrayList<>(Arrays.asList(0, 60, 30, 15));
        return fps.get(level);
    }

    private void publishToManager( String url, String streamName ){

        HashMap<String, Object> variant = transcoderData.getVariantByName(streamName);
        HashMap<String, Integer> properties = (HashMap<String, Integer>)variant.get("properties");

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

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        R5Camera camera = null;
        if(TestContent.GetPropertyBool("video_on")) {
            //attach a camera video source
            cam = openFrontFacingCameraGingerbread();
            cam.setDisplayOrientation((camOrientation + 180) % 360);

            camera = new R5Camera(cam, properties.get("videoWidth"), properties.get("videoHeight"));
            camera.setBitrate(properties.get("videoBR") / 1000);
            camera.setOrientation(camOrientation);
            camera.setFramerate(getFPSFromLevel(((Integer)variant.get("level")).intValue()));
        }

        if(TestContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            R5Microphone mic = new R5Microphone();
            publish.attachMic(mic);
        }

        preview.attachStream(publish);

        if(TestContent.GetPropertyBool("video_on"))
            publish.attachCamera(camera);

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.publish(streamName, getPublishRecordType());

        if(TestContent.GetPropertyBool("video_on"))
            cam.startPreview();

        edgeShow = new TextView(preview.getContext());
        FrameLayout.LayoutParams position = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        edgeShow.setLayoutParams(position);

        ((FrameLayout)preview.getParent()).addView(edgeShow);

        edgeShow.setText("Connected to: " + url, TextView.BufferType.NORMAL);
        edgeShow.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onProvisionSubmit (PublishTranscoderForm form) {

        String streamName = TestContent.GetPropertyString("stream1");
        HashMap<String, Object> highVariant = form.getHighVariant(streamName, 1);
        HashMap<String, Object> mediumVariant = form.getMediumVariant(streamName, 2);
        HashMap<String, Object> lowVariant = form.getLowVariant(streamName, 3);

        ArrayList<HashMap<String, Object>> provisions = new ArrayList<>(
                Arrays.asList(highVariant, mediumVariant, lowVariant)
        );

        this.transcoderData = new PublishTranscoderData(provisions);

        postProvisions(streamName, this.transcoderData.toJSON());

    }
}
