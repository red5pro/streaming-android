# Subscribing to Transcoded Stream over Stream Manager

This example show how to rquest a provision list for a transcoded video over Stream Manager and select playback.

## Example Code

- ***[SubscribeStreamManagerTranscoderTest.java](SubscribeStreamManagerTranscoderTest.java)***

# Setup

You will first need to define a provision and begin publishing to the highest variant with a supported broadcaster.

> For ease we have provided an publisher example to do so: [PublishStreamManagerTranscode](../PublishStreamManagerTranscode)

# Requesting Transcoder Provision

In the example, a request for the transcoder provision is made prior to:

1. Making a subsequent request for the Edge to which to subscribe.
2. Requesting subscription for playback.

To access the provision held on the server, use the Stream Manager API for event metadata:

```java
String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
String version = TestContent.GetPropertyString("sm_version");
String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
String token = TestContent.GetPropertyString("sm_access_token");
String url = protocol + "://" +
    TestContent.GetPropertyString("host") + port + "/streammanager/api/" + version +
    "/admin/event/meta/" +
    TestContent.GetPropertyString("context") + "/" +
    streamNameGUID + "?action=subscribe&accessToken=" + token;

HttpClient httpClient = new DefaultHttpClient();
HttpResponse response = httpClient.execute(new HttpGet(url));
```

If you are requesting to subscribe to a stream named `mystream`, the above endpoint API URL would look something similar to:

```ssh
https://myserver.com/streammanager/api/3.1/admin/event/meta/live/mystream?action=subscribe&accessToken=123
```

In a successful response, a JSON array with variant stream names is provided.

> With a GUID of `mystream`, you will likely see a list of variant names such as the following: `mystream_1`, `mystream_2`, `mystream_3` - representing 3 levels of transcoded broadcast for consumption.

# Subscribing to a Variant

From the JSON list returned, the example provides the user to select which variant to being subscribing to. Upon selection:

1. A request on the Stream Manager is made to get the available Edge(s) that have the desired stream variant.
2. If successful, a request to being playback of the stream on the provided Edge(s).

> The above is how one would normally subscribe to a stream over [Stream Manager](../SubscribeStreamManager), only using the variant name (e.g, `mystream_1`) instead of the GUID (`mystream`).

The server will handle when to switch the stream variant on the client based on bandwidth and network availability.
