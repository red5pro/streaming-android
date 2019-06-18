# Publish Image Capture

`R5Stream.getStreamImage` allows the user to capture a screenshot of the stream at any time.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishImageTest.java](PublishImageTest.java)***

## Running the example

Touch the screen at any time while streaming to popup a temporary overlay containing the UIImage that is returned from the Red5 Pro SDK.

## Using getStreamImage

`R5Stream.getStreamImage` returns a UIImage containing a screenshot of the current stream. The image dimensions match the incoming stream dimensions, and contain RGB data. Once streaming, simply call:

```Java
Bitmap streamImage = publish.getStreamImage();
```
<sub>
[PublishImageTest.java #44](PublishImageTest.java#L44)
</sub>

The UIImage can be saved to disk, displayed with a UIImageView, or processed in any way that is needed.