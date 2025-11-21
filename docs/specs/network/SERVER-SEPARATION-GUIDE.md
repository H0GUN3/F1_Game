# Server Separation Guide - 서버 분리 구현 가이드

> 기존 프로젝트에서 서버를 별도 모듈로 분리하는 완전 가이드

**참고**: libGDX 멀티플레이어 게임에서는 단일 프로젝트 내에 서버와 클라이언트를 별도 모듈로 두는 것이 일반적입니다. 이렇게 하면 코드 중복을 피하고 공통 로직을 공유할 수 있습니다.

---

## 🏗️ 1. 권장 아키텍처

### 1.1 멀티 모듈 구조 (Best Practice)

```
f1-racing-game/
├── core/                    # 공유 로직 (클라이언트 + 서버)
│   ├── src/main/java/
│   │   └── com/mygame/f1/
│   │       ├── shared/      # ✅ 공통 코드
│   │       ├── client/      # 🎮 클라이언트 전용
│   │       └── server/      # 🖥️ 서버 전용
│   └── build.gradle
│
├── lwjgl3/                  # 🎮 클라이언트 런처 (데스크톱)
│   ├── src/main/java/
│   │   └── com/mygame/f1/lwjgl3/
│   │       └── Lwjgl3Launcher.java
│   └── build.gradle
│
├── server/                  # 🖥️ 서버 런처 (새로 추가)
│   ├── src/main/java/
│   │   └── com/mygame/f1/server/
│   │       └── ServerLauncher.java
│   └── build.gradle
│
├── settings.gradle          # 모듈 포함 설정
└── build.gradle             # 루트 빌드 설정
```

### 1.2 왜 이 구조인가?

**장점**:
- ✅ **코드 공유**: 물리 로직, 게임 규칙을 양쪽에서 사용
- ✅ **동기화 용이**: 같은 코드베이스로 클라이언트/서버 동기화
- ✅ **유지보수**: 한 곳만 수정하면 양쪽 모두 적용
- ✅ **타입 안정성**: 패킷 클래스를 공유하여 직렬화 오류 방지

**단점**:
- ⚠️ 클라이언트 전용 코드(libGDX Graphics)가 서버에 포함됨
    - **해결**: 서버 JAR에서 제외 (Gradle 설정)

---

## 📋 2. 단계별 구현

### Step 1: settings.gradle 수정

```groovy
// settings.gradle
include 'core'
include 'lwjgl3'
include 'server'        // ✅ 새 모듈 추가
```

### Step 2: server/build.gradle 생성

```groovy
// server/build.gradle
plugins {
    id 'java'
    id 'application'
}

sourceCompatibility = 17
mainClassName = 'com.mygame.f1.server.ServerLauncher'

dependencies {
    // Core 모듈 의존 (공통 로직 포함)
    implementation project(':core')
    
    // KryoNet (네트워크 라이브러리)
    implementation "com.esotericsoftware:kryonet:2.24.0"
    
    // SLF4J (로깅)
    implementation "org.slf4j:slf4j-api:2.0.9"
    implementation "org.slf4j:slf4j-simple:2.0.9"
    
    // JUnit (테스트)
    testImplementation "org.junit.jupiter:junit-jupiter:5.10.0"
}

// 실행 가능한 JAR 생성
jar {
    manifest {
        attributes 'Main-Class': mainClassName
    }
    
    // 의존성 포함 (Fat JAR)
    from {
        configurations.runtimeClasspath.collect {
            it.isDirectory() ? it : zipTree(it)
        }
    }
    
    // 중복 파일 처리
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// 서버 실행 태스크
task runServer(type: JavaExec) {
    mainClass = mainClassName
    classpath = sourceSets.main.runtimeClasspath
    standardInput = System.in
    
    // JVM 옵션
    jvmArgs = [
        '-Xms512m',
        '-Xmx1024m',
        '-Djava.awt.headless=true'  // GUI 없이 실행
    ]
}
```

### Step 3: core/build.gradle 수정

```groovy
// core/build.gradle
project(":core") {
    apply plugin: "java-library"

    dependencies {
        // libGDX 코어 (클라이언트 + 서버 공통)
        api "com.badlogicgames.gdx:gdx:$gdxVersion"
        api "com.badlogicgames.gdx:gdx-box2d:$gdxVersion"
        
        // KryoNet (네트워크)
        api "com.esotericsoftware:kryonet:2.24.0"
        
        // SQLite (데이터베이스)
        api "org.xerial:sqlite-jdbc:3.45.0.0"
    }
}
```

