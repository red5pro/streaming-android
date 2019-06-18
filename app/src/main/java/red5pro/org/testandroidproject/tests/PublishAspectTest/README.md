# Publisher Aspect Ratio

`R5VideoViewController.scaleMode` controls the display mode of the content that is being pushed to it. Depending on the value the content will scale to the appropriate fill value.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishAspectTest.java](PublishAspectTest.java)***

## Running the example

Touch the screen at any time while streaming to change the scale mode, affecting how the stream is display on the user's end.

## Using scaleMode

R5VideoViewController.scaleMode has 3 potential enum values.

```sh
r5_scale_to_fill: scale to fill and maintain aspect ratio (cropping will occur)
r5_scale_to_fit: scale to fit inside view (letterboxing will occur)
r5_scale_fill: scale to fill view (will not respect aspect ratio of video)
```

By default, this value is `r5_scale_to_fill` and the android SDK handles this enum through raw int value (0,1,2) This example cycles through these values when it receives a tap.

```Java
int sMode = subscribe.getScaleMode();

sMode++;
//A value of 3 or larger won't parse correctly to the enum, so it's reset to 0
if(sMode == 3) sMode = 0;

subscribe.setScaleMode(sMode);
```

[PublishAspectTest.java #34](PublishAspectTest.java#L29)
