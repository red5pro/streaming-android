# Publish Camera2

Android's Camera2 api offers a robust suit of controls for the camera, but it needs to be handled significatnly differently than the deprecated Camera object. While most applications can use the standard Camera object for its simplicity, this is an example of using Camera2.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishCamera2Test.java](Publish2Test.java)***

## Using R5Camera2

Note that the Camera2 interface was added in Android API 21, with additional features included in API 22 - meaning that the minimum API for this class is higher than the rest of our SDK.

The biggest change that you'll notice from the base publish example is that unlike a camera, a CameraDevice can't be opened syncronously, and so the creation of the R5Camera2 object and triggering of the publisher has to be done after Android has returned the device to you.
To call for the device, you have to get the CameraManager, ask that for the list of cameras, identify the camera you want, and then open the camera from the manager.

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

Two things to note -

* One, the CameraCharacteristics object to refer to the camera you're passing to the publisher must also be passed, so it's best to retain it here.
* Two, the thread that the camera returns on won't necesarily be the UI thread, hence why the publisher is mostly set up before calling openCamera.

After recieving the camera, the steps are similar to initializing an R5Camera object, with the addition of the CameraCharacteristics object in the constructor.

```Java
camera2 = new R5Camera2(camera, camInfo, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
camera2.setBitrate(TestContent.GetPropertyInt("bitrate"));
camera2.setOrientation(90);
camera2.setFramerate(TestContent.GetPropertyInt("fps"));

publish.attachCamera(camera2);
```

[PublishCamera2Test.java #122](PublishCamera2Test.java#L122)

After that, the stream can be started as normal.

## Special Addendum - Capture Request Builder

In addition to the basic functionality to mirror our implementation of the Camera interface, we have also added the ability for developers to take advantage of the advanced features of the Camera2 api. To reiterate, these additional features won't be of benefit to 99% of live streaming use cases, but the option is available for those few cases that need it.

R5Camera2.setCaptureRequestBuilder will accept a CaptureRequest.Builder object and open a capture session with it. For any calculations that need to know the image format, we are using ImageFormat.YUV_420_888. Note that most of the advanced functionality of Camera2 has not been implemented by the manufactures of most phones, so take care when creating a builder.
