#Red5 Pro Android Streaming Examples

This repository contains a simple project with a number of examples that can be used for testing and reference.  

##Requirements

You will need a functional, running Red5 Pro server web- (or locally-) accessible for the client to connect to.  

For more information visit http://red5pro.com.

##Setup

You will need to modify **/app/src/main/res/values/values.xml (the domain value)** to point to your server instance.  If you do not, the examples will not function when you build.

Once you have modified your settings, you can run the application for simulator or device. 


##Examples


| **[Publishing](/app/src/main/java/com/red5pro/red5proexamples/examples/publish)**                 
| :-----
| *Starter example on publishing to a Red5 Pro stream* 
|
| **[Adaptive Bitrate Publishing](/app/src/main/java/com/red5pro/red5proexamples/examples/adaptivebitrate)**
| *Utilize the AdaptiveBitrateController to dynamically adjust video bitrate with connection quality*
| 
| **[Subscribing](/app/src/main/java/com/red5pro/red5proexamples/examples/subscribe)**
| *Starter example on subscribing to a Red5 Pro stream*  
|
| **[Stream Send](/app/src/main/java/com/red5pro/red5proexamples/examples/streamsend)**
| *Broadcast messages to subscribers with R5Stream.send*  
|
| **[AutoReconnect](/app/src/main/java/com/red5pro/red5proexamples/examples/reconnect)**
| *Wait for a publisher to start by monitoring connection events* 
|
| **[Two Way Video Chat](/app/src/main/java/com/red5pro/red5proexamples/examples/twoway)**
| *Starter example that shows how to implement a two way video chat*
     
##Notes

1. For some of the above examples you will need two devices (a publisher, and a subscriber). You can also use a web browser to subscribe or publish via Flash.
2. You can see a list of active streams by navigating to http://your_red5_pro_server_ip:5080/live/streams.jsp
3. Click on the flash link (for example, flash_publisher) in the streams list displayed to view the published stream in your browser.

