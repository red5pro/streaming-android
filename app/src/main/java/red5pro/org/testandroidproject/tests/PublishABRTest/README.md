# Adaptive Bitrate Publishing

Demonstrates the `AdaptiveBitrateController`, which dynamically adjusts the publishing bitrate to meet the bandwidth restrictions of the network connection or encoding hardware.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishABRTest.java](PublishABRTest.java)***

### Setup

To set up the `AdaptiveBitrateController` create an instance of the controller and attach the stream you wish to control. It will monitor the stream and make all adjustments automatically for you.

```Java
R5AdaptiveBitrateController adaptor = new R5AdaptiveBitrateController();
adaptor.AttachStream(publish);
```

[PublishABRTest.java #56](PublishABRTest.java#L56)

### Range

The `AdaptiveBitrateController` will dynamically adjust the video bit rate between the lowest possible bit rate the encoder can achieve and the value set on the `R5VideoSource` (typically an `R5Camera`) on the stream. Assign that value in `tests.xml`.

```Java
camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
```

[PublishABRTest.java #53](PublishABRTest.java#L53)

The controller will adjust the bit rate up to 200 kbps every two seconds to achieve the best possible video quality.

If the stream cannot maintain a smooth connection at the lowest possible bit rate, the video will be turned off. ***(? What does that mean, exactly?)***

You can force the video stream ***(to be?)*** with the `AdaptiveBitrateController.requiresVideo` flag. ***(Do we want them to be able to do this?)***