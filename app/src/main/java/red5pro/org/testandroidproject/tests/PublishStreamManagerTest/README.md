#Stream Manager Publishing

With clustering, we need to determine which red5 pro instance the client will use. The other examples used a static configuration ip for streaming endpoints. Basic clustering uses more than one stream endpoint for subscribers. Advanced clustering uses more than one endpoint for publishers also.With the Stream Manager, our configuration ip will be used similarly for publishers and subscribers. Both publishers and subscribers will call a web service to receive the ip that should be used.

###Example Code
- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishStreamManagerTest.java](PublishStreamManagerTest.java)***

###Setup
In order to publish, you first need to connect to the origin server's Stream Manager. The Stream Manager will know which edges are active and provide the one that needs to be published to.

```Java
String url = "http://" +
	TestContent.GetPropertyString("host") + ":5080/streammanager/api/1.0/event/" +
	TestContent.GetPropertyString("context") + "/" +
	TestContent.GetPropertyString("stream1") + "?action=broadcast";

HttpClient httpClient = new DefaultHttpClient();
HttpResponse response = httpClient.execute(new HttpGet(url));
StatusLine statusLine = response.getStatusLine();

if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	response.getEntity().writeTo(out);
	String responseString = out.toString();
	out.close();
```
<sup>
[PublishStreamManagerTest.java #46](PublishStreamManagerTest.java#L46)
</sup>

The service returns a json object with the information needed to connect to publish.

```Java
	JSONObject data = new JSONObject(responseString);
	String outURL = data.getString("serverAddress");
```
<sup>
[PublishStreamManagerTest.java #63](PublishStreamManagerTest.java#L63)
</sup>
