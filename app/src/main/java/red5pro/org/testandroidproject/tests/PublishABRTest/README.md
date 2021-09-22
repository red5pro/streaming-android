# Adaptive Bitrate Publishing

Demonstrates the `AdaptiveBitrateController`, which dynamically adjusts the publishing bitrate to meet the bandwidth restrictions of the network connection or encoding hardware.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishABRTest.java](PublishABRTest.java)***

### Setup

The AdaptiveBitrateController is simple to setup.  You simply create a new instance of the controller and attach the stream you wish to control.  It will monitor the stream and make all adjustments automatically for you.

```Java
R5AdaptiveBitrateController adaptor = new R5AdaptiveBitrateController();
adaptor.AttachStream(publish);
```

[PublishABRTest.java #56](PublishABRTest.java#L56)

The controller will continuously adjust the video bitrate until the stream has closed.

### Range

The AdaptiveBitrateController will dynamically adjust the video bitrate between the lowest possible bitrate the encoder can encode at, and the value set on the R5VideoSource (typically an R5Camera) on the stream. In this case, the value is assigned according to the value in tests.xml 

```Java
camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
```

[PublishABRTest.java #53](PublishABRTest.java#L53)

The controller will adjust the bitrate ~200 kbps every 2 seconds to achieve the best possible video quality.

Video will be turned off if the stream is unable to maintain a smooth connection at the lowest possible bitrate.  You can force video to be included with the `AdaptiveBitrateController.requiresVideo` flag.