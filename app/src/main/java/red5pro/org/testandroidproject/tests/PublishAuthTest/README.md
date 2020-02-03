# Publisher Authentication using Red5 Pro

This is an example of authenticating a Broadcast for stream playback.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishAuthTest.java](PublishAuthTest.java)***

> This example requires you to enable the `SimpleAuthentication` Plugin for the `live` webapp. More information: [https://www.red5pro.com/docs/](https://www.red5pro.com/docs/).

## Authenticating

With the username and password known from the Red5 Pro Server `webapps/live/WEB-INF/simple-auth-plugin.credentials` file (if following the basic auth setup of the Red5 Pro Server), those values are provided to the `parameters` attribute of the `R5Configuration` instance delimited and appended with a semicolon (`;`).

For example, if you have defined the authorization of a username `foo` with a password `bar`, the configuration addition would look like the following:

```java
config.setParameters("username=foo;password=bar;");
```

### Example

In the example, the `username` and `password` values are defined in the [test.xml](../../res/raw/test.xml#L123-L133) file entry for the *Publish - Auth* test. They are accessed and provided to the `R5Configuration` instance prior to establishing a connection:

```java
String auth = "username=" + TestContent.GetPropertyString("username") + ";";
auth += "password=" + TestContent.GetPropertyString("password") + ";";
//Create the configuration from the values.xml
R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
  TestContent.GetPropertyString("host"),
  TestContent.GetPropertyInt("port"),
  TestContent.GetPropertyString("context"),
  TestContent.GetPropertyFloat("publish_buffer_time"),
  auth);
config.setLicenseKey(TestContent.GetPropertyString("license_key"));
config.setBundleID(getActivity().getPackageName());
```

[PublishAuthTest.java #33](PublishAuthTest.java#L33)

If the provided credentials match those defined for the `live` webapp in its Simple Authentication properties, then the broadcast will begin as normal. If the credentials _do not_ match, the broadcast will be rejected.

