# Bandwidth Detection with Red5 Pro

This example shows how to easily detect upload and download bandwidth with Red5 Pro prior to beginning a stream.

## Example Code

* **_[TestDetailFragment.java](../../TestDetailFragment.java)_**
* **_[BandwidthDetectionTest.java](BandwidthDetectionTest.java)_**

## How to Check Bandwidth

Checking the bandwidth (download and upload simultaneously) prior to beginning a stream is relatively simple, requiring only a few pieces of setup.

1. One must [instantiate an `R5BandwidthDetection` instance](BandwidthDetectionTest.java#L75)
2. One must then [utilize the `checkSpeeds` method](BandwidthDetectionTest.java#L77) of that instance
3. Doing so requires passing in:
	1. The base url (usually the same as the `host` you would provide to your `R5Configuration`)
	2. How long you wish the total bandwidth test to take, in seconds
	3. Callback blocks for the successful and unsuccessful attempts at checking the bandwidth

A simplified example of this would be:

```java
R5BandwidthDetection detection = new R5BandwidthDetection(this); // Or your listener that implements R5BandwidthDetection.CallbackListener
try {
		detection.checkSpeeds("your-host-here", 2.5);
} catch (Exception e) {
		e.printStackTrace();
}
```

The rest of this example is based on [SubscribeTest.java](../SubscribeTest/) and ensures that a [minimum bandwidth](BandwidthDetectionTest.java#L158) (as [defined by the `tests.xml` file](../../../../../../res/raw/tests.xml#L3)) is met prior to subscribing to the stream.
