# Subscribe Auto Reconnect Test

This example utilizes overriding the `onConnectionEvent` method to test for and attempt to recover from connection errors or stopped streams by resubscribing after some preconfigured amount of time. This example also allows for manually stopping the stream by tapping on it.

## Example Code
- **_[PublishTest.java](../PublishTest.java)_**
- **_[SubscribeReconnectTest.java](SubscribeReconnectTest.java)_**