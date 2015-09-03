#Adaptive Bitrate Publishing

This example demonstrates the AdaptiveBitrateController, which provides a mechanism to dynamically adjust the video publishing bitrate to adjust quality to meet the bandwidth restrictions of the network connection or encoding hardware.

###Example Code
- ***[AdaptiveBitrateExample.java](AdaptiveBitrateExample.java)***

###Setup
The AdaptiveBitrateController is simple to setup.  You simply create a new instance of the controller and attach the stream you wish to control.  It will monitor the stream and make all adjustments automatically for you.


```Java
R5AdaptiveBitrateController adaptor = new R5AdaptiveBitrateController();
adaptor.AttachStream(publish);
```

<sup>
[AdaptiveBitrateExample.java #60](AdaptiveBitrateExample.java#L60)
</sup>

The controller will continuously adjust the video bitrate until the stream has closed.

###Range
The AdaptiveBitrateController will dynamically adjust the video bitrate between the lowest possible bitrate the encoder can encode at, and the value set on the R5VideoSource (typically an R5Camera) on the stream.  

```camera.setBitrate(res.getInteger(R.integer.highBitrate));
```

<sup>
[AdaptiveBitrateExample.java #57](AdaptiveBitrateExample.java#L57)
</sup>


The controller will adjust the bitrate ~200 kbps every 2 seconds to achieve the best possible video quality.



Video will be turned off if the stream is unable to maintain a smooth connection at the lowest possible bitrate.<!--  You can force video to be included with the `AdaptiveBitrateController.requiresVideo` flag.-->






