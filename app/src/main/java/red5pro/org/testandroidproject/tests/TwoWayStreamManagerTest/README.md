# Two Way with the Stream Manager

With clustering, we need to determine which red5 pro instance the client will use. The other examples used a static configuration ip for streaming endpoints. Basic clustering uses more than one stream endpoint for subscribers. Advanced clustering uses more than one endpoint for publishers also.

With the Stream Manager, our configuration ip will be used similarly for publishers and subscribers. Both publishers and subscribers will call a web service to receive the ip that should be used.

### Example Code

- ***[TwoWayTest.java](../TwoWayTest/TwoWayTest.java)***
- ***[TwoWayStreamManagerTest.java](TwoWayStreamManagerTest.java)***

## Running the Example

Like the other Two Way example, you need two devices running it, and the second will need to hit `swap streams` in the home settings, so that they're publishing and subscribing to each other. You will also need to have pointed the app to a properly deployed cluster origin server.

### Setup

In order to stream, you first need to connect to the origin server's Stream Manager. The Stream Manager will know which edges are active and provide the one that needs to be published to. For the publisher we add the action `broadcast` to the web call, while we send `subscribe` for the subscribers.

```Java
String url = "http://" +
        TestContent.GetPropertyString("host") + port + "/streammanager/api/2.0/event/" +
        TestContent.GetPropertyString("context") + "/" + streamName + "?action=" + action;

HttpClient httpClient = new DefaultHttpClient();
HttpResponse response = httpClient.execute(new HttpGet(url));
StatusLine statusLine = response.getStatusLine();
```

[TwoWayStreamManagerTest.java #78](TwoWayStreamManagerTest.java#L78)

The service returns a json object with the information needed to connect to publish.

```Java
ByteArrayOutputStream out = new ByteArrayOutputStream();
response.getEntity().writeTo(out);
String responseString = out.toString();
out.close();

JSONObject data = new JSONObject(responseString);
final String outURL = data.getString("serverAddress");
```

[TwoWayStreamManagerTest.java #87](TwoWayStreamManagerTest.java#L87)

### Knowing When to Subscribe

Like with any stream, you can't subscribe to a stream until it's been published. To know what streams are available to subscribe to with clustering, use the `list` function of the Stream Manager api.

```Java
String urlStr = "http://" + TestContent.GetPropertyString("host") + port + "/streammanager/api/2.0/event/list";

URL url = new URL(urlStr);
HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
```

[TwoWayStreamManagerTest.java #194](TwoWayStreamManagerTest.java#L194)

Like using `streams.jsp` on a solo server, on success this returns a JSON array of dictionaries. For our purposes, the only property we care about in the dictionary is `name` - as we need to compare it against the name we've set up to subscribe to.

```Java
JSONArray list = new JSONArray(responseString);

for (int i = 0; i < list.length(); i++) {
    if (list.getJSONObject(i).getString("name").equals(TestContent.GetPropertyString("stream2"))) {
```

[TwoWayStreamManagerTest.java #220](TwoWayStreamManagerTest.java#L220)

For more information on this and other parts of the Stream Manager API, see our dcumentation [here](https://www.red5pro.com/docs/autoscale/streammanagerapi-v2.html)
