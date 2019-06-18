# Remote Procedure Calls

`R5Stream.send` allows the publisher to send messages to the server to be sent to all subscribers.

### Example Code

- ***[PublishRemoteCallTest.java](PublishRemoteCallTest.java)***
- ***[SubscribeRemoteCallTest.java](../SubscribeRemoteCallTest/SubscribeRemoteCallTest.java)***

## Running the example

Two devices are required to run this example.  One as a publisher, and the other as a subscriber. 

Connect the first device (publisher) with the Publish - Remote Call example. On the second device (subscriber) use the Subscribe - Remote Call example.

Touch the preview on the publisher screen to display a label on the subscriber screen where the publisher touched.

## Using the R5Stream send

Once the stream has connected you are able to dispatch messages to any connected subscribers.  Sending the message is a simple call that takes a map object:

```Java
publish.send("whateverFunctionName", map);
```

[PublishRemoteCallTest.java #40](PublishRemoteCallTest.java#L40)

### Send Message Format

The publisher send takes any object that implements the Map interface that uses a string as both the key and value. This example uses a Hashtable.

```Java
Hashtable<String, String> map = new Hashtable<String, String>();
map.put("key", "value");
```

The R5RemoteCallContainer will automatically handle turning the map into a strin of the following format, which is how it will be received by the subscriber's client.

```Java
"key1=value1;key2=value2;key3=value3;"
```

## Receiving R5Stream send calls

In order to handle `R5Stream.send` calls from the publisher, the `R5Stream.client` delegate must be set.  This delegate will receive all `R5Stream.send` messages via appropriately named methods.

```Java
subscribe.client = this;
```

[SubscribeRemoteCallTest.java #27](../SubscribeRemoteCallTest/SubscribeRemoteCallTest.java#L27)

Because the publisher will be sending **whateverFunctionName**, the subscriber client delegate will need a matching method signature. As the name implies, the function can be named anything as long as it is publicly accessible. All methods receive a single string argument containing the variable map provided by the publisher.  This map can easily be parsed.

```Java
public void whateverFunctionName( String message ){

	System.out.println("Recieved message from publisher: " + message);

	String[] parsedMessage = message.split(";");
	Hashtable<String, String> map = new Hashtable<String, String>();
	for (String s : parsedMessage) {
		String key = s.split("=")[0];
        String value = s.split("=")[1];
        System.out.println("Received key: " + key + "; with value: " + value);

        map.put(key,value);
    }
```

[SubscribeRemoteCallTest.java #30](../SubscribeRemoteCallTest/SubscribeRemoteCallTest.java#L30)
