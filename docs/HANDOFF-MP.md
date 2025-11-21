# Multiplayer Handoff (Server Progress)

## Summary
- Added `server/` module with KryoNet server entry: `server/src/main/java/com/mygame/f1/server/ServerLauncher.java:1`
- Added `shared/` module with lobby/basic sync packets: `shared/src/main/java/com/mygame/f1/shared/Packets.java:1`
- Packet registry for Kryo: `shared/src/main/java/com/mygame/f1/shared/PacketRegistry.java:1`
- Implemented minimal lobby: create/join/leave room, max 4 players, broadcast `RoomStatePacket`: `server/src/main/java/com/mygame/f1/server/GameServer.java:1`
- Script: `scripts/run-server.ps1`

## How to Run
- Build: `./gradlew clean build`
- Run server: `./gradlew :server:run` or `./scripts/run-server.ps1`
- (Optional) Ports: `./scripts/run-server.ps1 -Tcp 54555 -Udp 54777`

## Validation
- Run `./gradlew :server:run` (default ports TCP 54555 / UDP 54777)
- In a separate terminal, execute `./gradlew :core:lobbyRoundtrip` to spin up host/joiner clients
- Confirm console logs show `Room created`, `Joiner entered`, and both clients receiving `RaceStart` countdown packets
- (Optional) Manual UI test: `MultiplayerPlaceholderScreen` QuickHost/QuickJoin using the same ports and two game instances

## Next
1. 서버가 전체 방 목록/참가자·준비 상태를 주기적으로 브로드캐스트하도록 `RoomListPacket`과 Ready 토글 API 추가
2. 모든 플레이어가 준비 완료되면 자동으로 COUNTDOWN→`RaceStartPacket` 흐름을 시작하고, 누군가 Ready 해제 시 COUNTDOWN을 취소하는 상태 기계 구현
3. 클라이언트 UI를 “방 생성” vs “방 목록” 2단 구성으로 개편하여 Room ID 수동 입력 없이 방을 생성/선택/입장할 수 있게 하고, 각 플레이어의 Ready 상태와 카운트다운을 화면에 표시
4. 위 기능을 자동으로 검증할 수 있는 새로운 roundtrip 테스트(`host Ready -> join Ready -> 자동 카운트다운`)를 추가하고 Validation 섹션에 절차 업데이트

## Decisions
- Keep backend isolated in `server/` subproject to avoid main-class conflicts with `lwjgl3`
- Cap room size to 4 (Phase target)

## Follow-ups
- Guard client temporary socket connect in `Main` behind env/prop (`CLIENT_NET_ENABLED`, `-Dclient.net=true`)
- Extend protocol per `docs/specs/network/MULTIPLAYER-SYNC.md` (race phases, inputs, state tick)
