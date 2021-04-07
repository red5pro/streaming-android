# Publish Local Record

`R5Stream.beginLocalRecording` captures a local copy of a stream to the device's camera roll.

### Example Code

- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishLocalRecordTest.java](PublishLocalRecordTest.java)***

## Running the example

Open the example, and the app will begin streaming and recording. Close the app to end both, and then open the Photos app to see your newly recorded content.

## Using R5Stream.beginLocalRecording

`R5Stream.beginLocalRecording` triggers the SDK to begin passing data from the camera and microphone - if they are attached - to a file writer. Once the stream has ended, the created file will be passed to the phone's camera roll. Once streaming, simply call:

```Java
publish.beginLocalRecording(getActivity().getApplicationContext(), "testRecord");
```

To end the recording before ending the stream, simply call `R5Stream.endLocalRecording`

## Record Quality

There is a second `record` function that takes a map object so that the recording doesn't need to use the same settings as the broadcast - which can be especially important if the broadcast needs to transmit over a poor network. Unfortunately video size, frame rate, and audio sample rate are determined at capture, but bitrate for audio and video can both be set at the encoder. Passing a map to `R5Stream.beginLocalRecordingWithProperties` with an int value for the `R5Stream.VideoBitrateKey` or `R5Stream.AudioBitrateKey` or both, will set that value for recording, separate from the associated values for the stream.

```Java
Map<String, Integer> props = new HashMap<>();
props.put(R5Stream.VideoBitrateKey, TestContent.GetPropertyInt("bitrate") * 2);
props.put(R5Stream.AudioBitrateKey, publish.getAudioSource().getBitRate() * 2);

publish.beginLocalRecordingWithProperties(getActivity().getApplicationContext(), "testRecord", props);
```

[PublishLocalRecordTest.java #15](PublishLocalRecordTest.java#L21)

Note that this example as it is won't show a massive change in quality between what's streamed and what's recorded - the bitrate is already set to an appropriate value for the size. For a better representation of the difference, setting a high resolution in `tests.plist` and increasing the multiplier for the `vidRate` will make the difference more apparent.

## Saving to an Album

In order to have videos save to a different album in Android's photo app, all you need to do is add a folder in front of the file name of the video. Android determines which album to save to based on the name of the containing folder. By default, videos are saved to the public "Movies" folder - and thus the "Movies" album, but by specifying a path in front of the name, the SDK will create the appropriate folders, and Android will send the videos to the appropriate albums.

For instance, if you wanted the above example to save to the `r5pro` album, you would pass `"r5pro/testRecord"` instead.

## Android 10+

Android 10 introduced new security measures that limited where on the device an app can handle files - in specific, apps can no longer access the externalStoragePublicDirectory. Instead, all files made by an app must be stored in their own local directory. By default, the local record is placed in the internal, but you can also add a specific path as a string with the `R5Stream.RecordDirectoryKey` key in the properties object.

Note that not every location will be able to be scanned into the Photos app, so please test all locations you intend to use in production before distributing it. If you don't want the video sent to the user's media roll, you can disable the media scanner by setting `R5Stream.RecordMediaScanKey` to `false` in the properties object.

## A Note about Android Jellybean

Local Record relies primarily on MediaMuxer - which was added in Android API 18 - partway through Jellybean's update. In order to support api 16 and 17, the mp4parser library needs to be added to your project.

```
compile group: 'com.googlecode.mp4parser', name: 'isoparser', version: '1.1.22'
```

[build.gradle #56](../../../../../../../build.gradle#L56)

Also note that due to the different muxing method, this form of recording can only use half of a device's remaining storage, where the API 18+ could record a video file that takes up the entirety of the device.
