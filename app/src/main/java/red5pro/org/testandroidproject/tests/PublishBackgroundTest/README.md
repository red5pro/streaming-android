# Background Subscribing

The Red5Pro SDK is capable of running in the background, allowing people to multitask without needing to disconnect from their stream.

### Example Code

- ***[PublishTest.java](../SubscribeTest/PublishTest.java)***
- ***[PublishBackgroundTest.java](PublishBackgroundTest.java)***
- ***[PublishService.java](PublishService.java)***

## Running the example

Subscribe to this stream from any other device to see that audio continues to be streamed even when the example is in the background.

Note that closing the app won't disconnect the stream - as that's the point of the example. In order to end the stream, either the example needs to be closed by tapping the "End" button in the bottom right of the view, or the app needs to be ended completely by removing it from the active apps list. Just be aware that this method of background activity could continue without the app in the active list, but for this example, cleanup was added to the `onDestroy` method of the test.

## A note on "Background"

This example is intended to allow the stream to continue while the user is using other apps on the device. Most Android devices will also allow audio streams while the device's screen has turned off, and many will also permit services to run unimpeded when the device is explicitly locked. However, some OEMs restrict network or CPU usage while the screen is off, and especially when the device is locked - even to the point of ignoring wake locks in rare cases.

Given this varience in the implementation of Android in general, while we can say that the background example will permit multi-tasking, we can't guarantee that it will continue to function in all states while the device's screen is off.

## Using Background Services

By default, when an app loses focus, it's moved into the background and suspended. In order to preserve functionality, anything that you want to have continue has to be run in a service. This needs to be declared in the app manifest alongside other activity declarations. The flow is as follows:

onCreateView -> startService -> bindService (with flag BIND_IMPORTANT)

Using `startService` instead of setting the flag to create it in `bindService` allows it to function separately from the calling activity. The `BIND_IMPORTANT` flag allows the service to signal to Android that it needs to stay in focus.

Note that on binding, the service returns a customized `Binder` object to pass a reference to the service object to the rest of the activity, which allows the R5VideoView - which needs to be with the ui in the main activity - to be passed to the R5Stream - which needs to be in the service to continue.

Since streaming is a processor and memory intensive task, the service that surrounds the stream needs to call `startForeground` while the rest of the app is in the background. This allows the app to continue functioning in the foreground memory pool by connecting it to the Notification passed into the `startForeground` function.

```Java
holderNote = (new Notification.Builder(getApplicationContext()))
        .setContentTitle("R5Testbed")
        .setContentText("Publishing from the background")
        .setSmallIcon(R.drawable.ic_launcher)
        .build();
startForeground(57234111, holderNote);
```

[PublishService.swift #105](PublishService.swift#L105)

In order to not get in the way of the main UI thread, `stopForeground` needs to be called when the app is brought back into focus.
Once the service is no longer required, it needs to be unbound and stopped, otherwise it will continue until the app is completely stopped.

```Java
public void onStop() {
...
if(shouldClean()) {
    getActivity().unbindService(pubServiceConnection);
    getActivity().stopService(pubIntent);
```

[PublishBackgroundTest.swift #101](PublishBackgroundTest.swift#L101)

There are of course other methods to manage services, and this may not be the best for your needs. It is the way this example uses, and has been tested to work, but we encourage you to explore other methods if you're familiar with service management.

Note - in general Android does not permit background graphics processing. Attempts to run video in the background may cause the service to be forcibly suspended.

## Disabling Graphics Processing

Most implementations of Android crack down on graphics processing from unbound services - meaning GL calls and video encode/decode actions in general, as well as specifically using the camera. Sometimes this means that Camera objects are forcibly released, while others are just stopped. To handle such situations, it's best to call `stopPreview()` and `release()` on the camera (note: android.hardware.Camera, not the R5Camera) and then open and connect it again when the app returns to the foreground. With some devices, you also need to ensure that the previous camera isn't referenced when it reconnects to the view by using R5Camera.setCamera(null). Also note that to prevent the stream from waiting on new video frames before sending audio, `R5Stream.restrainVideo(true)` should be called before stopping the camera, then again (with false) after reconnecting the camera so that video can continue.
To be more clear, this is the full disconnect process to prevent errors:

```Java
publish.restrainVideo(true);
cam.stopPreview();
cam.release();
camera.setCamera(null);
cam = null;
```

[PublishService.swift #129](PublishService.swift#L129)

