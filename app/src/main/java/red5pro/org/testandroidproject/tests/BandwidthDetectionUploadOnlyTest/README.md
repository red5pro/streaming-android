# Bandwidth Detection (Upload) with Red5 Pro

This example shows how to easily detect upload bandwidth with Red5 Pro prior to publishing a stream.

## Example Code

* **_[TestDetailFragment.java](../../TestDetailFragment.java)_**
* **_[BandwidthDetectionUploadOnlyTest.java](BandwidthDetectionUploadOnlyTest.java)_**

## How to Check Bandwidth

Checking the upload bandwidth prior to publishing a stream is relatively simple, requiring only a few pieces of setup.

1. One must [instantiate an `R5BandwidthDetection` instance](BandwidthDetectionUploadOnlyTest.java#L92)
2. One must then [utilize the `checkUploadSpeed` method](BandwidthDetectionUploadOnlyTest.java#L94) of that instance
3. Doing so requires passing in:
	1. The base url (usually the same as the `host` you would provide to your `R5Configuration`)
	2. How long you wish the total bandwidth test to take, in seconds
	3. Callback blocks for the successful and unsuccessful attempts at checking the bandwidth

A simplified example of this would be:

```java
R5BandwidthDetection detection = new R5BandwidthDetection(this); // Or your listener that implements R5BandwidthDetection.CallbackListener
try {
		detection.checkUploadSpeed("your-host-here", 2.5);
} catch (Exception e) {
		e.printStackTrace();
}
```

The rest of this example is based on [PublishTest.java](../PublishTest/) and ensures that a [minimum bandwidth](BandwidthDetectionUploadOnlyTest.java#L214) (as [defined by the `tests.xml` file](../../../../../../res/raw/tests.xml#L3)) is met prior to publishing to the stream.
