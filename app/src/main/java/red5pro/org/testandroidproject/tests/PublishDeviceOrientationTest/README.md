# Publish Device Orientation

This example demonstrates sending orientation metadata to subscribers of a broadcast when the Publisher rotates their physical device.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishDeviceOrientationTest.java](PublishDeviceOrientationTest.java)***

## Running the example

After starting a broadcast, rotate your device from portrait to landscape. You will notice that the view updates on the broadcasting device. Additionally, if you subscribe with mobile or the browser-based players, you will see their orientation update with the change to device orientation.

## Implementation as Publisher

### Device Orientation project settings

The `configChanges="orientation|screenSize"` option was added to the root `Activity` in the `ActivityManifest`:

```xml
android:configChanges="orientation|screenSize"
```

[ActivityManifest.xml #28](../../../../../../ActivityManifest.xml#L28)

This will allow the view displaying the camera playback to update based on device orientation.

In order to respond to orientations from the device, the `onConfigurationChanged` method is overriden. This method is invoked upon orientation changes of the device. Values for rotation as they apply to the current Publisher display and the `orientation` property of the MetaData provided to subscribers are determined based on the device rotation:

```java
@Override
public void onConfigurationChanged(Configuration config) {
  super.onConfigurationChanged(config);

  int d_rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
  Log.d("PublishDeviceOrientTest", "d_rotation: " + d_rotation);

  int degrees = 0;
  switch (d_rotation) {
    case Surface.ROTATION_0: degrees = 0; break;
    case Surface.ROTATION_90: degrees = 270; break;
    case Surface.ROTATION_180: degrees = 180; break;
    case Surface.ROTATION_270: degrees = 90; break;
  }

  Log.d("PublishDeviceOrientTest", "degrees: " + degrees);
  camDisplayOrientation = (mOrigCamOrientation + degrees) % 360;
  camOrientation = d_rotation % 2 != 0 ? camDisplayOrientation - 180 : camDisplayOrientation;
  mOrientationDirty = true;
}
```

[PublishDeviceOrientationTest.java #57](PublishDeviceOrientationTest.java#L57)

This orientation values are not applied directly from this orientation handler as this is a pre-notification of orientation changes that will affect the layout.

Instead, we set a `mOrientationDirty` flag to know that orientation has changed, and we handle the application of these orientation values upon update to the layout.

### View.OnLayoutChangeListener

To respond to updates on the layout - which occur *after* an orientation notification, a `View.OnLayoutChangeListener` is created upon launch of the test:

```java
mLayoutListener = new View.OnLayoutChangeListener() {
  @Override
  public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
    Log.d("PublishDeviceOrientTest", "onLayoutChange");
    if (mOrientationDirty) {
      Log.d("PublishDeviceOrientTest", "dirty orientation");
      reorient();
    }
  }
};
```

[PublishDeviceOrientationTest.java #92](PublishDeviceOrientationTest.java#L92)

When the `Fragment` is notified of the layout change, the `mOrientationDirty` flag is checked. If we have been notified of an orientation change, then the `PublishDeviceOrientationTest:reorient` method is invoked to proceed.

```java
protected void reorient() {
  cam.setDisplayOrientation((camDisplayOrientation + 180) % 360);
  camera.setOrientation(camOrientation);
  mOrientationDirty = false;
}
```

In the `reorient` method, the display of the `Camera` playback is updated with a rotation as it relates to the current device; Android devices can have different base orientations.

As well, the `R5Camera` object is notified of the new rotation. It is from this `orientation` property that the MetaData value sent to all subscribers is derived.

## Implementation as Subscriber

When MetaData is received on the Subscriber, there is an `orientation` property values that relates to the current orientation of the Publisher stream.

For the Android Subscriber example, the `orientation` MetaData is parsed and applied as a `rotation` transform on the `R5VideoView` instance that is displaying the incoming stream:

```java
protected void updateOrientation(int value) {
  Log.d("SubscribeTest", "update orientation to: " + value);
  display.setRotation(value);
}

public void onMetaData(String metadata) {
  Log.d("SubscribeTest", "Metadata receieved: " + metadata);
  String[] props = metadata.split(";");
  for (String s: props) {
    String[] kv = s.split("=");
    if (kv[0].equalsIgnoreCase("orientation")) {
      updateOrientation(Integer.parseInt(kv[1]));
    }
  }
}
```

[SubscriberTest.java #66](../Subscribe/SubscriberTest.java#L6)