### Step 4: 디렉토리 구조 재정리

```
core/src/main/java/com/mygame/f1/
├── shared/                          # ✅ 공통 코드 (클라이언트 + 서버)
│   ├── network/
│   │   ├── packets/                 # 네트워크 패킷
│   │   │   ├── PacketRegistry.java
│   │   │   ├── PlayerJoinPacket.java
│   │   │   ├── VehicleStatePacket.java
│   │   │   └── ...
│   │   └── NetworkConstants.java    # 포트, 틱레이트 등
│   │
│   ├── physics/                     # 물리 로직 (Box2D)
│   │   ├── VehiclePhysics.java      # 차량 물리 계산
│   │   ├── CollisionHandler.java
│   │   └── PhysicsConstants.java
│   │
│   ├── gameplay/                    # 게임 규칙
│   │   ├── TireType.java
│   │   ├── TireDegradation.java
│   │   ├── DamageCalculator.java
│   │   └── RaceRules.java
│   │
│   └── utils/
│       ├── TimeUtils.java
│       └── MathUtils.java
│
├── client/                          # 🎮 클라이언트 전용
│   ├── screens/
│   │   ├── GameScreen.java
│   │   ├── MainMenuScreen.java
│   │   └── ...
│   │
│   ├── ui/
│   │   └── hud/
│   │       ├── HUDManager.java
│   │       ├── Speedometer.java
│   │       └── ...
│   │
│   ├── network/
│   │   ├── GameClient.java          # 클라이언트 네트워크 로직
│   │   └── RemotePlayer.java        # 원격 플레이어 보간
│   │
│   └── Main.java                    # 클라이언트 메인
│
└── server/                          # 🖥️ 서버 전용
    ├── GameServer.java              # 메인 서버 클래스
    ├── ServerPlayer.java            # 서버의 플레이어 표현
    ├── RoomManager.java             # 방 관리
    └── AntiCheat.java               # 치트 검증
```

---

## 🖥️ 3. 서버 구현

### 3.1 ServerLauncher.java (진입점)

```java
package com.mygame.f1.server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

/**
 * 서버 진입점
 * - HeadlessApplication 사용 (그래픽 없음)
 * - 콘솔에서 실행 가능
 */
public class ServerLauncher {
    
    public static void main(String[] args) {
        System.out.println("=== F1 Racing Server Starting ===");
        
        // 서버 설정
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.renderInterval = 1/60f; // 60 TPS (Ticks Per Second)
        
        // HeadlessApplication 시작
        new HeadlessApplication(new ServerApplication(), config);
    }
}
```

### 3.2 ServerApplication.java

```java
package com.mygame.f1.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * 서버 애플리케이션
 * - 렌더링 없음 (HeadlessApplication)
 * - 게임 로직만 업데이트
 */
public class ServerApplication extends ApplicationAdapter {
    
    private GameServer gameServer;
    private long lastUpdateTime;
    
    @Override
    public void create() {
        System.out.println("Initializing server...");
        
        try {
            gameServer = new GameServer();
            gameServer.start();
            
            System.out.println("Server started successfully!");
            System.out.println("TCP Port: 54555");
            System.out.println("UDP Port: 54777");
            
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            Gdx.app.exit();
        }
        
        lastUpdateTime = TimeUtils.millis();
    }
    
    @Override
    public void render() {
        // 고정 타임스텝 업데이트 (60 TPS)
        long currentTime = TimeUtils.millis();
        float delta = (currentTime - lastUpdateTime) / 1000f;
        lastUpdateTime = currentTime;
        
        gameServer.update(delta);
    }
    
    @Override
    public void dispose() {
        System.out.println("Shutting down server...");
        gameServer.stop();
    }
}
```

### 3.3 GameServer.java (핵심 로직)

