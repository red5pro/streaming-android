#Published Stream Recording

`R5Stream.RecordType.Record` signals for the server to record the stream.

###Example Code
- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[RecordTest.java](RecordTest.java)***

##Recording
The only difference between this example and the publish test is that in the publish command you send a different RecordType flag:

```Java
publish.publish(TestContent.GetPropertyString("stream1"), R5Stream.RecordType.Record);
```
<sub>
[RecordTest.java #64](RecordTest.java#L64)
</sub>