# Background Subscribing

The Red5Pro SDK is capable of running in the background, allowing people to multitask without needing to disconnect from their stream.

### Example Code

- ***[SubscribeTest.java](../SubscribeTest/SubscribeTest.java)***
- ***[SubscribeBackgroundTest.java](SubscribeBackgroundTest.java)***
- ***[SubscribeService.java](SubscribeService.java)***

## Running the example

Begin by publishing to **stream1** from a second device.  **stream1** is the default stream1 name that is used by this example.

Note that closing the app won't disconnect the stream - as that's the point of the example. In order to end the stream, either the example needs to be closed by tapping the "End" button in the bottom right of the view, or the app needs to be ended completely by removing it from the active apps list. Just be aware that this method of background activity could continue without the app in the active list, but for this example, cleanup was added to the `onDestroy` method of the test.

## Using Background Services

By default, when an app loses focus, it's moved into the background and suspended. In order to preserve functionality, anything that you want to have continue has to be run in a service. This needs to be declared in the app manifest alongside other activity declarations. The flow is as follows:

onCreateView -> startService -> bindService (with flag BIND_IMPORTANT)

Using `startService` instead of setting the flag to create it in `bindService` allows it to function separately from the calling activity. The `BIND_IMPORTANT` flag allows the service to signal to Android that it needs to stay in focus.
Note that on binding, the service returns a customized `Binder` object to pass a reference to the service object to the rest of the activity, which allows the R5VideoView - which needs to be with the ui in the main activity - to be passed to the R5Stream - which needs to be in the service to continue.
Since streaming is a processor and memory intensive task, the service that surrounds the stream needs to call `startForeground` while the rest of the app is in the background. This allows the app to continue functioning in the foreground memory pool by connecting it to the Notification passed into the `startForeground` function.

```Java
holderNote = (new Notification.Builder(getApplicationContext()))
        .setContentTitle("R5Testbed")
        .setContentText("Streaming from the background")
        .setSmallIcon(R.drawable.ic_launcher)
        .build();
startForeground(7335776, holderNote);
```

[SubscribeService.swift #105](SubscribeService.swift#L105)

In order to not get in the way of the main UI thread, `stopForeground` needs to be called when the app is brought back into focus.
Once the service is no longer required, it needs to be unbound and stopped, otherwise it will continue until the app is completely stopped.
```Java
public void onStop() {
...
if(shouldClean()) {
    getActivity().unbindService(subServiceConnection);
    getActivity().stopService(subIntent);
```

[SubscribeBackgroundTest.swift #101](SubscribeBackgroundTest.swift#L101)

There are of course other methods to manage services, and this may not be the best for your needs. It is the way this example uses, and has been tested to work, but we encourage you to explore other methods if you're familiar with service management.

> Note - in general Android does not permit background graphics processing. Attempts to run video in the background may cause the service to be forcibly suspended.

## Disabling Graphics Processing

To prevent app culling, the function `deactivate_display` has been added to R5Stream. This needs to be called when the app is about to enter the background, caught by overriding the `onStop` function. Then in `onResume`, the method `activate_display` needs to be called on the R5Stream so that the SDK will resume decoding video frames.

Note that if you're playing a stream that isn't attached to a view, but can't guarantee that the source won't be pushing any video frames, make sure to still call `deactivate_display` to ensure that incoming video frames are ignored instead of decoded. If the publisher that you're subscribed to hasn't added a video source, this call won't change anything, but otherwise, the fact that frames don't have a view to be pushed to may not stop Android from culling the service for processing them.