```java
package com.mygame.f1.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygame.f1.shared.network.NetworkConstants;
import com.mygame.f1.shared.network.packets.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 게임 서버
 * - KryoNet 서버 관리
 * - 플레이어 연결/해제 처리
 * - 게임 상태 업데이트 및 브로드캐스트
 */
public class GameServer {
    
    private Server server;
    private Map<Integer, ServerPlayer> players;
    private World physicsWorld;
    
    private static final float TIME_STEP = 1/60f;
    private float accumulator = 0f;
    
    public GameServer() {
        server = new Server(16384, 8192); // write/read buffer
        players = new ConcurrentHashMap<>();
        
        // Box2D 월드 (서버도 물리 시뮬레이션)
        physicsWorld = new World(new Vector2(0, 0), true);
        
        // 패킷 등록
        PacketRegistry.register(server.getKryo());
        
        // 리스너 등록
        server.addListener(new ServerListener());
    }
    
    /**
     * 서버 시작
     */
    public void start() throws IOException {
        server.bind(NetworkConstants.TCP_PORT, NetworkConstants.UDP_PORT);
        server.start();
    }
    
    /**
     * 매 프레임 업데이트 (60 TPS)
     */
    public void update(float delta) {
        accumulator += Math.min(delta, 0.25f);
        
        // 고정 타임스텝 물리 업데이트
        while (accumulator >= TIME_STEP) {
            updatePhysics(TIME_STEP);
            accumulator -= TIME_STEP;
        }
        
        // 상태 브로드캐스트 (20 Hz)
        if (System.currentTimeMillis() % 50 < 17) {
            broadcastGameState();
        }
    }
    
    /**
     * 물리 업데이트
     */
    private void updatePhysics(float delta) {
        // 모든 플레이어 차량 업데이트
        for (ServerPlayer player : players.values()) {
            player.update(delta);
        }
        
        // Box2D 스텝
        physicsWorld.step(TIME_STEP, 8, 3);
        
        // 충돌 검증, 위치 동기화 등
        validatePlayerStates();
    }
    
    /**
     * 게임 상태 브로드캐스트
     */
    private void broadcastGameState() {
        GameStatePacket packet = new GameStatePacket();
        packet.serverTimestamp = System.currentTimeMillis();
        packet.playerStates = new PlayerState[players.size()];
        
        int i = 0;
        for (ServerPlayer player : players.values()) {
            packet.playerStates[i++] = player.getState();
        }
        
        server.sendToAllUDP(packet);
    }
    
    /**
     * 플레이어 상태 검증 (치트 방지)
     */
    private void validatePlayerStates() {
        for (ServerPlayer player : players.values()) {
            // 속도 체크
            if (player.getSpeed() > NetworkConstants.MAX_SPEED * 1.1f) {
                System.out.println("Suspicious speed detected: " + player.getUsername());
                // 플레이어 킥 또는 경고
            }
            
            // 위치 체크 (트랙 경계 내)
            // ...
        }
    }
    
    public void stop() {
        server.stop();
        physicsWorld.dispose();
    }
    
    /**
     * 서버 이벤트 리스너
     */
    private class ServerListener extends Listener {
        
        @Override
        public void connected(Connection connection) {
            System.out.println("Client connected: " + connection.getID());
        }
        
        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof PlayerJoinPacket) {
                handlePlayerJoin(connection, (PlayerJoinPacket) object);
            }
            else if (object instanceof PlayerInputPacket) {
                handlePlayerInput(connection, (PlayerInputPacket) object);
            }
            else if (object instanceof PitStopPacket) {
                handlePitStop(connection, (PitStopPacket) object);
            }
        }
        
        @Override
        public void disconnected(Connection connection) {
            int playerId = connection.getID();
            ServerPlayer player = players.remove(playerId);
            
            if (player != null) {
                System.out.println("Player disconnected: " + player.getUsername());
                
                // 다른 클라이언트에게 알림
                PlayerLeftPacket packet = new PlayerLeftPacket();
                packet.playerId = playerId;
                server.sendToAllTCP(packet);
            }
        }
    }
    
    /**
     * 플레이어 참가 처리
     */
    private void handlePlayerJoin(Connection connection, PlayerJoinPacket packet) {
        int playerId = connection.getID();
        
        // 서버 플레이어 생성
        ServerPlayer player = new ServerPlayer(playerId, packet.username, physicsWorld);
        players.put(playerId, player);
        
        System.out.println("Player joined: " + packet.username + " (ID: " + playerId + ")");
        
        // 클라이언트에게 확인 전송
        PlayerJoinedPacket response = new PlayerJoinedPacket();
        response.assignedPlayerId = playerId;
        response.username = packet.username;
        response.vehicleId = packet.vehicleId;
        
        Vector2 spawnPos = player.getPosition();
        response.startX = spawnPos.x;
        response.startY = spawnPos.y;
        response.startRotation = player.getRotation();
        
        connection.sendTCP(response);
        
        // 다른 클라이언트들에게 알림
        server.sendToAllExceptTCP(playerId, response);
    }
    
    /**
     * 플레이어 입력 처리
     */
    private void handlePlayerInput(Connection connection, PlayerInputPacket packet) {
        ServerPlayer player = players.get(packet.playerId);
        
        if (player != null) {
            player.setInput(packet.acceleration, packet.steering, packet.braking);
        }
    }
    
    /**
     * 피트 스톱 처리
     */
    private void handlePitStop(Connection connection, PitStopPacket packet) {
        ServerPlayer player = players.get(packet.playerId);
        
        if (player != null) {
            player.performPitStop(packet.newTire, packet.result);
            
            // 다른 클라이언트들에게 브로드캐스트
            server.sendToAllExceptUDP(packet.playerId, packet);
        }
    }
}
```

