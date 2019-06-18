# Publishing on Red5 Pro

This is the basic starter example on publishing to a Red5 Pro stream. 

### Example Code

- ***[PublishTest.java](PublishTest.java)***

## How to Publish

Publishing to a Red5 Pro stream requires a few components to function fully.

### Setup R5Connection

The `R5Connection` manages the connection that the stream utilizes.  You will need to setup a configuration and intialize a new connection.
```Java
R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
	TestContent.GetPropertyString("host"),
	TestContent.GetPropertyInt("port"),
	TestContent.GetPropertyString("context"),
	TestContent.GetPropertyFloat("publish_buffer_time"));
R5Connection connection = new R5Connection(config);
```

[PublishTest.java #44](PublishTest.java#L44)

### Setup R5Stream

The `R5Stream` handles both subscribing and publishing.  Creating one simply requires the connection already created.

```Java
//setup a new stream using the connection
publish = new R5Stream(connection);
```

[PublishTest.java #51](PublishTest.java#L51)

### Attach a Video Source

The R5Stream will need a video and/or audio source to stream from.  To attach a video source, you will need to create an `R5Camera` with the device you wish to stream from.

```Java
//attach a camera video source
cam = openFrontFacingCameraGingerbread();
//Cameras can be installed in one of two orientations, so in order to support all devices, the orientation needs to be relative to the initial orientation of the camera.
cam.setDisplayOrientation( (camOrientation + 180) % 360 );

//Resolution can only be set as part of this constructor
R5Camera camera  = new R5Camera(cam, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
//Specify the max bitrate to allow
//Note : This bitrate will not be respected if it is lower than the encoder can go!
camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
//Setup the rotation of the video stream.  This is meta data, and is used by the client to rotate the video.  No rotation is done on the publisher.
camera.setOrientation(camOrientation);
```

[PublishTest.java #57](PublishTest.java#L57)

```Java
//Add the camera to the stream
publish.attachCamera(camera);
```

[PublishTest.java #74](PublishTest.java#L74)

`R5Camera.width` and `R5Camera.height` specify the encoded video size to be streamed.  `R5Camera` will choose the video format that is closest to this resolution from the camera.

`R5Camera.orientation` provides meta information to the stream for presentation on the client.  The video is not rotated by the device.  A value of **90** will provide portrait orientation on receiving devices.

### Attach an Audio Source

To add audio to a stream a `R5Microphone` object can be attached.  It behaves similarly to `R5Camera`, but requires `R5Stream.attachAudio` instead.

```Java
//attach a microphone
R5Microphone mic = new R5Microphone();

publish.attachMic(mic);
```

[PublishTest.java #65](PublishTest.java#L65)

### Preview the Publisher

The `SurfaceView` will present publishing streams.  To preview a publishing stream, it simply needs to attach the `R5Stream`.  

***This is not required to publish - but allows for previewing the stream.***

A `SurfaceView` can be set in any View, or created programmatically

```Java
preview = (SurfaceView)rootView.findViewById(R.id.videoPreview);
```

[PublishTest.java #70](PublishTest.java#L70)

To view the preview before publishing has started, use `R5VideoViewController.showPreview`.

Lastly, we attach the Stream to the R5VideoView to see the streaming content.

```Java
publish.setView(preview);
```

[PublishTest.java #72](PublishTest.java#L72)

### Start Publishing

The `R5Stream.publish` method will establish the server connection and begin publishing.  

```Java
publish.publish(TestContent.GetPropertyString("stream1"), R5Stream.RecordType.Live);
```

[PublishTest.java #76](PublishTest.java#L76)

The *type* parameter tells the server the recording mode to use on the server.

- **R5RecordTypeLive** - Stream but do not record
- **R5RecordTypeRecord** - Stream and record the file name.  Replace existing save.
- **R5RecordTypeAppend** - Stream and append the recording to any existing save.

### View your stream

Open a browser window and navigate to http://your_red5_pro_server_ip:5080//live/streams.jsp to see a list of active streams. Click on the _flash version to subscribe to your stream.