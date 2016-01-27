#Auto Reconnection and Events

`R5Stream.send` allows the publisher to send messages to the server to be sent to all subscribers.


###Example Code
- ***[StreamSendExample.java](StreamSendExample.java)***

##Running the example
Two devices are required to run this example.  One as a publisher, and the other as a subscriber. 

Connect the first device (publisher) and make sure the Toggle **Swap Names** is *NOT* selected.  Select the **StreamSend** option to begin publishing.

Connect the second device (subscriber) and toggle the **Swap Names** to on.  This will let the example match the name of the publisher application, and notify the example that this is the subscriber.

Touch the screen to log an event with the message value on the subscriber.


##Using the R5Stream send
Once the stream has connected you are able to dispatch messages to any connected subscribers.  Sending the message is a simple call that takes a map object:

```Java

	publish.send(new R5RemoteCallContainer("onStreamSend", map));

```
<sup>
[StreamSendExample.java #85](StreamSendExample.java#L85)
</sup>

###Send Message Format
The publisher send takes any object that inherits from the Map class that uses a string as both the key and value. This example uses a Hashtable.

```Java
Hashtable<String, String> map = new Hashtable<String, String>();
map.put("value", "A simple string");
```

The R5RemoteCallContainer will automatically handle turning the map into a strin of the following format, which is how it will be received by the subscriber's client.
```Java
"key1=value1;key2=value2;key3=value3;"
```

##Receiving R5Stream send calls
In order to handle `R5Stream.send` calls from the publisher, the `R5Stream.client` delegate must be set.  This delegate will receive all `R5Stream.send` messages via appropriately named methods.

```Java
subscribe.client = this;
subscribe.setListener(this);
```
<sup>
[StreamSendExample.java #101](StreamSendExample.java#L101)
</sup>

Because the publisher will be sending **onStreamSend**, the subscriber client delegate will need a matching method signature.  All methods receive a single string argument containing the variable map provided by the publisher.  This map can easily be parsed.

```
public void onStreamSend( String received ) {

    String[] parsedReceive = received.split(";");
    System.out.println("Received data from publisher:");
    for (String s : parsedReceive) {
        String key = s.split("=")[0];
        String value = s.split("=")[1];
        System.out.println("Received key: " + key + "; with value: " + value);
    }
}
```
<sup>
[StreamSendExample.java #130](StreamSendExample.java#L130)
</sup>
