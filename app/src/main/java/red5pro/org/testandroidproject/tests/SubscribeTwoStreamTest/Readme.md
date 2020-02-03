# Subscribe To Two Streams

This example demonstrates Subscribing to two different sources at once.

### Example Code

- ***[SubscribeTwoStreamTest.java](SubscribeTwoStreamTest.java)***

## Setup

This is intended to be used with two others using the two-way example to put on a presentation, allowing anyone using this client to watch them converse.

### Managing Streams

Of special note for subscribing to two streams at once is that each stream needs its own R5AudioController. Note that the first stream can still use the default R5AudioController, but any additional streams need a separate one.

```Java
secondSubscribe.audioController = new R5AudioController();
```

[SubscribeTwoStreamTest.java #79](SubscribeTwoStreamTest.java#L79)

In addition, each stream needs a small ammount of time in between calls to subscribe.

```Java
new Thread(new Runnable() {
  @Override
  public void run() {

    try {
      Thread.sleep(1000);

            Looper.prepare();

            R5Configuration config2 = new R5Configuration(R5StreamProtocol.RTSP,
              TestContent.GetPropertyString("host"),
              TestContent.GetPropertyInt("port"),
              TestContent.GetPropertyString("context"),
              TestContent.GetPropertyFloat("subscribe_buffer_time"));

      R5Connection secondConnection = new R5Connection(config2);

            secondSubscribe = new R5Stream(secondConnection);

            secondDisplay.attachStream(secondSubscribe);
            secondDisplay.showDebugView(TestContent.GetPropertyString("debug_view").equals("true"));

      secondSubscribe.audioController = new R5AudioController();
            secondSubscribe.play(TestContent.GetPropertyString("stream2"));

    }catch (Exception e){
      e.printStackTrace();
        }
  }
}).start();
```

[SubscribeTwoStreamTest.java #57](SubscribeTwoStreamTest.java#L57)