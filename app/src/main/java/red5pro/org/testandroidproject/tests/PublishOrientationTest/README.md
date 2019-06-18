# Publish Orientation

`R5Camera.orientation` allows the user to rotate the video source for a stream without interupting the broadcast.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishOrientationTest.java](PublishOrientationTest.java)***

## Running the example

Touch the screen at any time while streaming to rotate the video source by 90 degrees. It's sugested that you verify this change with a separate device.

## Using R5Camera.orientation

`R5Camera.orientation` will tell you how much the current video source is rotated from how it's coming into the application. By getting the instance of R5Camera attached to the R5Stream object and changing its orientation property, this can be modified live for the stream. Once streaming, simply call:

```Swift
R5Camera publishCam = (R5Camera)publish.getVideoSource();
publishCam.setOrientation( publishCam.getOrientation() + 90 );
```

[PublishOrientationTest.java #30](PublishOrientationTest.java#L30)