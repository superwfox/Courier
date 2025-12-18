# Courier | [[English Documention]](https://github.com/superwfox/Courier/blob/master/README_EN.md)

Minecraft Paper 1.21 插件，实现 QQ 群与游戏服务器的双向通信桥接。

## 技术栈

- Java 11 / Paper API 1.21
- WebSocket (java-websocket) 连接 OneBot 协议
- Maven 构建 + Shade 打包

## 架构设计

```
┌─────────────────┐     WebSocket      ┌─────────────────┐
│   QQ Bot        │◄──────────────────►│   Courier       │
│   (OneBot)      │                    │   (Paper Plugin)│
└─────────────────┘                    └─────────────────┘
                                              │
                                              ▼
                                       ┌─────────────────┐
                                       │   CSV 存储      │
                                       │   (allowlist)   │
                                       └─────────────────┘
```

## 核心模块

| 文件 | 职责 |
|------|------|
| `Courier.java` | 插件入口，初始化 WebSocket 连接 |
| `OneBotWebsocket.java` | OneBot 协议实现，消息收发与指令解析 |
| `EventListener.java` | Bukkit 事件监听，处理玩家登录/退出/聊天 |
| `AllowList.java` | QQ-游戏ID 绑定逻辑 |
| `FileManager.java` | CSV 文件读写工具 |
| `IPsensor.java` | IP 归属地查询 + 异地登录踢出 |
| `LevelManager.java` | 等级数据查询 |
| `Picture.java` | 在线玩家列表图片生成 |

## 性能设计

### 事件驱动 vs 轮询

采用 Bukkit 事件监听机制而非定时轮询：

```java
@EventHandler
public void onPlayerJoin(PlayerPreLoginEvent e) { ... }
```

- 玩家登录/退出时触发，无空转开销
- 避免定时任务持续占用 CPU

### WebSocket 长连接

```java
client = new OneBotWebsocket(uri);
client.connectBlocking();
```

- 单一持久连接，避免 HTTP 轮询开销
- 消息即时推送，延迟 < 100ms

### 静态方法协作

```java
public static void sendG(String message) { ... }
public static void sendP(String user, String message) { ... }
```

- 跨类调用无需实例化，减少对象创建
- 内存占用更低，GC 压力更小

### 数据结构选择

```java
static ConcurrentHashMap<String, String> ChangeName = new ConcurrentHashMap<>();
static Set<String> bannedIP = new HashSet<>();
```

- `ConcurrentHashMap` 处理并发绑定请求，O(1) 查找
- `HashSet` 存储封禁 IP，O(1) 判断

## 图片生成模块

`Picture.java` 实现动态玩家列表图片生成：

```java
BufferedImage image = new BufferedImage(width, baseHeight, BufferedImage.TYPE_INT_ARGB);
Graphics2D g = image.createGraphics();
g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
```

### 设计要点

- **动态宽度计算**：根据玩家数量自适应画布尺寸
  ```java
  int width = Math.max(600, ((players.length - 1) / 6 + 1) * 320 + 140);
  ```
- **内存流输出**：直接转 Base64，避免磁盘 I/O
  ```java
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  ImageIO.write(image, "png", baos);
  String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
  ```
- **自定义像素字体**：从资源加载 TTF，保持视觉一致性
- **抗锯齿渲染**：`VALUE_ANTIALIAS_ON` 提升文字清晰度

## 功能特性

- QQ 群消息 ↔ 游戏内广播
- QQ 绑定游戏 ID（白名单机制）
- 异地登录检测 + IP 封禁
- 在线玩家列表图片生成
- 服务器状态查询（TPS/内存）
- 管理员远程执行命令

## 构建

```bash
mvn clean package
```

输出: `target/Courier-1.0-SNAPSHOT.jar`
