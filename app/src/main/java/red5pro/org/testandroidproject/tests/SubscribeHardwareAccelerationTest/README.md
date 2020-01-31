# Subscribing to playback with Hardware Accelerated Video Decode

This example shows how to playback a stream using the Hardware Accelerated decode of the video data.

## Example Code

- ***[SubscribeHardwareAccelerationTest.java](SubscribeHardwareAccelerationTest.java)***

# Video Decoding

In Mobile SDK versions prior to `6.0.0`, the SDK defaulted to decoding incoming video frames on the CPU using an RGB scalar. The default for `6.0.0` and forward is to move the video decode operation to the GPU. This generates YUV420p (tri-planar) data.

Additionally, you can request to use **hardware accelerated** decode capabilities of the target platform; in the case of iOS, that is `MediaCodec`. The result of using **hardware acceleration** for decode is the generation of a YUV420v data represented as a an array of `byte` array length of 2 (1 plane for `Y`, the other for `UV`).

## play:withHardwareAcceleration

The API to turn on hardware accelerated video frame decoding is `play` with the second argument being `true` to turn on hardware acceleration:

```java
subscribe.play(TestContent.GetPropertyString("stream1"), true);
```

[SubscribeHardwareAccelerationTest #71](SubscribeHardwareAccelerationTest.java#L71)

## setFrameListener

Because there are a few different possibilities in requesting decode format as of the `6.0.0` release, the `setFrameListener` callback API of `R5FrameListener` interface has been updated to include the format of the data being sent.

```java
subscribe.setFrameListener(new R5FrameListener() {
  @Override
  public void onFrameReceived(Object o, R5StreamFormat r5StreamFormat, int w, int h) {
    int format = r5StreamFormat.value(); // 3 - YUV Bi-Planar
    if (r5StreamFormat.equals(R5StreamFormat.YUV_BIPLANAR)) {
        byte[][] yuv_frames = (byte[][]) o; // Cast and access data in 2 planes as byte array. (byte[2][])
    }
  }
});
```

[SubscribeHardwareAccelerationTest #104](SubscribeHardwareAccelerationTest.java#L104)

The `R5StreamFormat` enumeration is:

* `UNKNOWN` : an unknown/unspecified format
* `RGB` : RGB. The `data` argument is a single block of data.
* `YUV_PLANAR` : YUV420p, tri-planar. The `data` is an array of data in 3 planes (Y, U, V, respectively).
* `YUV_BIPLANAR` : YUV420v. The `data` argument is an array of data in 2 planes (Y and UV, respectively).

