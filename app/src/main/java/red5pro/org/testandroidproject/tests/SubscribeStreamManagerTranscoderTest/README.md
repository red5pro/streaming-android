# Subscribing to Transcoded Stream over Stream Manager

This example show how to request a provision list for a transcoded video over Stream Manager and select playback.

## Example Code

- ***[SubscribeStreamManagerTranscoderTest.java](SubscribeStreamManagerTranscoderTest.java)***

# Setup

You will first need to define a provision and begin publishing to the highest variant with a supported broadcaster.

> For ease we have provided an publisher example to do so: [PublishStreamManagerTranscode](../PublishStreamManagerTranscode)

# Requesting Transcoder Provision

In the example, a request for the transcoder provision is made prior to:

1. Making a subsequent request for the Edge to which to subscribe.
2. Requesting subscription for playback.

## Provision

The following is an example of the schema for the provision previously posted - such as from the[PublishStreamManagerTranscode](../PublishStreamManagerTranscode):

```json
[
  {
    "streamGuid": "live/test",
    "streams": [
      {
        "streamGuid": "live/test_3",
        "abrLevel": 3,
        "videoParams": {
          "videoWidth": 320,
          "videoHeight": 180,
          "videoBitRate": 500000
        }
      },
      {
        "streamGuid": "live/test_2",
        "abrLevel": 2,
        "videoParams": {
          "videoWidth": 640,
          "videoHeight": 360,
          "videoBitRate": 1000000
        }
      },
      {
        "streamGuid": "live/test_1",
        "abrLevel": 1,
        "videoParams": {
          "videoWidth": 1280,
          "videoHeight": 720,
          "videoBitRate": 2000000
        }
      }
    ]
  }
]
```

## Access and Authorization

To access the provision held on the server, use the Stream Manager API to access variant stream listing within a provision associated with a root `streamGuid`, which is a combination of the app context and root stream name - for the pursoses of this example, that will be `live/test`.

Prior to the request for the provision an authorization token is required:

To request an authorization token:

```Java
String host = TestContent.GetPropertyString("host");
String username = TestContent.GetPropertyString("sm_username");
String password = TestContent.GetPropertyString("sm_password");

String url = String.format("https://%s/as/v1/auth/login", host);
String creds = String.format("%s:%s", username, password);
String token = String.format("Basic %s", Base64.getEncoder().encodeToString(creds.getBytes()));

URL url1 = new URL(url);
HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
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
    postProvisions(jsonObject.getString("token"), data);

} catch (Exception e) {
    throw e;
}
```

Then, using the token, request the provision:

```java
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
```

Once successul, the `streams` list is used to display the available stream variants to select from. The variant `streamGuid` is then used in requesting an Edge for stream playback; for the purposes of this example, the available `streamGuid` values would be `live/test_1`, `live/test_2` and `live/test_3`.

## Subscribing to a Variant

From the JSON list returned, the example provides the user to select which variant to being subscribing to. Upon selection:

1. A request on the Stream Manager is made to get the available Edge(s) that have the desired stream variant.
2. If successful, a request to being playback of the stream on the provided Edge(s).

### Requesting an Edge and Subscribe

With the target variant and `streamGuid`, request an Edge that the stream variant resides on to begin subscribing:

```Java
//url format: "\(host)\(portURI)/as/\(version)/streams/stream/\(nodeGroup)/publish/\(context)/\(streamName)"
String host = TestContent.GetPropertyString("host");
String version = TestContent.GetPropertyString("sm_version");
String nodeGroup = TestContent.GetPropertyString("sm_nodegroup");
String context = TestContent.GetPropertyString("context");
String streamName = TestContent.GetPropertyString("stream1");

String url = String.format("https://%s/as/%s/streams/stream/%s/subscribe/%s/%s",
    host,
    version,
    nodeGroup,
    context,
    streamName);
HttpClient httpClient = new DefaultHttpClient();
HttpResponse response = httpClient.execute(new HttpGet(url));
StatusLine statusLine = response.getStatusLine();

if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    response.getEntity().writeTo(out);
    String responseString = out.toString();
    out.close();

    JSONArray edges = new JSONArray(responseString);
    JSONObject data = edges.getJSONObject(0);
    final String outURL = data.getString("serverAddress");
}
```

The service returns a JSON array of Origin nodes available to connect to; in typical deployments, this will be of a length of one.

```Java
JSONArray origins = new JSONArray(responseString);
JSONObject data = origins.getJSONObject(0);
final String outURL = data.getString("serverAddress");
```

The Edge address is then used as the `host` configuration property in order to subscriber to the stream.

> The server will handle when to switch the stream variant on the client based on bandwidth and network availability.
