# Subscribing to playback with RGB decode

This example shows how to playback a stream using the CPU decode of video frames to RGB.

## Example Code

- ***[SubscribeRendererRGBScalarTest.java](SubscribeRendererRGBScalarTest.java)***

# Video Decoding

In Mobile SDK versions prior to `6.0.0`, the SDK defaulted to decoding incoming video frames on the CPU using an RGB scalar. There were other API from the SDK that related to accessing these frames that you may have been using in your project(s). As such, providing an API to request to decode to RGB and access that RGB data is preserved for backward-compatiblity.

As of the `6.0.0` release of the Mobile SDKs, video decoding has been moved to the GPU to provide better playback. By default, the video frames are now decoded to YUV420v which is 3 planes of frame data.

Additionally, you can request to use hardware accelerated decode of the platform. In the case of Android, that uses `MediaCodec` and produces a YUV420v representation with 2 planes (`Y` and `UV`).

> For **Hardware Acceleration** usage, see [SubscribeHardwareAcceleration](../SubscribeHardwareAccelerationTest).

## play:withForcedRGBScalar

The API to support backward compatibility in generating and accessing the RGB video frame is `playWithForcedRGBScalar`:

```java
subscribe.playWithForcedRGBScalar(TestContent.GetPropertyString("stream1"));
```

[SubscribeRendererRGBScalarTest #64](SubscribeRendererRGBScalarTest.java#L64)

> The `R5Stream` API of `play` (which previously would produce RGB frame data) now defaults to offloading the video decode to the GPU to generate YUV420p (tri-planar) data.

## setFrameListener

Because there are a few different possibilities in requesting decode format as of the `6.0.0` release, the `setFrameListener` callback API of the `R5FrameListener` interface has been updated to include the format of the data being sent.

```java
subscribe.setFrameListener(new R5FrameListener() {
    @Override
    public void onFrameReceived(Object o, R5StreamFormat r5StreamFormat, int w, int h) {
        int format = r5StreamFormat.value(); // 1 - RGB
        if (r5StreamFormat.equals(R5StreamFormat.RGB)) {
            byte[] rgb_frame = (byte[]) o; // Cast and access data plane as byte array.
        }
    }
});
})
```

[SubscribeRendererRGBScalarTest #101](SubscribeRendererRGBScalarTest.swift#L101)

The `R5StreamFormat` enumeration is:

* `UNKNOWN` : an unknown/unspecified format
* `RGB` : RGB. The `data` argument is a single block of data.
* `YUV_PLANAR` : YUV420p, tri-planar. The `data` is an array of data in 3 planes (Y, U, V, respectively).
* `YUV_BIPLANAR` : YUV420v. The `data` argument is an array of data in 2 planes (Y and UV, respectively).
