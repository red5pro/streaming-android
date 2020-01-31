# Subscribe Volume Test

`R5AudioController.setPlaybackGain` can be used to adjust the playback volume of a stream. The range is from `0.0` to `1.0`.

### Example Code

- ***[SubscribeSetVolumeTest.java](SubscribeSetVolumeTest.java)***

## Running the example

1. Begin by publishing to **stream1** from a second device.  **stream1** is the default stream1 name that is used by this example.
2. Use the slider UI to adjust the playback volume of the stream.

## Using setPlaybackGain

`R5AudioController.setPlaybackGain:` takes a value from `0.0` to `1.0`. In this example, the value is changed based on the user input from the slider control.

```java
slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        float value = i/100f;
        Log.d("Subscriber", ":setVolume=" + value);
        subscribe.audioController.setPlaybackGain(value);
    }
}
```

[SubscribeSetVolumeTest.java #46](SubscribeSetVolumeTest.java#L46)
