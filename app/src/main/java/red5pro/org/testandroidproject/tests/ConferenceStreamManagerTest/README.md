# Conference Chat

This example demonstrates multi-party communication using Red5 Pro. It should be used in conjunction with a conference WebSocket host such as [this example](https://github.com/red5pro/red5pro-conference-host).

It is recommended to view this example as part of the `webrtcexamples` webapp shipped with the [Red5 Pro Server](https://account.red5pro.com/download).

## Basic Publisher

**Please refer to the [Basic Publisher Documentation](../PublishTest/README.md) to learn more about the basic setup of a publisher.**

## Basic Subscriber

**Please refer to the [Basic Subscriber Documentation](../SubscribeTest/README.md) to learn more about the basic setup of a subscriber.**

## Example Code

- **[ConferenceTest.java](ConferenceTest.java)**
- **[WebSocketProvider.java](WebSocketProvider.java)**

# Setup

## WebSocket Conference Host

The `WebSocket Conference Host` refers to the socket endpoint that manages the list of active streams and their scopes for a given conference session.

We have provided a basic example at [https://github.com/red5pro/red5pro-conference-host](https://github.com/red5pro/red5pro-conference-host).

The endpoint for the `WebSocket Conference Host` is defined in the **tests.xml** as the `conference_host` property. By default it is set to a local IP address and port on your network (e.g., `ws://10.0.0.75:8001`). Change this to either the local IP or the remote IP of the machine that you launch the `WebSocket Conference Host` on.

> The reason it is defined as a local IP on your network and not `localhost` is because `localhost` would refer to the actual device that the testbed is launched on. We assume you would not also be running the `WebSocket Conference Host` NodeJS server on your iOS device :)

Once a publish session has begun, a connection to the `WebSocket Conference Host` is established and messages with regards to active stream listing are handled:

```java
public void connectSocket () {
  final String sRoomName = this.roomName;
  String host = TestContent.GetPropertyString("conference_host")
                .replace("http:", "ws:")
                .replace("https:", "wss:");
  String url = host + "?room=" + sRoomName + "&streamName=" + this.pubName;
  mWebSocketProvider = new WebSocketProvider(url);
  mWebSocketProvider.connect(new WebSocketProvider.OnWebSocketListener() {
    @Override
    public void onWebSocketMessage(String room, ArrayList<String> streams) {
      if (room.equals(sRoomName)) {
        String streamNames = TextUtils.join(",", streams);
        stringToQueue(streamNames);
      }
    }
  });
}
```

## Java-WebSocket

By default, the WebSocket implementation used is the [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) Java library. It is installed via `gradle`.