### 3.4 ServerPlayer.java

```java
package com.mygame.f1.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygame.f1.shared.gameplay.TireType;
import com.mygame.f1.shared.network.packets.PlayerState;
import com.mygame.f1.shared.physics.VehiclePhysics;

/**
 * 서버의 플레이어 표현
 * - Box2D Body 관리
 * - 물리 시뮬레이션
 * - 상태 검증
 */
public class ServerPlayer {
    
    private int playerId;
    private String username;
    private Body body;
    
    // 게임 상태
    private TireType currentTire = TireType.MEDIUM;
    private float tireCondition = 1.0f;
    private float vehicleDurability = 1.0f;
    private int currentLap = 0;
    private float lapTime = 0f;
    
    // 입력
    private float accelerationInput = 0f;
    private float steeringInput = 0f;
    private boolean brakingInput = false;
    
    public ServerPlayer(int playerId, String username, World world) {
        this.playerId = playerId;
        this.username = username;
        
        // Box2D Body 생성
        createBody(world);
    }
    
    private void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getSpawnPosition());
        
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 1.0f);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 500f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.1f;
        
        body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    /**
     * 업데이트 (물리 적용)
     */
    public void update(float delta) {
        // 차량 물리 적용 (shared 패키지의 공통 로직 사용)
        VehiclePhysics.applyAcceleration(body, accelerationInput);
        VehiclePhysics.applySteering(body, steeringInput);
        VehiclePhysics.applyBraking(body, brakingInput);
        
        // 타이어 마모
        tireCondition -= delta * 0.01f; // 간단한 마모 모델
        tireCondition = Math.max(0, tireCondition);
        
        // 랩 타임 증가
        lapTime += delta;
    }
    
    /**
     * 입력 설정
     */
    public void setInput(float acceleration, float steering, boolean braking) {
        this.accelerationInput = acceleration;
        this.steeringInput = steering;
        this.brakingInput = braking;
    }
    
    /**
     * 피트 스톱 수행
     */
    public void performPitStop(TireType newTire, PitStopResult result) {
        this.currentTire = newTire;
        this.tireCondition = 1.0f;
        this.vehicleDurability = 1.0f;
    }
    
    /**
     * 현재 상태 반환 (브로드캐스트용)
     */
    public PlayerState getState() {
        PlayerState state = new PlayerState();
        state.playerId = playerId;
        
        Vector2 pos = body.getPosition();
        state.x = pos.x;
        state.y = pos.y;
        state.rotation = body.getAngle() * MathUtils.radiansToDegrees;
        
        Vector2 vel = body.getLinearVelocity();
        state.velocityX = vel.x;
        state.velocityY = vel.y;
        state.angularVelocity = body.getAngularVelocity();
        
        state.currentLap = currentLap;
        state.lapTime = lapTime;
        state.currentTire = currentTire;
        state.tireCondition = tireCondition;
        state.vehicleDurability = vehicleDurability;
        
        return state;
    }
    
    public Vector2 getPosition() {
        return body.getPosition();
    }
    
    public float getRotation() {
        return body.getAngle() * MathUtils.radiansToDegrees;
    }
    
    public float getSpeed() {
        return body.getLinearVelocity().len();
    }
    
    public String getUsername() {
        return username;
    }
    
    private Vector2 getSpawnPosition() {
        // 플레이어 수에 따라 스폰 위치 계산
        return new Vector2(10f + playerId * 2f, 10f);
    }
}
```

