# Subscribing on Red5 Pro

This example shows how to easily subscribe to a Red5 Pro stream.

### Example Code

- ***[SubscribeTest.java](SubscribeTest.java)***

## How to Subscribe

Subscribing to a Red5 Pro stream requires a few components to function fully.

### Setup R5Connection

The R5Connection manages the connection that the stream utilizes.  You will need to setup a configuration and intialize a new connection.

```Java
//Create the configuration from the tests.xml
R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
	TestContent.GetPropertyString("host"),
	TestContent.GetPropertyInt("port"),
	TestContent.GetPropertyString("context"),
	TestContent.GetPropertyFloat("subscribe_buffer_time"));
R5Connection connection = new R5Connection(config);
```

[SubscribeTest.java #33](SubscribeTest.java#L33)

### Setup R5Stream

The `R5Stream` handles both subscribing and publishing.  Creating one simply requires the connection already created.

```Java
//setup a new stream using the connection
subscribe = new R5Stream(connection);
```

[SubscribeTest.java #41](SubscribeTest.java#L41)

The `R5StreamDelegate` that is assigned to the `R5Stream` will receive status events for that stream, including connecting, disconnecting, and errors.

### Preview the Subscriber

The `R5VideoView` will present subscribed streams.  To view the subscribing stream, it simply needs to attach the `R5Stream`.  

A `R5VideoView` can be set in any View, or created programmatically

```Java
display = (R5VideoView) view.findViewById(R.id.videoView);
```

[SubscribeTest.java #48](SubscribeTest.java#L48)

Lastly, we attach the Stream to the R5VideoView to see the streaming content.

```Java
display.attachStream(subscribe);
```

[SubscribeTest.java #49](SubscribeTest.java#L49)

### Start Subscribing

The `R5Stream.Subscribe` method will establish the server connection and begin Subscribing.  

```Java   subscribe.play(TestContent.GetPropertyString("stream1"));
```

[SubscribeTest.java #51](SubscribeTest.java#L51)

