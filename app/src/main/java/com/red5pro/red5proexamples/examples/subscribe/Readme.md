#Subscribing on Red5 Pro

This example shows how to easily subscribe to a Red5 Pro stream.

###Example Code
- ***[SubscribeExample.java](SubscribeExample.java)***


##How to Subscribe
Subscribing to a Red5 Pro stream requires a few components to function fully.
####Setup R5Connection
The R5Connection manages the connection that the stream utilizes.  You will need to setup a configuration and intialize a new connection.

```Java
//Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
        R5Connection connection = new R5Connection(config);
```
<sup>
[SubscribeExample.java #41](SubscribeExample.java#L41)
</sup>

####Setup R5Stream
The `R5Stream` handles both subscribing and publishing.  Creating one simply requires the connection already created.

```Java
	 //setup a new stream using the connection
    subscribe = new R5Stream(connection);
```

<sup>
[SubscribeExample.java #44](SubscribeExample.java#L44)
</sup>

#### Preview the Subscriber
The `R5VideoViewController` will present publishing streams as well as subscribed streams.  To view the subscribing stream, it simply needs to attach the `R5Stream`.  

```Java
	 //find the view and attach the stream
     R5VideoView r5VideoView =(R5VideoView) view.findViewById(R.id.video);
     r5VideoView.attachStream(subscribe);
```

<sup>
[SubscribeExample.java #44](SubscribeExample.java#L44)
</sup>

####Start Subscribing
The `R5Stream.play` method will establish the server connection and begin playing the stream through the R5VideoView.  

```Java
    subscribe.play(getString(R.string.stream1)); 
```
<sup>
[SubscribeExample.java #57](SubscribeExample.java#L57)
</sup>
