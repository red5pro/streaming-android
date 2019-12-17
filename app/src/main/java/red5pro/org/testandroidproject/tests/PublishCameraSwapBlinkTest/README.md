# Publish Camera Swap

This example expands on the `Camera Swap` test by providing a customized form of the camera object to clear the screen while the camera device is being swapped. The front and back cameras of most devices are flipped from each other, meaning changes in camera selection often include changes in orientation. Since the changed metadata and new video data won't arrive at exactly the same time, this example is meant to prevent the subscriber from seeing upside-down images when the changes don't happen simultaneously.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[../PublishCameraSwapTest](PublishCameraSwapTest)***
- ***[PublishCameraSwapBlinkTest.java](PublishCameraSwapBlinkTest.java)***

## Running the example

Touch the screen at any time while streaming to switch between broadcasting from the front facing and back facing camera.

## Replacing Video Data

To display a black frame instead of the image from either camera, muting the video isn't enough - the last image sent will still be displayed. Instead, the image heading to the encoder needs to be replaced with the data that's supposed to be displayed instead. While intercepting the data in `onPreviewFrame` is an option for the R5Camera, it's easier - and more universally aplicable - to replace the data as it's passed to the encoder.

```Java
@Override
public synchronized void encode(byte[] input, long time, boolean reset) {
  // Replacing data here instead of onPreviewFrame to prevent interrupting the output buffer swaps
  if(coverCam){
    input = blackData;
  }

  super.encode(input, time, reset);
}
```

[PublishCameraSwapBlinkTest.java #129](PublishCameraSwapBlinkTest.java#L129)

`blackData` in this case is just an array of bytes that's been pre-populated with the yuv data for a black frame of the appropriate size. This technique can be used to display graphics for intermissions or other cases as well.

After setting the camera to a black frame, it's important to wait at least 2 frames before and after swapping cameras, to give the system enough time to capture and apply the changes. Also remember that many devices don't allow apps to access the camera unless the code that's doing so is running on the main thread. 

```Java
((R5BlinkCamera)camera).coverCam = true;

// Wait for two frames, guarantees at least one black frame is sent pre- and post- swap
final int waitMS = 2000/camera.getFramerate();

System.out.println(" TESTING - On Publish Touch - " + waitMS + "ms set");

new Thread(new Runnable() {
  @Override
  public void run() {
    try {
        Thread.sleep(waitMS);
    }catch (Exception e){
        return;
    }

    Handler mainLoop = new Handler(Looper.getMainLooper());
    mainLoop.post(new Runnable() {
      @Override
      public void run() {

        if(publish == null){
          return;
        }

        PublishCameraSwapBlinkTest.super.onPublishTouch(e);

        System.out.println(" TESTING - On Publish Touch - swapping");

        new Thread(new Runnable() {
          @Override
          public void run() {
            try{
              Thread.sleep(waitMS);
            } catch (Exception e) {
              return;
            }
            if(publish == null){
                return;
            }

            ((R5BlinkCamera)camera).coverCam = false;

            System.out.println(" TESTING - On Publish Touch - done");

            swapping = false;
          }
        }).start();
      }
    });
  }
}).start();
```

[PublishCameraSwapBlinkTest.java #28](PublishCameraSwapBlinkTest.java#L28)