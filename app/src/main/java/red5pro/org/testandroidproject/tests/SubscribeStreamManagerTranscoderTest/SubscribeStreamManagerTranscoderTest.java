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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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

		String context = TestContent.GetPropertyString("context");
		String streamName = TestContent.GetPropertyString("stream1");

		authenticateAndRequestProvisions(String.format("%s/%s", context, streamName));

        return view;

    }

	protected void authenticateAndRequestProvisions(final String streamNameGUID) {
		final Context context = this.getActivity();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String host = TestContent.GetPropertyString("host");
					String username = TestContent.GetPropertyString("sm_username");
					String password = TestContent.GetPropertyString("sm_password");

					String urlStr = String.format("https://%s/as/v1/auth/login", host);
					String creds = String.format("%s:%s", username, password);
					String token = String.format("Basic %s", Base64.getEncoder().encodeToString(creds.getBytes()));

					URL url = new URL(urlStr);
					HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setChunkedStreamingMode(0);
					httpURLConnection.setRequestProperty("Authorization", token);
					httpURLConnection.setRequestProperty("Accept", "application/json");
					httpURLConnection.setRequestProperty("Content-type", "application/json");
					httpURLConnection.setRequestMethod("PUT");

					InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder result = new StringBuilder();
					String line = null;
					try {
						while ((line = reader.readLine()) != null) {
							result.append(line + "\n");
						}
						final JSONObject jsonObject = new JSONObject(result.toString());
						requestProvisions(jsonObject.getString("token"), streamNameGUID);

					} catch (Exception e) {
						throw e;
					}
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
				}
			}
		}).start();
	}

    protected void requestProvisions (final String authToken, final String streamNameGUID) {
        final Context context = this.getActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //url format: `https://${host}/as/${version}/streams/provision/${nodeGroup}/${streamGuid}`
					String host = TestContent.GetPropertyString("host");
					String version = TestContent.GetPropertyString("sm_version");
					String nodeGroup = TestContent.GetPropertyString("sm_nodegroup");
					String url = String.format("https://%s/as/%s/streams/provision/%s/%s", host, version, nodeGroup, streamNameGUID);

					URL url1 = new URL(url);
					HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
					httpURLConnection.setDoOutput(false);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setChunkedStreamingMode(0);
					httpURLConnection.setRequestProperty("Authorization", "Bearer " + authToken);
					httpURLConnection.setRequestProperty("Accept", "application/json");
					httpURLConnection.setRequestProperty("Content-type", "application/json");
					httpURLConnection.setRequestMethod("GET");

					InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder result = new StringBuilder();
					String line = null;
					try {
						while ((line = reader.readLine()) != null) {
							result.append(line + "\n");
						}
						final JSONObject jsonObject = new JSONObject(result.toString());
						final JSONArray streams = jsonObject.getJSONArray("streams");
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showStreamList(streams);
							}
						});

					} catch (Exception e) {
						throw e;
					}
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
                final String streamGuid = String.valueOf(btn.getText());
                startSubscriber(streamGuid);
                buttonContainer.removeAllViews();
            }

        };
        for (int i = 0; i < streamList.length(); i++) {
            try {
                JSONObject stream = streamList.getJSONObject(i);
                String name = stream.getString("streamGuid");
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

    protected void startSubscriber (final String streamGuid) {

		//url format: "\(host)\(portURI)/as/\(version)/streams/stream/\(nodeGroup)/publish/\(context)/\(streamName)"
		String host = TestContent.GetPropertyString("host");
		String version = TestContent.GetPropertyString("sm_version");
		String nodeGroup = TestContent.GetPropertyString("sm_nodegroup");
		List<String> paths = Arrays.asList(streamGuid.split("/"));
		String streamName = paths.get(paths.size() - 1);

		String url = String.format("https://%s/as/%s/streams/stream/%s/subscribe/%s",
			host,
			version,
			nodeGroup,
			streamGuid);

		getEdgeAndSubscribe(url, streamName);
    }

}
