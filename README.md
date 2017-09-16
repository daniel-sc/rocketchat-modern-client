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

Connect and login:
```java
CompletableFuture<String> loginToken = subscriptions = client.connect()
    .thenCompose(session -> client.login(USER, PASSWORD));
```

Get subscriptions/rooms:
```java
List<Subscription> subscriptions = loginToken
    .thenCompose(token -> client.getSubscriptions())
    .join();
```

Send message:
```java
ChatMessage msg = loginToken
    .thenCompose(token -> client.sendMessage("Your message", roomId))
    .join();
```

Stream/read messages:
```java
Observable<ChatMessage> msgStream = loginToken
    .thenApply(token -> client.streamRoomMessages(roomId))
    .join();
msgStream.forEach(msg -> System.out.println("received msg: " + msg));
```