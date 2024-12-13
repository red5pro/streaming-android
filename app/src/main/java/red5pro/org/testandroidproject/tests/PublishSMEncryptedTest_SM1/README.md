# Encrypted Stream Manager Publishing

Using a stream manager doesn't prevent your streams from being encrypted - the same configuration can be used to protect the contents of your broadcasts in any server configuration.

## Example Code
- ***[PublishTest.java](../PublishTest/PublishTest.java)***
- ***[PublishStreamManagerTest.java](../PublishStreamManagerTest/PublishStreamManagerTest.java)***
- ***[PublishSMEncryptedTest.swift](PublishSMEncryptedTest.swift)***

## The One Change
The only change from the [basic Stream Manager example](../PublishStreamManagerTest/) is that value that the `protocol` setting of the configuration is set to. Setting the protocol to use `R5StreamProtocol.SRTP` is all you need to do to signal the SDK to negotiate an encrypted session.

```Java
R5Configuration config = new R5Configuration(R5StreamProtocol.SRTP,
```
<sup>
[PublishSMEncryptedTest.java #24](PublishSMEncryptedTest.java#L24)
</sup>

### Further Security Concerns
As with the basic Encryption example, it's suggested that some form of stream authentication is used. Additionally, it's suggested that the innitial negotiation to find the server to broadcast to be done over HTTPS (iOS defaults to HTTPS where available) to prevent a malicious party from publishing to the returned server first. Note - SRTP does not require the server to have an SSL certificate, and so this example is set to run without HTTPS to run on more servers, but HTTPS will require the server to be set up with an appropriate SSL certificate, and the port must not be added to the Stream Manager API request for it to connect correctly.
