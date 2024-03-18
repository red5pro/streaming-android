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

import com.google.gson.Gson;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

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

	private void authenticateAndPost(final PublishTranscoderData data) {
		final Context context = this.getActivity();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String host = TestContent.GetPropertyString("host");
					String username = TestContent.GetPropertyString("sm_username");
					String password = TestContent.GetPropertyString("sm_password");

					String url = String.format("https://%s/as/v1/auth/login", host);
					String creds = String.format("%s:%s", username, password);
					String token = String.format("Basic %s", Base64.getEncoder().encodeToString(creds.getBytes()));

					URL url1 = new URL(url);
					HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
					httpURLConnection.setDoOutput(true);
					httpURLConnection .setDoInput(true);
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
						postProvisions(jsonObject.getString("token"), data);

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

    private void postProvisions (final String authToken, final PublishTranscoderData data) {

        final Context context = this.getActivity();
		final ArrayList list = new ArrayList<>();
		list.add(data);
		Gson gson = new Gson();
		final String json = gson.toJson(list);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
					String host = TestContent.GetPropertyString("host");
					String version = TestContent.GetPropertyString("sm_version");
					String nodeGroup = TestContent.GetPropertyString("sm_nodegroup");
					String url = String.format("https://%s/as/%s/streams/provision/%s", host, version, nodeGroup);

                    HttpURLConnection conn = null;
                    try {
                        URL url1 = new URL(url);
						conn = (HttpURLConnection) url1.openConnection();
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setChunkedStreamingMode(0);
						conn.setRequestProperty("Authorization", "Bearer " + authToken);
                        conn.setRequestProperty("Content-type", "application/json");
                        conn.setRequestMethod("POST");

                        OutputStream out = conn.getOutputStream();
                        out.write(json.getBytes());
						out.flush();
                        out.close();

                        try {
							int statusCode = conn.getResponseCode();
							if ((statusCode >= 200 && statusCode < 300) || (statusCode == 409)) {
								// 409 is Conflict, which means the provision already exists.
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										transcoderForm.setVisibility(View.INVISIBLE);
										getOriginAndPublish(transcoderData.getVariantByLevel(1));
									}
								});
							} else {
								String errorMessage = "Could not create provision. Status code: " + statusCode;
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {

										try {
											AlertDialog alertDialog = new AlertDialog.Builder(context).create();
											alertDialog.setTitle("Error");
											alertDialog.setMessage(errorMessage);
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

    private void getOriginAndPublish (final PublishTranscoderData.StreamVariant variant) {

        final Context context = this.getActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
					String host = TestContent.GetPropertyString("host");
					String version = TestContent.GetPropertyString("sm_version");
					String nodeGroup = TestContent.GetPropertyString("sm_nodegroup");
					String appContext = TestContent.GetPropertyString("context");
					String streamName = TestContent.GetPropertyString("stream1");

					String url = String.format("https://%s/as/%s/streams/stream/%s/publish/%s/%s",
						host,
						version,
						nodeGroup,
						appContext,
						streamName);

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(new HttpGet(url));
                    StatusLine statusLine = response.getStatusLine();

                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        out.close();

						JSONArray origins = new JSONArray(responseString);
						JSONObject data = origins.getJSONObject(0);
						final String outURL = data.getString("serverAddress");

                        if( !outURL.isEmpty() ){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    publishToManager(outURL, variant);
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

    private void publishToManager(String url, PublishTranscoderData.StreamVariant variant) {

		PublishTranscoderData.VideoParams videoParams = variant.videoParams;
		List<String> paths = Arrays.asList(variant.streamGuid.split("/"));
		String streamName = paths.get(paths.size() - 1);
		paths = paths.subList(0, paths.size() - 1);
		String context = String.join("/", paths);

		int port = TestContent.GetPropertyInt("port");
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                url,
                port,
                context,
                TestContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(getActivity().getPackageName());

		String params = TestContent.getConnectionParams();
		if (params != null) {
			config.setParameters(params);
		}

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

            camera = new R5Camera(cam, videoParams.videoWidth, videoParams.videoHeight);
            camera.setBitrate(videoParams.videoBitRate / 1000);
            camera.setOrientation(camOrientation);
            camera.setFramerate(getFPSFromLevel((variant.abrLevel).intValue()));
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

		String context = TestContent.GetPropertyString("context");
		String streamName = TestContent.GetPropertyString("stream1");
        String streamGuid = String.format("%s/%s", context, streamName);
        PublishTranscoderData.StreamVariant highVariant = form.getHighVariant(streamGuid + "_1", 1);
        PublishTranscoderData.StreamVariant mediumVariant = form.getMediumVariant(streamGuid + "_2", 2);
        PublishTranscoderData.StreamVariant lowVariant = form.getLowVariant(streamGuid + "_3", 3);

        ArrayList<PublishTranscoderData.StreamVariant> provisions = new ArrayList<>(
			Arrays.asList(highVariant, mediumVariant, lowVariant)
        );

        this.transcoderData = new PublishTranscoderData(streamGuid, provisions);
        authenticateAndPost(this.transcoderData);

    }
}
