# Subscriber Bandwidth Test

`onConnectionEvent` is a method called by the R5Stream on its assigned `R5ConnectionListener` object. This function allows a developer to gain status information events to monitor the stream.

### Example Code

- ***[SubscribeTest.java](../SubscribeTest/SubscribeTest.java)***
- ***[SubscribeBandwidthTest.java](SubscribeBandwidthTest.java)***

## Running the example

Begin by publishing to **stream1** from a second device.  **stream1** is the default stream1 name that is used by this example.

While streaming, upon receipt of a flag indicationg that the current net connection has insufficient bandwidth to properly display the stream, the stream view will appear to darken. If it instead receives a flag saying there is enough bandwidth, the screen will return to normal.

To see the effects, the stream should be published in high quality, and the example should be run from a poor network.

## Using onR5StreamStatus

`onConnectionEvent` is a method of the `R5ConnectionListener` interface. Any object that impliments this interface can be assigned to the stream with the `R5Stream.setListener` method of an active stream. In this example, `SubscribeBandwidthTest` impliments `R5ConnectionListener` allowing us to use the object as the delegate for its own stream.

```Java
subscribe.setListener(this);
```

[SubscribeBandwidthTest.java #22](SubscribeBandwidthTest.java#L22)

In order to add functionality, the `onConnectionEvent` function from `R5ConnectionListener` needs to be overridden. This overridden function can then parse the message sent to it to determine what action, if any, needs to be taken.

```Java
public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
  if ( R5ConnectionEvent.NET_STATUS.value() == r5ConnectionEvent.value() ) {
    if( r5ConnectionEvent.message == "NetStream.Play.SufficientBW" ){
      overlay.setAlpha( 0f );
    }
    else if( r5ConnectionEvent.message == "NetStream.Play.InSufficientBW" ){
      overlay.setAlpha( 0.5f );
    }
  }
}
```

[SubscribeBandwidthTest.java #36](SubscribeBandwidthTest.java#L36)