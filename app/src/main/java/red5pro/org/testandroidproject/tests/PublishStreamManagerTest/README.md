# Stream Manager Publishing

With clustering, we need to determine which red5 pro instance the client will use. The other examples used a static configuration ip for streaming endpoints. Basic clustering uses more than one stream endpoint for subscribers. Advanced clustering uses more than one endpoint for publishers also.

With the Stream Manager, our configuration IP will be used similarly for publishers and subscribers. Both publishers and subscribers will call a web service to receive the IP that should be used. Since this is an HTTP call, you can use a DNS Name for the `host` value. 

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishStreamManagerTest.java](PublishStreamManagerTest.java)***

### Setup

In order to publish, you first need to connect to the Origin server of your deployment. The Stream Manager will know which edges are active and provide the one that needs to be published to.

```Java
// url format: "\(host)\(portURI)/as/\(version)/streams/stream/\(nodeGroup)/publish/\(context)/\(streamName)"
String host = TestContent.GetPropertyString("host");
String version = TestContent.GetPropertyString("sm_version");
String nodeGroup = TestContent.GetPropertyString("sm_nodegroup");
String context = TestContent.GetPropertyString("context");
String streamName = TestContent.GetPropertyString("stream1");

String url = String.format("https://%s/as/%s/streams/stream/%s/publish/%s/%s",
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

    JSONArray origins = new JSONArray(responseString);
    JSONObject data = origins.getJSONObject(0);
    final String outURL = data.getString("serverAddress");
}
```

The service returns a JSON array of Origin nodes available to connect to; in typical deployments, this will be of a length of one.

```Java
JSONArray origins = new JSONArray(responseString);
JSONObject data = origins.getJSONObject(0);
final String outURL = data.getString("serverAddress");
```