---

## 🎮 4. 클라이언트 수정

### 4.1 Main.java 수정

```java
// core/src/main/java/com/mygame/f1/client/Main.java
package com.mygame.f1.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

/**
 * 클라이언트 메인 (기존 코드)
 */
public class Main extends Game {
    public static AssetManager assetManager;
    
    @Override
    public void create() {
        assetManager = new AssetManager();
        // ... 기존 코드
        
        setScreen(new MainMenuScreen(this));
    }
}
```

### 4.2 GameClient.java

```java
// core/src/main/java/com/mygame/f1/client/network/GameClient.java
package com.mygame.f1.client.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygame.f1.shared.network.NetworkConstants;
import com.mygame.f1.shared.network.packets.*;

import java.io.IOException;

/**
 * 게임 클라이언트
 */
public class GameClient {
    
    private Client client;
    private int localPlayerId;
    
    public GameClient() {
        client = new Client(16384, 8192);
        PacketRegistry.register(client.getKryo());
        
        client.addListener(new ClientListener());
    }
    
    /**
     * 서버 연결
     */
    public void connect(String serverIP) throws IOException {
        client.connect(5000, serverIP, NetworkConstants.TCP_PORT, NetworkConstants.UDP_PORT);
        
        // 참가 요청
        PlayerJoinPacket joinPacket = new PlayerJoinPacket();
        joinPacket.username = "Player" + System.currentTimeMillis();
        joinPacket.vehicleId = 1;
        
        client.sendTCP(joinPacket);
    }
    
    /**
     * 입력 전송
     */
    public void sendInput(float acceleration, float steering, boolean braking) {
        PlayerInputPacket packet = new PlayerInputPacket();
        packet.playerId = localPlayerId;
        packet.timestamp = System.currentTimeMillis();
        packet.acceleration = acceleration;
        packet.steering = steering;
        packet.braking = braking;
        
        client.sendUDP(packet);
    }
    
    private class ClientListener extends Listener {
        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof PlayerJoinedPacket) {
                PlayerJoinedPacket packet = (PlayerJoinedPacket) object;
                if (packet.assignedPlayerId == client.getID()) {
                    localPlayerId = packet.assignedPlayerId;
                    System.out.println("Assigned player ID: " + localPlayerId);
                }
            }
            else if (object instanceof GameStatePacket) {
                // 게임 상태 업데이트
                handleGameState((GameStatePacket) object);
            }
        }
    }
    
    private void handleGameState(GameStatePacket packet) {
        // GameScreen에서 처리
    }
}
```

---

## 🚀 5. 실행 방법

### 5.1 서버 실행

```bash
# 개발 중: Gradle로 실행
./gradlew server:runServer

# 또는 IDE에서
# Run Configuration → Main class: com.mygame.f1.server.ServerLauncher

# 프로덕션: JAR 빌드 후 실행
./gradlew server:jar
java -jar server/build/libs/server.jar
```

### 5.2 클라이언트 실행

```bash
# 기존 방식과 동일
./gradlew lwjgl3:run
```

### 5.3 동시 실행 (로컬 테스트)

```bash
# 터미널 1: 서버
./gradlew server:runServer

# 터미널 2: 클라이언트 1
./gradlew lwjgl3:run

# 터미널 3: 클라이언트 2
./gradlew lwjgl3:run
```

---

## 📊 6. 완성도 체크리스트

### 프로젝트 구조
- [ ] `server/` 모듈 생성
- [ ] `settings.gradle`에 server 추가
- [ ] `server/build.gradle` 작성
- [ ] `core/src` 디렉토리 재구성 (shared/client/server)

### 서버 구현
- [ ] ServerLauncher.java (HeadlessApplication)
- [ ] GameServer.java (KryoNet)
- [ ] ServerPlayer.java (물리 시뮬레이션)
- [ ] 패킷 등록 (PacketRegistry)

### 클라이언트 수정
- [ ] GameClient.java (KryoNet)
- [ ] Main.java에서 서버 IP 입력 UI
- [ ] GameScreen에 GameClient 통합

### 테스트
- [ ] 서버 단독 실행
- [ ] 클라이언트 1개 연결
- [ ] 클라이언트 2개 동시 연결
- [ ] 실시간 위치 동기화 확인

---

**다음 단계**: ServerLauncher.java 작성부터 시작하세요! 🖥️
