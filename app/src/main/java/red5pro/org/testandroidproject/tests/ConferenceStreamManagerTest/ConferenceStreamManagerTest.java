package red5pro.org.testandroidproject.tests.ConferenceStreamManagerTest;

import android.os.Handler;
import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import red5pro.org.testandroidproject.tests.ConferenceTest.ConferenceTest;
import red5pro.org.testandroidproject.tests.TestContent;

public class ConferenceStreamManagerTest extends ConferenceTest {

	protected boolean cleanUp = false;

	private void delayRetryRequest (final String streamName, final String context, final String action, final ConferenceStreamManagerTest.StreamURLDelegate starter) {
		Handler h = new Handler(Looper.getMainLooper());
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				requestServer(streamName, context, action, starter);
			}
		}, 2000);
	}

	private void requestServer(
		final String streamName,
		final String context,
		final String action,
		final ConferenceStreamManagerTest.StreamURLDelegate starter) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try{

					// start by delaying for a second just to be sure other activities
					// are cleaned up before stream could run
					Thread.sleep(1000);

					//url format: "\(host)\(portURI)/as/\(version)/streams/stream/\(nodeGroup)/\(action)/\(context)/\(streamName)"
					String host = TestContent.GetPropertyString("host");
					String nodeGroup = TestContent.GetPropertyString("sm_nodegroup");
					String version = TestContent.GetPropertyString("sm_version");
					String url = String.format("https://%s/as/%s/streams/stream/%s/%s/%s/%s",
						host,
						version,
						nodeGroup,
						action,
						context,
						streamName);

					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(new HttpGet(url));
					StatusLine statusLine = response.getStatusLine();

					if (statusLine.getStatusCode() == HttpStatus.SC_OK && !cleanUp) {
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
									starter.passURL(outURL);
								}
							});
						}
						else {
							System.out.println("Server address not returned");
							delayRetryRequest(streamName, context, action, starter);
						}
					}
					else{
						response.getEntity().getContent().close();
//						throw new IOException(statusLine.getReasonPhrase());
						delayRetryRequest(streamName, context, action, starter);
					}

				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void publish() {
		final String context = TestContent.GetPropertyString("context") + "/" + roomName;
		requestServer(pubName, context, "publish",  new ConferenceStreamManagerTest.StreamURLDelegate() {
			@Override
			public void passURL(String url) {
				config.setHost(url);
				config.setContextName(context);
				ConferenceStreamManagerTest.super.publish();
			}
		});
	}

	@Override
	public void Subscribe(final String toName) {
		final String context = TestContent.GetPropertyString("context") + "/" + roomName;
		requestServer(toName, context, "subscribe",  new ConferenceStreamManagerTest.StreamURLDelegate() {
			@Override
			public void passURL(String url) {
				config.setHost(url);
				config.setContextName(context);
				ConferenceStreamManagerTest.super.Subscribe(toName);
			}
		});
	}

	@Override
	public void onStop() {
		cleanUp = true;
		super.onStop();
	}

	private interface StreamURLDelegate {
		void passURL(String url);
	}
}
