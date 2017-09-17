# Rocketchat modern client

This project aims to provide a simple java client for the [Rocket.Chat](https://rocket.chat) live chat api.
The current focus is on ease of usability and solid core functionality over complete method coverage (PRs always welcome!).

## Read first
Make sure you have at least some rough understanding of [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)
and [RxJava](https://github.com/ReactiveX/RxJava) as these are used heavily for the client api.

## Setup
Publishing via Maven Central is in progress: https://issues.sonatype.org/browse/OSSRH-34576 .
When this is completed, you should be able to include the client as Maven dependency in your `pom.xml` via:

```xml
<dependency>
    <groupId>com.github.daniel-sc</groupId>
    <artifactId>rocketchat-modern-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Usage

Get subscriptions/rooms:
```java
try(RocketChatClient client = new RocketChatClient("wss://demo.rocket.chat:443/websocket", USERNAME, PASSWORD)) {
    List<Subscription> subscriptions = client.getSubscriptions().join();
}
```

Send message:
```java
try(RocketChatClient client = new RocketChatClient(URL, USERNAME, PASSWORD)) {
    ChatMessage msg = client.sendMessage("Your message", roomId).join();
}
```

Stream/read messages:
```java
try(RocketChatClient client = new RocketChatClient(URL, USERNAME, PASSWORD)) {
    Observable<ChatMessage> msgStream = client.streamRoomMessages(roomId).join();
    msgStream.forEach(msg -> System.out.println("received msg: " + msg));
}
```

## Websocket API
This client ships with [Tyrus](https://github.com/tyrus-project/tyrus)
websocket reference implementation.

If you like, you can replace the websocket library with any 
other JSR-356 complient implementation of [WebSocket Server API](https://mvnrepository.com/artifact/javax.websocket/javax.websocket-api).
Just update your `pom.xml` as follows:
```xml
<dependency>
    <groupId>com.github.daniel-sc</groupId>
    <artifactId>rocketchat-modern-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>org.glassfish.tyrus.bundles</groupId>
            <artifactId>tyrus-standalone-client-jdk</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>some.websocket-api.impl</groupId>
    <artifactId>some.websocket-api.impl</artifactId>
    <version>VERSION</version>
</dependency>
```