# Publish Mute/Unmute

The `R5Stream:restrainAudio` and `R5Stream:restrainVideo` methods allow for a broadcast stream to be muted and unmuted of audio and video separately.

### Example Code

- ***[PublishPauseTest.java](PublishPauseTest.java)***

## Running the example

The `PublishPauseTest` launches in a broadcast session with Audio & Video inputs enabled and streaming.

Touch the screen at any time while streaming to toggle between muted and unmuted states of each Audio and Video input. Subscribe to the stream on another device to see how the muted states affect the broadcast.

The toggle sequence is as follows when you tap the screen multiple times:

1. The first tap will mute the audio being sent.
2. The second tap will unmute the audio from _Tap 1_ and mute the video.
3. The third tap will mute the audio again - muting both video and audio at the same time.
4. The fourth tap will unmute both audio and video, returning to its original state on launch of test and broadcast.

## Using RStream:restrainAudio and R5Stream:restrainVideo

`R5Stream:restrainAudio` and `R5Stream:restrainVideo` are methods that can be invoked to mute or unmute the audio and video of a stream, repectively. They each take a `boolean` argument. Passing `true` will mute the media in the stream, passing `false` will unmute the media.

```java
preview.setOnTouchListener(new View.OnTouchListener() {
  @Override
  public boolean onTouch(View v, MotionEvent event) {

    if (event.getAction() == MotionEvent.ACTION_DOWN) {

        muteEnum = muteEnum + 1;
        if (muteEnum > 3) {
            muteEnum = 0;
        }

        switch (muteEnum) {
            case 1:
                // mute audio
                publish.restrainAudio(true);
                publish.restrainVideo(false);
                Log.d("PublisherPause", "Mute Audio");
                break;
            case 2:
                // mute video
                publish.restrainAudio(false);
                publish.restrainVideo(true);
                Log.d("PublisherPause", "Mute Video");
                break;
            case 3:
                // mute audio & video
                publish.restrainAudio(true);
                publish.restrainVideo(true);
                Log.d("PublisherPause", "Mute Audio & Video");
                break;
            case 0:
                // unmute audio & video
                publish.restrainAudio(false);
                publish.restrainVideo(false);
                Log.d("PublisherPause", "Umute all");
                break;
        }

    }

    return true;
  }
});
```

[PublishPauseTest.java #31](PublishPauseTest.javat#L31)

## Listening for mute on a Subscriber stream

Calling the `R5Stream:restrainAudio` and `R5Stream:restrainVideo` methods change the `streamingMode` value of the metadata that is additionally broadcast to subscribers of the stream. As a subscriber, you can listen for their respective mute and unmute states of a broadcast stream from the status codes defined for [R5ConnectionListener](https://www.red5pro.com/docs/static/android-streaming/interfacecom_1_1red5pro_1_1streaming_1_1event_1_1_r5_connection_listener.html).

