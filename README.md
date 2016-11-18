# Red5 Pro Android Streaming Testbed

This repository contains a simple project with a number of examples that can be used for testing and reference.  

##Requirements

You will need a functional, running Red5 Pro server web- (or locally-) accessible for the client to connect to.  

For more information visit http://red5pro.com.

##Setup

You will need to modify **/app/src/main/res/raw/tests.xml (the domain value)** to point to your server instance.  If you do not, the examples will not function when you build.

Once you have modified your settings, you can run the application for simulator or device.

##Examples

###[Publishing](app/src/main/java/red5pro/org/testandroidproject/tests/PublishTest)

| **[1080p](app/src/main/java/red5pro/org/testandroidproject/tests/PublishTest)**                 
| :-----
| *A high quality publisher. Note that this is the publish test with a non-default 'bitrate' and camera size values set in tests.xml* 
|
| **[ABR](app/src/main/java/red5pro/org/testandroidproject/tests/PublishABRTest)**
| *A high bitrate publisher with AdaptiveBitrateController*   
|
| **[Camera Swap](app/src/main/java/red5pro/org/testandroidproject/tests/PublishCameraSwapTest)**
| *Touch the screen to swap which camera is being used! Verify using flash that camera is swapping properly and no rendering problems occur.*
|
| **[Custom Video Source](app/src/main/java/red5pro/org/testandroidproject/tests/PublishCustomSourceTest)**
| *Uses a custom controller to supply video data to the publisher.*
|
| **[Image Capture](app/src/main/java/red5pro/org/testandroidproject/tests/PublishImageTest)**
| *Touch the publish stream to take a screen shot that is displayed!*  
|
| **[Orientation](app/src/main/java/red5pro/org/testandroidproject/tests/PublishOrientationTest)**
| *Touch the screen to rotate the output video 90 degrees.  Verify with flash, iOS, or other android device running subscribe test.*   
|
| **[Record](app/src/main/java/red5pro/org/testandroidproject/tests/RecordedTest)**
| *A publish example that records stream data on the server.*
|
| **[Remote Call](app/src/main/java/red5pro/org/testandroidproject/tests/PublishRemoteCallTest)**
| *The publish portion of the remote call example - sends the remote call.*
| 
| **[Stream Manager](app/src/main/java/red5pro/org/testandroidproject/tests/PublishStreamManagerTest)**
| *A publish example that connects with a server cluster using a Stream Manger*
|
| **[Two Way](app/src/main/java/red5pro/org/testandroidproject/tests/TwoWayTest)**
| *An example of simultaneously publishing while subscribing - allowing a conversation. Includes stream detection and auto-connection.*

###[Subscribing](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeTest)

| **[Aspect Ratio](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeAspectTest)**                
| :-----
| *Change the fill mode of the stream.  scale to fill, scale to fit, scale fill.  Aspect ratio should be maintained on first 2.* 
|
| **[Bandwidth Test](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeBandwidthTest)**
| *Detect Insufficient and Sufficient BW flags.  Test on a poor network using a publisher that has high video quality. Video should become sporadic or stop altogether.  The screen will darken when no video is being received.*  
|
| **[Cluster](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeCluster)** 
| *An example of conecting to a cluster server.*
|
| **[Image Capture](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeImageTest)** 
| *Touch the subscribe stream to take a screen shot that is displayed!*
|
| **[No View](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeNoViewTest)** 
| *A proof of using an audio only stream without attaching it to a view.*
|
| **[Remote Call](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeRemoteCallTest)**
| *The subscribe portion of the remote call example - receives the remote call.*
| 
| **[Stream Manager](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeStreamManagerTest)**
| *A subscribe example that connects with a server cluster using a Stream Manger.*   
|
| **[Two Streams](app/src/main/java/red5pro/org/testandroidproject/tests/SubscribeTwoStreamTest)**
| *An example of subscribing to multiple streams at once, useful for subscribing to a presentation hosted by two people using a Two Way connection.*
     
##Notes

1. For some of the above examples you will need two devices (a publisher, and a subscriber). You can also use a web browser to subscribe or publish via Flash.
2. You can see a list of active streams by navigating to http://your_red5_pro_server_ip:5080/live/streams.jsp
3. Click on the flash link (for example, flash_publisher) in the streams list displayed to view the published stream in your browser.

[![Analytics](https://ga-beacon.appspot.com/UA-59819838-3/red5pro/streaming-ios?pixel)](https://github.com/igrigorik/ga-beacon)