# Stream Manager Publishing with Transcoder

With clustering, we need to determine which Red5 Pro instance the client will use. The other examples used a static configuration address for streaming endpoints. Basic clustering uses more than one stream endpoint for subscribers. Advanced clustering uses more than one endpoint for publishers also.

With the Stream Manager, our configuration IP will be used similarly for publishers and subscribers. Both publishers and subscribers will call a web service to receive the IP that should be used.

## Transcoder Support

To enable Adaptive Bitrate (ABR) control of a stream being played back by a consumer, you need to POST a provision to the Stream Manager detailing the variants at which you will be broadcasting.

For scenarios in which the broadcaster does not have the capability of publishing the variants of the provision, the broadcaster can request that the server does the Transcoding to the variants.

To do so, the broadcast most locate the server address of the Transcoder using the transcode=true query param, from which one of the variants will be broadcast to. The tTranscoder will that generate the additional variants for consumption.

> To learn more about the `transcode` query for API, please visit the documentation: [Stream Manager REST API](https://www.red5pro.com/docs/autoscale/streammanagerapi.html#rest-api-for-streams).

## Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishStreamManagerTranscodeTest.java](PublishStreamManagerTranscodeTest.java)***

# Setup

In order to publish using the transcoder, you need to request to endpoint for the transcoder from the Stream Manager just as you would in accessing the Origin endpoint in a normal Stream Manager request for broadcast. To do so, append the `transcode=true` query param on the end of the API request.

```Java
String url = "http://" +
  TestContent.GetPropertyString("host") + ":5080/streammanager/api/1.0/event/" +
  TestContent.GetPropertyString("context") + "/" +
  TestContent.GetPropertyString("stream1") + "?action=broadcast&transcode=true";

HttpClient httpClient = new DefaultHttpClient();
HttpResponse response = httpClient.execute(new HttpGet(url));
StatusLine statusLine = response.getStatusLine();

if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  response.getEntity().writeTo(out);
  String responseString = out.toString();
  out.close();
```

[PublishStreamManagerTranscodeTest.java #54](PublishStreamManagerTranscodeTest.java#L54)

The service returns a JSON object with the information needed to connect and publish to the transcoder.

```Java
  JSONObject data = new JSONObject(responseString);
  String outURL = data.getString("serverAddress");
```

[PublishStreamManagerTranscodeTest.java #69](PublishStreamManagerTranscodeTest.java#L69)

## Broadcast Stream Name

When using the Transcoder, a set of provisioning variants needs to be provided to the server (as mentioned in the documentation: [Stream Manager REST API](https://www.red5pro.com/docs/autoscale/streammanagerapi.html#rest-api-for-streams)).

When you start a broadcast to the Transcoder, it is recommended you configure your broadcast session with the details from the highest variant and start the stream with the associated stream name of the variant; in the case of this example, it is the `stream1` configuration name appended with `_1` (i.e., `mystream_1`).
