#Auto Reconnection and Events

This example demonstrates using the status updates from `R5Stream`.  A timer is used to attempt to reconnect to a stream that has not yet been published.

To use the example: 
1. Launch the Auto Reconnect feature in the app and you will see it attempting to connect to stream 'subscriber'.  
2. Launch a second device using the publisher app, and use the "swap names" button to publish a stream with the name "subscriber" (alternatively, you can publish via the flash client running on your Red5 Pro server, at http://your_red5_pro_server_ip:5080/live/broadcast.jsp )
3. After you have started the broadcast, your app will successfully connect to the active stream.
 


###Example Code
- ***[AutoReconnectExample.java](/AutoReconnectExample.java)***


##Handling Status Updates from R5Stream
When the `R5Stream.delegate` is set, all events will be sent through the  `R5StreamDelegate.onConnectionEvent(R5ConnectionEvent r5ConnectionEvent)` method.  

The status code passed in is an `R5ConnectionEvent` enum.

This example receives the status event, and if there was a connection error, will start a timer to reconnect the stream in 8 seconds.  The message included with the error will display in a toast in the super method.

```Java

	public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        if ( r5ConnectionEvent == R5ConnectionEvent.ERROR )
        {
            retryThread = new Thread(new Runnable() {
                @Override
                public void run() {
                if(!Thread.interrupted() && subscribe != null){

                    try{
                        Thread.sleep(8000);

                        subscribe.stop();
                        subscribe.play(getStream1());
                    }catch(Exception e){
                        System.out.println("failed to reconnect");
                    }
                }
                }
            });
            retryThread.start();
       }
    }

```
<sup>
[ReconnectExample.java #65](/ReconnectExample.java#L65)
</sup>

