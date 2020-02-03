# Subscribe Auto Reconnect Test

This example utilizes overriding the `onConnectionEvent` method to test for and attempt to recover from connection errors or stopped streams by resubscribing after some preconfigured amount of time.  Prior to attempt to subscribe, it first makes a call on the `Streams API` to see if the stream is still active and available.

## Example Code

- **_[PublishTest.java](../PublishTest/PublishTest.java)_**
- **_[SubscribeReconnectTest.java](SubscribeReconnectTest.java)_**

# Implementation

Use the `Streams API` of the Red5 Pro Server to request the list of active publish streams. Once the stream is available in the listing, it can be subscribed to.
If the stream goes away - such as a loss or stop in broadcast - restart a timer to request the stream listing using the `Streams API` again.

## Streams API

The `Streams API` of the Red5 Pro Server can be found in the default location of the `live` webapp. Making a `GET` request on the `streams.jsp` file will return a JSON array of streams.

> If streaming to a different webapp context other than `live`, you will need to move the `streams.jsp` file and update the web configs as needed.

```java
private void findStreams() {

final String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
final String urlStr = "http://" + TestContent.GetPropertyString("host") + port + "/" + TestContent.GetPropertyString("context") + "/streams.jsp";

if(callThread != null) {
    callThread.interrupt();
    callThread = null;
}

Log.d("SubReconnectTest", "Requesting stream list...");

callThread = new Thread(new Runnable() {
    @Override
    public void run() {

        try {

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
                }
                else {
                    responseString = "error: http issue, response code - " + urlConnection.getResponseCode();
                }
            }
            catch (Exception e) {
            }
            finally {
                urlConnection.disconnect();
            }

            if(!Thread.interrupted()) {

                if (!responseString.startsWith("error")) {

                    Log.d("SubReconnectTest", "Stream list receieved...");

                    boolean exists = false;
                    JSONArray list = new JSONArray(responseString);

                    for (int i = 0; i < list.length(); i++) {
                        if (list.getJSONObject(i).getString("name").equals( TestContent.GetPropertyString("stream1") )) {
                            exists = true;
                            break;
                        }
                    }

                    final boolean willConnect = exists;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(willConnect) {
                                Log.d("SubReconnectTest", "Attempting a reconnect...");
                                reconnect();
                            }
                            else {
                                Log.d("SubReconnectTest", "Publisher does not exist.");
                                delayReconnect(reconnectDelay);
                            }

                        }
                    });

                }
                else {

                    Log.d("SubReconnectTest", "Error: " + responseString);
                    delayReconnect(reconnectDelay);

                }
            }

        }
        catch (Exception e) {
            Log.d("SubReconnectTest", "Error");
            e.printStackTrace();
        }

    }
});
callThread.start();

}
```

[SubscribeReconnectTest.java #33](SubscribeReconnectTest.java#L33)

Once the stream the subscriber is attempting to connect to has become available in the stream listing from the `Streams API`, you can continue to create a Subscriber session as you would normally.

## Events & Reconnection

In the occurance of a lost stream from the publisher - either from a network issue or stop of broadcast - you can stop the Subscriber session and start the request cycle on the `Streams API` again.

There are 2 important events that relate to the loss of a publisher:

1. `CLOSE`
2. `NET_STATUS` with message of `NetStream.Play.UnpublishNotify`

The first is an event notification that the stream being consumed has closed. The second is an event notification that the publisher has explicitly stopped their broadcast.

By listening on these events and knowing their meaning, you can act accordingly in setting up a new request cycle for the `Streams API`:

```java
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
                        subscribeTest.findStreams();
                    }

                }
            }, reconnectDelay);

        }
        else if (r5ConnectionEvent == R5ConnectionEvent.NET_STATUS && r5ConnectionEvent.message.equals("NetStream.Play.UnpublishNotify")) {

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
```

[SubscribeReconnectTest.java #175](SubscribeReconnectTest.java#L175)
