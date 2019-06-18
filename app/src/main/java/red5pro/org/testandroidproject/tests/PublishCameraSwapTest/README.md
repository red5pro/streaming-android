# Publish Camera Swap

`R5Camera.setCamera` allows the user to change the video source for a stream without interupting the broadcast.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishCameraSwapTest.java](PublishCameraSwapTest.java)***

## Running the example

Touch the screen at any time while streaming to switch between broadcasting from the front facing and back facing camera.

## Using R5Camera.device

`R5Camera.setCamera` will allow you to hot-swap sources for the stream. Once streaming, simply call:

```Java
R5Camera publishCam = (R5Camera)publish.getVideoSource();
```

[PublishCameraSwapTest.java #32](PublishCameraSwapTest.java#L32)

```Java
publishCam.setCamera(newCam);
```

[PublishCameraSwapTest.java #59](PublishCameraSwapTest.java#L59)