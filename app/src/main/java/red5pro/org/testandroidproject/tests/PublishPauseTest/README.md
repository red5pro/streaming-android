# Publish Mute/Unmute

The `R5Stream:pauseAudio` and `R5Stream:pauseVideo` mutable properties allow for a broadcast stream to be muted and unmuted of audio and video separately.

### Example Code
- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishPauseTest.java](PublishPauseTest.java)***

## Running the example
The `PublishPauseTest` launches in a broadcast session with Audio & Video inputs enabled and streaming.

Touch the screen at any time while streaming to toggle between muted and unmuted states of each Audio and Video input. Subscribe to the stream on another device to see how the muted states affect the broadcast.

The toggle sequence is as follows when you tap the screen multiple times:

1. The first tap will mute the audio being sent.
2. The second tap will unmute the audio from _Tap 1_ and mute the video.
3. The third tap will mute the audio again - muting both video and audio at the same time.
4. The fourth tap will unmute both audio and video, returning to its original state on launch of test and broadcast.

## Using RStream:pauseAudio and R5Stream:pauseVideo
`R5Stream:pauseAudio` and `R5Stream:pauseVideo` are mutable properties that can be set to the desired boolean value:

* `true` will mute the media in real time (during a live broadcast).
* `false` will unmute the media in real time (during a live broadcast).

```java
private boolean onSubscribeTouch( MotionEvent e ){

  if(e.getAction() == MotionEvent.ACTION_DOWN ) {
    boolean hasAudio = !this.publish.getAudioPaused();
    boolean hasVideo = !this.publish.getVideoPaused();

    if (hasAudio && hasVideo) {
        this.publish.setAudioPaused(true);
        this.publish.setVideoPaused(false);
        postNotification("Pausing Audio");

    }
    else if (hasVideo && !hasAudio) {
        this.publish.setAudioPaused(false);
        this.publish.setVideoPaused(true);
        postNotification("Pausing Video");
    }
    else if (!hasVideo && hasAudio) {
        this.publish.setAudioPaused(true);
        this.publish.setVideoPaused(true);
        postNotification("Pausing Audio/Video");
    }
    else {
        this.publish.setAudioPaused(false);
        this.publish.setVideoPaused(false);
        postNotification("Resuming Audio/Video");
    }

  }

  return true;
}
```
[PublishPauseTest.java #57](PublishPauseTest.java#L57)

## Listening for mute on a Subscriber stream
Setting the `R5Stream:pauseAudio` and `R5Stream:pauseVideo` attribute value on a broadcast stream change the `streamingMode` value of the metadata that is additionally broadcast to subscribers of the stream. As a subscriber, you can listen for their respective mute and unmute states of a broadcast stream from the status codes defined for [R5ConnectionListener:onConnectionEvent](https://www.red5pro.com/docs/static/android-streaming/interfacecom_1_1red5pro_1_1streaming_1_1event_1_1_r5_connection_listener.html).
