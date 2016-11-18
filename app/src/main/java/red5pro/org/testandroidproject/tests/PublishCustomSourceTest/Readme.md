#Custom Publishing on Red5 Pro

This example demonstrates passing custom video data into the R5Stream.

###Example Code
- ***[PublishCustomSourceTest.java](PublishCustomSourceTest.java)***
- ***[CustomVideoSource.java](CustomVideoSource.java)***

###Setup
To view this example, you simply need to open the example and subscribe to your stream from a second device.  All audio will be recorded, and instead of camera input, a simply plasma style effect is rendered.

####Attach a Custom Video Source
Instead of using an R5Camera, this example uses a custom video source, the `CustomVideoSource`.  This device

```Java
CustomVideoSource source = new CustomVideoSource();
publish.attachCamera(source);
```
<sup>
[PublishCustomSourceTest.java #54](PublishCustomSourceTest.java#L54)
</sup>

This device automatically polls and pushes to the encoder when new pixels are ready to be published.  There are several steps that are needed to properly feed data to the encoder.

1. **Make sure all bytes are in the YUV420 or YV12 pixel format.**  `CustomVideoSource` uses RGBA data, so it has a special function `encodeYUV420` which converts RGB data to this format.  This method can be seen at [CustomVideoSource.java #47](CustomVideoSource.java#L47).
2. **Call `prepareFrame`**.  This method will convert the YUV data into the proper format for the encoder.  `CustomVideoSource` has two buffers, and copies the properly prepared frames to the output buffer at [CustomVideoSource.java #183](CustomVideoSource.java#L183).
3. **Get the timestamp.** The timestamp can be gotten from the AudioEngine if you are publishing audio, or can be localled tracked.  [CustomVideoSource.java #187](CustomVideoSource.java#L187).
4. **Encode the data!** This last call to `encode` will send the data to the encoder and pass to the stream when ready. [CustomVideoSource.java #192](CustomVideoSource.java#L192).

