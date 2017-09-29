#Publish Local Record

`R5Stream.beginLocalRecording` captures a local copy of a stream to the device's camera roll.

###Example Code
- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishLocalRecordTest.java](PublishLocalRecordTest.java)***

##Running the example
Open the example, and the app will begin streaming and recording. Close the app to end both, and then open the Photos app to see your newly recorded content.

##Using R5Stream.beginLocalRecording
`R5Stream.beginLocalRecording` triggers the SDK to begin passing data from the camera and microphone - if they are attached - to a file writer. Once the stream has ended, the created file will be passed to the phone's camera roll. Once streaming, simply call:

```Swift
publish.beginLocalRecording(getActivity().getApplicationContext(), "testRecord");
```
<sub>
[PublishLocalRecordTest.java #15](PublishLocalRecordTest.java#L15)
</sub>

To end the recording before ending the stream, simply call `R5Stream.endLocalRecording`
