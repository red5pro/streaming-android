# Publish Camera2

The Camera2 API offers a robust suite of controls for a device's camera. While it's still possible to use the Camera API for simpler use-cases, note that it is deprecated. Instead, the Android developer documents recommend using the Camera2 and CameraX APIs. See the [Camera API](https://developer.android.com/guide/topics/media/camera) and [Camera2 API](https://developer.android.com/training/camera2) Developer Documentation for more information.

### Example Code

- [PublishTest.java](../PublishTest/PublishTest.java)
- [PublishCamera2Test.java](Publish2Test.java)

## Using R5Camera2

**Note:** *Introduced in Android API 21, with additional features included in API 22, the Camera2 API has a higher minimum API than the rest of our SDK.*

The most significant change that you'll notice from the base publish example is that `CameraDevice` cannot be opened synchronously. Therefore, creating the `R5Camera2` object and triggering the publisher is done after the OS has returned the camera to you.

To call for the hardware camera, ask `CameraManager` for the list of available devices, identify the one you want, and then acquire it through the manager.

```Java
CameraManager manager = (CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);
try {
  String[] camList = manager.getCameraIdList();
    for(String id : camList){
        CameraCharacteristics info = manager.getCameraCharacteristics(id);
        if(info.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT){
            camInfo = info;
            manager.openCamera(id, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    if(preview == null)
                        return;
                    startPublish(camera);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {}

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {}
            }, null);
            break;
        }
    }

} catch (CameraAccessException e) {
    e.printStackTrace();
}
```

[PublishCamera2Test.java #86](PublishCamera2Test.java#L86)

The `CameraCharacteristics` object, which refers to the camera you're passing to the publisher, must also be included, so it's best to retain it here.

Additionally, the thread that the camera returns on may not be the UI thread, hence why the publisher is partially set up before calling `openCamera`.

After receiving the camera, the steps are similar to initializing an `R5Camera` object, with the addition of the `CameraCharacteristics` object in the constructor.

Then the stream can be started.

```Java
camera2 = new R5Camera2(camera, camInfo, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
camera2.setBitrate(TestContent.GetPropertyInt("bitrate"));
camera2.setOrientation(90);
camera2.setFramerate(TestContent.GetPropertyInt("fps"));

publish.attachCamera(camera2);
```

[PublishCamera2Test.java #122](PublishCamera2Test.java#L122)

## Special Addendum - Capture Request Builder

* Not all manufacturers have implemented the functionality of Camera2 on their devices, so take care when creating a builder.
* While we have added the ability for developers to take advantage of the Camera2 API, most live stream use-cases are covered by the Camera API.
* The `R5Camera2.setCaptureRequestBuilder` will accept a `CaptureRequest.Builder` object and open a capture session with it. For any calculations that need to know the image format, we use `ImageFormat.YUV_420_888`.