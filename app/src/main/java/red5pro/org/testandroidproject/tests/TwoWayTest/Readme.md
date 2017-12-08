#Two Way Video Chat

This example demonstrates two way communication using Red5 Pro.  It also demonstrates using Remote Procedure Calls (RPC) on the server.

## Notice
It is _required_ for setting up Publishers in a Two-Way session that the `sample_rate` configuration property be a value of `8000`.

###Example Code
- ***[TwoWayTest.java](TwoWayTest.java)***

###Setup
Two way communication simply requires setting up a publish stream and a subscribe stream at the same time.  You can test the example with two devices.  On the second device select the **Swap Names** toggle on the main screen before launching. 

The subscriber half will automatically connect when the second person begins streaming.

###Getting Live Streams
You can make RPC calls to the server using `R5Connection.call`.  The call is similar to `R5Stream.send` but allows you to specify a return method name.

`streams.getLiveStreams` is a built in RPC in all Red5 Pro servers.  This call will return a string value that contains a json array of all streams that are currently publishing.

```Java
    //call out to get our new stream
    publish.connection.call(new R5RemoteCallContainer("streams.getLiveStreams", "R5GetLiveStreams", null));
```
<sup>
[TwoWayTest.java #103](TwoWayTest.java#L103)
</sup>

The return method will be called on the `R5Stream.client`.  The client will need a method that matches the signature of the return.  Since `streams.getLiveStreams` returns a string, a void method with a single string parameter will handle the result.

```Java
public void R5GetLiveStreams(String streams){
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
			if(TestContent.GetPropertyString("stream2").equals(names.getString(i))){
				getActivity().runOnUiThread(new Runnable() {
                	@Override
                	public void run() {
                   		onSubscribeReady();
                	}
                });
				listThread.interrupt();
				return;
			}
		} catch (Exception e){
		System.out.println("Item at index " + i + " cannot be retrieved as a String");
	}
}
```
<sup>
[TwoWayTest.java #119](TwoWayTest.java#L119)
</sup>

A simple json parsing will get all streams, then loop through the list of streams and connect to the other stream name when it is available.

Note that onSubscribeReady needs to be run in the UiThread specifically, otherwise it will throw an error as there is no guarantee that R5GetLiveStreams will be called on that branch.

