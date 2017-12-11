## Requirements

* [Red5 Pro Server](#red5-pro-server)
* [Red5 Pro SDK License Key](#red5-pro-sdk-license-key)

### Red5 Pro Server
You will need a functional, running Red5 Pro server web- (or locally-) accessible for the client to connect to. If you already have a [Red5 Pro Account](https://account.red5pro.com), you can find the Red5 Pro Server download at [https://account.red5pro.com/download](https://account.red5pro.com/download).

> For more information visit [Red5Pro.com](https://red5pro.com).

### Red5 Pro SDK License Key
A Red5 Pro SDK License Key is required to use the iOS Mobile SDK. If you already have a [Red5 Pro Account](https://account.red5pro.com), you can find your Red5 Pro SDK License Key at [https://account.red5pro.com/overview](https://account.red5pro.com/overview).

> You will need to copy the `SDK License` into the `license_key` property field of the [tests.xml](app/src/res/raw/test.xml) resource file.

## Setup

You will need to modify **app/src/res/raw/test.xml (the domain value)** to point to your `host` server instance's IP address and update the `license_key` property to that of your Red5 Pro SDK License.  If you do not, the examples will not function when you build. If you are running the server locally, then your machine and mobile device need to be on the same WiFi network.

Once you have modified your settings, you can run the application for simulator or device.

> **If prompted to upgrade gradle, you should ignore.**

## Examples

### [Publishing](app/src/main/java/red5pro/org/testandroidproject/tests/PublishTest)

| **[1080p](app/src/main/java/red5pro/org/testandroidproject/tests/PublishTest)**
| :-----
| *A high quality publisher. Note that this is the publish test with a non-default 'bitrate' and camera size values set in tests.xml*
| ---
| **[ABR](app/src/main/java/red5pro/org/testandroidproject/tests/PublishABRTest)**
| *A high bitrate publisher with AdaptiveBitrateController*
| ---
| **[Authentication](app/src/main/java/red5pro/org/testandroidproject/tests/PublishAuthTest)**
| *An example of publishing a stream as an authenticated user*   
| ---
| **[Mute/Unmute](app/src/main/java/red5pro/org/testandroidproject/tests/PublishPauseTest)**
| *Touch the screen to toggle between sending Audio & Video, sending just Video, sending just Audio, and sending no Audio or Video. Turning off and on the media sources is considered mute and unmute events, respecitively*
| ---
| **[Camera Swap](app/src/main/java/red5pro/org/testandroidproject/tests/PublishCameraSwapTest)**
| *Touch the screen to swap which camera is being used! Verify using flash that camera is swapping properly and no rendering problems occur.*
| ---
| **[Custom Video Source](app/src/main/java/red5pro/org/testandroidproject/tests/PublishCustomSourceTest)**
| *Uses a custom controller to supply video data to the publisher.*
| ---
| **[Device Orientation](app/src/main/java/red5pro/org/testandroidproject/tests/PublishDeviceOrientationTest)**
| *After starting a broadcast, rotate your device from portrait to landscape. You will notice that the view updates on the broadcasting device. Additionally, if you subscribe with mobile or the browser-based players, you will see their orientation update with the change to device orientation.*
| ---
| **[HQ Audio](app/src/main/java/red5pro/org/testandroidproject/tests/PublishHQAudioTest)**
| *A publish example with high quality audio*
| ---
| **[Image Capture](app/src/main/java/red5pro/org/testandroidproject/tests/PublishImageTest)**
| *Touch the publish stream to take a screen shot that is displayed!*
| ---
| **[LocalRecord](app/src/main/java/red5pro/org/testandroidproject/tests/PublishLocalRecordTest)**
| *A publish example that records stream data locally on the device.*
| ---
| **[Orientation](app/src/main/java/red5pro/org/testandroidproject/tests/PublishOrientationTest)**
| *Touch the screen to rotate the output video 90 degrees. Verify with flash, android, or other iOS device running subscribe test.*
| ---
| **[Record](app/src/main/java/red5pro/org/testandroidproject/tests/RecordedTest)**
| *A publish example that records stream data on the server.*
| ---
| **[Remote Call](app/src/main/java/red5pro/org/testandroidproject/tests/PublishRemoteCallTest)**
| *The publish portion of the remote call example - sends the remote call.*
| ---
| **[Send](app/src/main/java/red5pro/org/testandroidproject/tests/PublishSendTest)**
| *An example of sending data and messages from a Broadcaster to N-Subscribers.*
| ---
| **[Stream Manager](app/src/main/java/red5pro/org/testandroidproject/tests/PublishStreamManagerTest)**
| *A publish example that connects with a server cluster using a Stream Manger*
| ---
| **[Two Way](app/src/main/java/red5pro/org/testandroidproject/tests/TwoWayTest)**
| *An example of simultaneously publishing while subscribing - allowing a conversation. Includes stream detection and auto-connection.*
| ---
| **[Shared Object](app/src/main/java/red5pro/org/testandroidproject/tests/SharedObjectTest)**
| *An example of sending data and messages between clients through remote shared objects.*

### [Subscribing](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeTest)

| **[Aspect Ratio](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeAspectTest)**
| :-----
| *Change the fill mode of the stream.  scale to fill, scale to fit, scale fill.  Aspect ratio should be maintained on first 2.*
| ---
| **[Authentication](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeAuth)**
| *An example of subscribing to a stream as an authenticated user*   
| ---
| **[Bandwidth Test](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeBandwidthTest)**
| *Detect Insufficient and Sufficient BW flags.  Test on a poor network using a publisher that has high video quality. Video should become sporadic or stop altogether.  The screen will darken when no video is being received.*
| ---
| **[Cluster](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeCluster)**
| *An example of conecting to a cluster server.*
| ---
| **[Image Capture](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeImageTest)**
| *Touch the subscribe stream to take a screen shot that is displayed!*
| ---
| **[No View](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeNoViewTest)**
| *A proof of using an audio only stream without attaching it to a view.*
| ---
| **[Reconnect](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeReconnectTest)**
| *An example of reconnecting to a stream on a connection error.*
| ---
| **[Remote Call](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeRemoteCallTest)**
| *The subscribe portion of the remote call example - receives the remote call.*
| ---
| **[Send](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeReceiveSendTest)**
| *An example of receiving data and messages from a Broadcaster using the `send` API.*
| ---
| **[Stream Manager](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeStreamManagerTest)**
| *A subscribe example that connects with a server cluster using a Stream Manger.*
| ---
| **[Two Streams](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeTwoStreamTest)**
| *An example of subscribing to multiple streams at once, useful for subscribing to a presentation hosted by two people using a Two Way connection.*

## Notes

1. For some of the above examples you will need two devices (a publisher, and a subscriber). You can also use a web browser to subscribe or publish via Flash.
2. You can see a list of active streams by navigating to http://your_red5_pro_server_ip:5080/live/streams.jsp
3. Click on the flash link (for example, flash_publisher) in the streams list displayed to view the published stream in your browser.

[![Analytics](https://ga-beacon.appspot.com/UA-59819838-3/red5pro/streaming-ios?pixel)](https://github.com/igrigorik/ga-beacon)
