#Two Way Video Chat

This example demonstrates two way communication using Red5 Pro.  It also demonstrates using Remote Procedure Calls (RPC) on the server.

###Example Code
- ***[TwoWayVideoChatExample.java](TwoWayExample.java)***

###Setup
Two way communication simply requires setting up a publish stream and a subscribe stream at the same time.  You can test the example with two devices.  On the second device select the **Swap Names** toggle on the main screen before launching. 

Once the streams have populated the table you can choose the other device by its name.

###Getting Live Streams
You can make RPC calls to the server using `R5Connection.call`.  The call is similar to `R5Stream.send` but allows you to specify a return method name.

`streams.getLiveStreams` is a built in RPC in all Red5 Pro servers.  This call will return a string value that contains a json array of all streams that are currently publishing.

```Java
    //call out to get our new stream
    self.publish.connection.call(new R5RemoteCallContainer("streams.getLiveStreams", "R5GetLiveStreams", null));
```

The return method will be called on the `R5Stream.client`.  The client will need a method that matches the signature of the return.  Since `streams.getLiveStreams` returns a string, a void method with a single string parameter will handle the result.

```Java
public void R5GetLiveStreams:(String streams){

    System.out.println("Got the streams: "+streams);

        //parse string as JSON
        JSONArray names;
        try {
            names = new JSONArray(streams);
        } catch (Exception e) {
            System.out.println("Failed to parse streams to JSONArray");
            return;
        }

        //Look for the other stream, subscribe when available
        for(int i  = 0; i < names.length(); i++){
            try {
                if(getStream2().equals(names.getString(i))){
                    subscribe.play(getStream2());
                    listThread.interrupt();
                    return;
                }
            } catch (Exception e){
                System.out.println("Item at index " + i + " cannot be retrieved as a String");
            }
        }

}
```
A simple json parsing will get all streams, then loop through the list of streams and connect to the other stream name when it is available.


<!--###Detecting Video Loss
A common feature of two way video chat is handling when video does not have enough bandwidth to broadcast properly.  This example displays a profile icon over the subscription stream when this is detected.  You can force this to happen by setting **showVideo** to NO in connection.plist on your other device.

In order to detect if video is currently being streamed, the `R5Stream.client` can implement `-(void)onR5PublishStateNotification:(NSString*)value;`
This method returns a mapped value of properties related to the publishing stream - and is called by the remote publisher on a fixed interval.

One of the keys in the state notification is `streamingMode`.  This example shows that if **"Video"** is not found in the streamMode, display the profile icon until it is broadcasting video again.

```Objective-C
-(void)onR5PublishStateNotification:(NSString*)value{
   
    NSArray *pairs = [value componentsSeparatedByString:@";"];
   
    for(int i=0;i<pairs.count;i++){
       
        NSArray *keyvalue = [[pairs objectAtIndex:i] componentsSeparatedByString:@"="];
        
        if(keyvalue.count > 1){

            NSString *key = [keyvalue objectAtIndex:0];
            NSString *val = [keyvalue objectAtIndex:1];
            
            //show or hide profile overlay if "streamingMode" contains "Video"
            if([key isEqualToString:@"streamingMode"]){
               
                if([val rangeOfString:@"Video"].location == NSNotFound){
                    
                    self.profileView.hidden = NO;
                    [self.subscribeR5View.view bringSubviewToFront:self.profileView];
                    
                }else{
                    
                    self.profileView.hidden = YES;
                }
            }
            
        }
    }
    
}
```-->