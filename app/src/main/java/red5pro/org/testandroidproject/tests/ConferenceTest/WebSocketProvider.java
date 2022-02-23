package red5pro.org.testandroidproject.tests.ConferenceTest;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class WebSocketProvider {

	private String url;
	private WebSocketClient mWebSocketClient;

	interface OnWebSocketListener {
		void onWebSocketMessage(String room, ArrayList<String> streams);
	}

	public WebSocketProvider (String url) {
		this.url = url;
	}

	private URI generateURI (String url) {
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void connect (final WebSocketProvider.OnWebSocketListener callback) {
		URI uri = generateURI(url);
		if (uri == null) return;

		mWebSocketClient = new WebSocketClient(uri) {

			@Override
			public void onOpen(ServerHandshake handshakedata) {}

			@Override
			public void onMessage(String message) {
				Log.d("MESSAGE", message);
				try {
					JSONObject payload = new JSONObject(message);
					String room = payload.getString("room");
					JSONArray streams = payload.getJSONArray("streams");
					ArrayList<String> streamNames = new ArrayList<String>();
					if (streams != null) {
						for (int i=0; i<streams.length(); i++) {
							streamNames.add(streams.getString(i));
						}
					}
					callback.onWebSocketMessage(room, streamNames);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				Log.d("Error", reason);
			}

			@Override
			public void onError(Exception ex) {
				Log.d("Error", ex.toString());
			}

		};
		mWebSocketClient.connect();
	}

	public void disconnect () {
		if (mWebSocketClient != null) {
			mWebSocketClient.close();
			mWebSocketClient = null;
		}
	}
}
