# Stream Manager Subscribing

With clustering, we need to determine which red5 pro instance the client will use. The other examples used a static configuration ip for streaming endpoints. Basic clustering uses more than one stream endpoint for subscribers. Advanced clustering uses more than one endpoint for publishers also.

With the Stream Manager, our configuration IP will be used similarly for publishers and subscribers. Both publishers and subscribers will call a web service to receive the IP that should be used. Since this is an HTTP call, you can use a DNS Name for the `host` value.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[SubscribeStreamManagerTest.java](SubscribeStreamManagerTest.java)***

## Setup

In order to subscribe, you first need to connect to the origin server's Stream Manager. The Stream Manager will know which edges are active and provide the one that you need to subscribe from.

```Java
String url = "https://" +
  TestContent.GetPropertyString("host") + "/streammanager/api/3.1/event/" +
  TestContent.GetPropertyString("context") + "/" +
  TestContent.GetPropertyString("stream1") + "?action=subscribe";

HttpClient httpClient = new DefaultHttpClient();
HttpResponse response = httpClient.execute(new HttpGet(url));
StatusLine statusLine = response.getStatusLine();

if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  response.getEntity().writeTo(out);
  String responseString = out.toString();
  out.close();
```

[SubscribeStreamManagerTest.java #46](SubscribeStreamManagerTest.java#L46)

The service returns a json object with the information needed to connect to subscribe.

```Java
  JSONObject data = new JSONObject(responseString);
  String outURL = data.getString("serverAddress");
```

[SubscribeStreamManagerTest.java #48](SubscribeStreamManagerTest.java#L48)

> **Note:** you will want to start the stream on the main thread.
