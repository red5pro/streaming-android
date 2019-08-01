# Publishing High Quality Audio

`R5AudioController.sampleRate` allows the user to increase the number of times per second that a device grabs a signal from the microphone. Higher values can improve the quality of a broadcast.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishHQAudioTest.java](PublishHQAudioTest.java)***

## Using R5Microphone.sampleRate

`R5AudioController.sampleRate` is by default set to 8000 - or 8khz, which is decent for most applications, but far from archival quality, especially when it comes to music. Note that setting this value higher will also require a higher bitrate. Increasing the bitrate without increasing the sample rate will only get you so far in reducing compression on the audio, but increasing the sample rate without increasing the bitrate will increase how much each sample is compressed, which could lead to a worse sound.

```Java
mic.setBitRate(128);//kbps
R5AudioController.getInstance().sampleRate = 44100;//hz (samples/second)
```

[PublishHQAudioTest.java #65](PublishHQAudioTest.java#L65)

This must be set before you start publishing, and changing the value after calling `R5Stream.publish` will not effect the sample rate.

## Receiving HQ Audio

Android has issues playing audio from raw data, and thus currently can't play back audio at sample rates other than 8khz. Other sample rates will be up or downsampled to play at that rate.

## Two Way and HQ Audio

Two way applications require echo cancellation to minimize feedback, and in order for the device to cancel playback that's received by the microphone, the two signals need to meet certain settings. In order for it to work correctly, the sample rates of the publisher and subscriber should be the same, and since playback is locked to 8khz, the publisher should be as well.
