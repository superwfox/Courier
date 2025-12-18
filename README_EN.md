# Courier

A Minecraft Paper 1.21 plugin that bridges QQ groups and game servers with bidirectional communication.

## Tech Stack

- Java 11 / Paper API 1.21
- WebSocket (java-websocket) with OneBot protocol
- Maven build + Shade packaging

## Architecture

```
┌─────────────────┐     WebSocket      ┌─────────────────┐
│   QQ Bot        │◄──────────────────►│   Courier       │
│   (OneBot)      │                    │   (Paper Plugin)│
└─────────────────┘                    └─────────────────┘
                                              │
                                              ▼
                                       ┌─────────────────┐
                                       │   CSV Storage   │
                                       │   (allowlist)   │
                                       └─────────────────┘
```

## Core Modules

| File | Responsibility |
|------|----------------|
| `Courier.java` | Plugin entry, WebSocket initialization |
| `OneBotWebsocket.java` | OneBot protocol, message handling & command parsing |
| `EventListener.java` | Bukkit event listener for player join/quit/chat |
| `AllowList.java` | QQ-GameID binding logic |
| `FileManager.java` | CSV file I/O utilities |
| `IPsensor.java` | IP geolocation lookup + suspicious login detection |
| `LevelManager.java` | Player level data query |
| `Picture.java` | Online player list image generation |

## Performance Design

### Event-Driven vs Polling

Uses Bukkit event listeners instead of scheduled polling:

```java
@EventHandler
public void onPlayerJoin(PlayerPreLoginEvent e) { ... }
```

- Triggers only on player join/quit, zero idle overhead
- Avoids CPU consumption from continuous polling tasks

### WebSocket Persistent Connection

```java
client = new OneBotWebsocket(uri);
client.connectBlocking();
```

- Single persistent connection, no HTTP polling overhead
- Real-time message push, latency < 100ms

### Static Method Collaboration

```java
public static void sendG(String message) { ... }
public static void sendP(String user, String message) { ... }
```

- Cross-class calls without instantiation, reduces object creation
- Lower memory footprint, less GC pressure

### Data Structure Selection

```java
static ConcurrentHashMap<String, String> ChangeName = new ConcurrentHashMap<>();
static Set<String> bannedIP = new HashSet<>();
```

- `ConcurrentHashMap` for concurrent binding requests, O(1) lookup
- `HashSet` for banned IPs, O(1) membership check

## Image Generation Module

`Picture.java` implements dynamic player list image generation:

```java
BufferedImage image = new BufferedImage(width, baseHeight, BufferedImage.TYPE_INT_ARGB);
Graphics2D g = image.createGraphics();
g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
```

### Design Highlights

- **Dynamic Width Calculation**: Canvas size adapts to player count
  ```java
  int width = Math.max(600, ((players.length - 1) / 6 + 1) * 320 + 140);
  ```
- **In-Memory Output**: Direct Base64 conversion, avoids disk I/O
  ```java
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  ImageIO.write(image, "png", baos);
  String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
  ```
- **Custom Pixel Font**: TTF loaded from resources for visual consistency
- **Anti-Aliased Rendering**: `VALUE_ANTIALIAS_ON` for sharper text

## Features

- QQ group messages ↔ In-game broadcast
- QQ binding to Game ID (whitelist mechanism)
- Remote login detection + IP banning
- Online player list image generation
- Server status query (TPS/Memory)
- Admin remote command execution

## Build

```bash
mvn clean package
```

Output: `target/Courier-1.0-SNAPSHOT.jar`
