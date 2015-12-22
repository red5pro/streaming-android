#Custom Publishing on Red5 Pro

This is the basic starter example on using a custom publisher for Red5 Pro

###Example Code
- ***[CustomPublishExample.java](CustomPublishExample.java)***
- ***[CustomCaptureDevice.java](CustomCaptureDevice.java)***



####Attach a Custom Video Source
Instead of using an R5Camera, this example uses a custom video source, the `CustomCaptureDevice`.  This device

```Java
  CustomCaptureDevice device = new CustomCaptureDevice();
  publish.attachCamera(device);
```
<sup>
[CustomPublishExample.java #63](CustomPublishExample.java#L63)
</sup>

This device automatically polls and pushes to the encoder when new pixels are ready to be published.  There are several steps that are needed to properly feed data to the encoder.

1. **Make sure all bytes are in the YUV420 or YV12 pixel format.**  `CustomCaptureDevice` uses RGBA data, so it has a special function `encodeYUV420` which converts RGB data to this format.  This method can be seen at [CustomCaptureDevice.java #48](CustomCaptureDevice.java#L48).
2. **Call `prepareFrame`**.  This method will convert the YUV data into the proper format for the encoder.  `CustomCaptureDevice` has two buffers, and copies the properly prepared frames to the output buffer at [CustomCaptureDevice.java #202](CustomCaptureDevice.java#L202).
3. **Get the timestamp.** The timestamp can be gotten from the AudioEngine if you are publishing audio, or can be localled tracked.  [CustomCaptureDevice.java #208](CustomCaptureDevice.java#L208).
4. **Encode the data!** This last call to `encode` will send the data to the encoder and pass to the stream when ready. [CustomCaptureDevice.java #213](CustomCaptureDevice.java#L213).

