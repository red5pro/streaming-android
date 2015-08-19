#Publishing on Red5 Pro

This is the basic starter example on publishing to a Red5 Pro stream. 

###Example Code
- ***[PublishExample.java](PublishExample.java)***


##How to Publish
Publishing to a Red5 Pro stream requires a few components to function fully.
####Setup R5Connection
The R5Connection manages the connection that the stream utilizes.  You will need to setup a configuration and intialize a new connection.

```Java
//Create the configuration from the values.xml
R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
R5Connection connection = new R5Connection(config);
```
<sup>
[PublishExample.java #40](PublishExample.java#L40)
</sup>

####Setup R5Stream
The `R5Stream` handles both subscribing and publishing.  Creating one simply requires the connection already created.

```Java
 //setup a new stream using the connection
 publish = new R5Stream(connection);

```
<sup>
[PublishExample.java #44](PublishExample.java#L44)
</sup>

####Attach a Video Source
The R5Stream will need a video and/or audio source to stream from.  To attach a video source, you will need to create an `R5Camera` with the `Camera` you wish to stream from.

```Java
//attach a camera video source
cam = openFrontFacingCameraGingerbread();
cam.setDisplayOrientation(90);

R5Camera camera  = new R5Camera(cam, 320, 240);
camera.setBitrate(res.getInteger(R.integer.bitrate));
camera.setOrientation(-90);

publish.attachCamera(camera);
```
<sup>
[PublishExample.java #50](PublishExample.java#L50)
</sup>

`R5Camera.width` and `R5Camera.height` specify the encoded video size to be streamed.  `R5Camera` will choose the video format that is closest to this resolution from the camera.

`R5Camera.orientation` provides meta information to the stream for presentation on the client.  The video is not rotated by the device.  A value of **90** will provide portrait orientation on receiving devices.

####Attach an Audio Source
To add audio to a stream a `R5Microphone` object can be attached.  It behaves similarly to `R5Camera`, but requires `R5Stream.attachMic` instead.

```Java
//attach a microphone
R5Microphone mic = new R5Microphone();
publish.attachMic(mic);

```
<sup>
[PublishExample.java #60](PublishExample.java#L60)
</sup>

#### Preview the Publisher
The `R5VideoView` will present publishing streams as well as subscribed streams.  To preview a publishing stream, it simply needs to attach the `R5Stream`.   

***This is not required to publish - but allows for previewing the stream.***

```Java
R5VideoView r5VideoView =(R5VideoView) view.findViewById(R.id.video);
r5VideoView.attachStream(publish);

publish.setView(r5VideoView);
```
<sup>
[PublishExample.java #66](PublishExample.java#L66)
</sup>

To view start preview, simply use `Camera.startPreview`.

```Java
cam.startPreview();
```
<sup>
[PublishExample.java #73](PublishExample.java#L73)
</sup>

####Start Publishing
The `R5Stream.publish` method will establish the server connection and begin publishing.  

```Java
publish.publish(getString(R.string.stream1), R5Stream.RecordType.Live);
```
<sup>
[PublishExample.java #71](PublishExample.java#L71)
</sup>

The *type* parameter tells the server the recording mode to use on the server.

- **RecordType.Live** - Stream but do not record
- **RecordType.Record** - Stream and record the file name.  Replace existing save.
- **RecordType.Append** - Stream and append the recording to any existing save.

