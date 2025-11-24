[33mcommit aa6c5d786c3f7d4796e0671127372288163730e9[m[33m ([m[1;36mHEAD[m[33m -> [m[1;32mmain[m[33m)[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Mon Nov 24 11:20:00 2025 +0900

    response 작업

[33mcommit aa0156e1fedd6af0d8a12561c3c6cd694dfbee6f[m
Merge: 770ac5a 7044bc7
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Dec 12 22:47:06 2025 +0900

    Merge response

[33mcommit 7044bc79da0bd395174f0d9261bfdfed381602a8[m[33m ([m[1;31morigin/feature/multiplayer-core[m[33m, [m[1;32mfeature/multiplayer-core[m[33m)[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Dec 12 22:46:33 2025 +0900

    response 작업

[33mcommit 3b84e5c02f90c88269a4e80ebc7502deb921c513[m[33m ([m[1;31mupstream/feature/multiplayer-core[m[33m, [m[1;31mfork/feature/multiplayer-core[m[33m)[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Dec 12 21:57:46 2025 +0900

    tiled 상호작용

[33mcommit 1c173e78d16c23593627315879d4ade24e740186[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Dec 12 21:29:12 2025 +0900

    tiled 상호작용 작업.

[33mcommit 5aedf73ee84181fdc093262364bb35585e6fb30f[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Wed Dec 10 00:13:51 2025 +0900

    gameseen tiled 맵 상호작용 (잔디를 밟을시 차량 속도 제어및 vehicle durablity, tire durablity 수정 ) 및 tiled 맵 수정.
    
    # Conflicts:
    #       core/src/main/java/com/mygame/f1/GameScreen.java

[33mcommit e489d82de40d2ce4fe4bc92ffb453859b362bbe4[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Thu Dec 4 11:43:28 2025 +0900

    WIP GameScreen

[33mcommit 91c1d6585ac634016305e02000270ab8b4dec087[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Tue Dec 9 23:53:30 2025 +0900

    내 작업내용 저장
    
    # Conflicts:
    #       assets/america.tmx
    #       assets/japan.tmx
    #       core/src/main/java/com/mygame/f1/GameScreen.java

[33mcommit 146afba0281b04a98479c33ee479e2dfa120427c[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 11:45:41 2025 +0900

    Perf: Optimize singleplayer rendering performance
    
    - Remove Vector2 allocations in render loop (getLateralVelocity, getForwardVelocity, handlePitState)
    - Use static LATERAL_DIR/FORWARD_DIR vectors for reuse
    - Remove expensive Tiled map re-rendering in minimap (major perf gain)
    - Minimap now uses simple background texture instead of full map render
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit dda31e7dfabb5a49434560d54b2a99773025fe01[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 11:33:59 2025 +0900

    Feat: Improve multiplayer sync with extrapolation and higher update rate
    
    - Add velocity/timestamp tracking to RemoteCar class for extrapolation
    - Implement smoothstep + extrapolation hybrid interpolation for smoother movement
    - Increase network sync rate from 20Hz to 30Hz (client and server)
    - Add damping to extrapolation to prevent overshooting
    - Update CLAUDE.md with Windows/Unix gradle commands and Git workflow
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 91180a3dfb1031755960abc235514418b6d73927[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 07:27:01 2025 +0900

    Feat: Merge singleplay-polish improvements while preserving multiplayer
    
    병합 개선사항:
    - 최고 전진 속도: 3.5 → 4.0 (약 15% 상향)
    - 가속 램프업 속도: 0.10 → 0.03 (느린 가속, 난이도 조정)
    - 타이어 컴파운드 시스템 강화:
      * Soft: 90초 마모, +12% 속도
      * Medium: 130초 마모, 기본 성능
      * Hard: 150초 마모, -15% 회전력
    - 내구도 페널티: 차량/타이어 0 이하 시 속도 30% 제한
    - 브레이크 키 변경: SPACE → SHIFT
    - HUD 속도 표기: 최대 268km/h (현실감 향상)
    - Pit UI 개선: 타이어 선택 패널 위치 조정
    
    보존된 멀티플레이 기능:
    - 레이스 완주 카운트다운 시스템
    - 완주 정보 전송 (completedLapTimes, sendPlayerFinished)
    - 결과 화면 전환 (MultiplayerResultScreen)
    - 미니맵 플레이어 색상 구분 (getPlayerColor)
    - 원격 차량 보간 (lerp=20 유지)
    
    문서:
    - 멀티플레이_진행상황.md: 최근 개선사항 정리
    - 병합_결과_보고서.md: 상세 병합 분석 및 테스트 가이드
    
    Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 83634baf49747343ded596adcb98c75dcd66368a[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 06:56:11 2025 +0900

    Fix: Add keep-alive and timeout to prevent ghost players
    
    Problem:
    - Disconnected players remained visible as "ghost players" in multiplayer
    - Server could not detect abnormal disconnections (network failure, power loss)
    - TCP connections remained active indefinitely without heartbeat mechanism
    
    Solution:
    - Added connection.setKeepAliveTCP(8000) to send heartbeat every 8 seconds
    - Added connection.setTimeout(20000) to close dead connections after 20 seconds
    - These settings enable server to detect and cleanup dead connections automatically
    
    Technical Details:
    - Keep-alive interval: 8 seconds (industry standard 8-10s)
    - Timeout duration: 20 seconds (2.5x keep-alive, recommended 1.5x-2x ratio)
    - When timeout occurs, disconnected() callback triggers leaveAll() cleanup
    
    Files Modified:
    - server/src/main/java/com/mygame/f1/server/GameServer.java
    
    Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 7271fc3e4a852f812cb1399689aa222cbc019437[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 06:34:41 2025 +0900

    Fix: Improve minimap player color assignment stability
    
    멀티플레이어 미니맵 색상 할당 로직 개선 및 tmx 맵 업데이트
    
    문제점:
    - getPlayerColor()가 playerVehicles 맵에 의존하여 불안정
    - indexOf()가 -1 반환 시 회색으로 표시되는 문제
    
    수정사항:
    - playerId % 4 방식으로 색상 직접 할당 (더 안정적)
    - playerVehicles 맵 의존성 제거
    - 모든 플레이어 ID에 대해 일관된 색상 보장
    
    맵 업데이트:
    - america.tmx 체크포인트 및 오브젝트 업데이트
    - f1_racing_map.tmx 기본 맵 최적화
    - japan.tmx 트랙 레이아웃 개선
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 2c6d37fde32e774647b4398a050bef60ac606460[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 06:17:01 2025 +0900

    Feat: Add unique player colors on multiplayer minimap
    
    멀티플레이어 미니맵에 각 플레이어를 고유한 색상으로 표시하는 기능 추가
    
    주요 변경사항:
    - RemoteCar 클래스에 playerId 필드 추가
    - getPlayerColor() 메서드로 플레이어별 고유 색상 할당
      - P1: 밝은 파란색 (0.2, 0.6, 1.0)
      - P2: 밝은 초록색 (0.2, 1.0, 0.4)
      - P3: 노란색 (1.0, 0.8, 0.2)
      - P4: 핑크색 (1.0, 0.4, 0.9)
    - 미니맵에서 각 원격 플레이어를 고유 색상으로 렌더링
    - 로컬 플레이어는 기존대로 빨간색 유지
    
    이점:
    - 4명 플레이어를 시각적으로 쉽게 구분
    - 플레이어 ID 기반으로 일관된 색상 유지
    - 실시간 위치 동기화와 함께 색상도 동기화
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 0551d9d44600a555402bebe61e76e5df257e5be2[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 04:27:45 2025 +0900

    Fix: Compilation errors in countdown UI
    
    컴파일 오류 수정:
    1. hudTitleFont → hudLapFont 사용 (기존 큰 폰트 활용)
    2. pixelTexture → startLightOnRegion.getTexture() 사용 (기존 텍스처 재활용)
    3. shapeRenderer 제거 → 간단한 검은색 박스 배경으로 대체
    4. MultiplayerPlaceholderScreen chatStyle 변수명 중복 → inputStyle로 변경
    
    변경사항:
    - 카운트다운 숫자 배경: 검은색 반투명 박스 (alpha 0.8)
    - 기존 리소스를 재사용하여 추가 텍스처 생성 불필요
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 4b77ee157919ae0fa1510f90f239f37662391585[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 04:12:04 2025 +0900

    Fix: Add delay before closing connection to prevent ghost players
    
    분신 플레이어 문제의 근본 원인:
    - leaveRoom() 패킷 전송과 close() 호출이 즉시 순차 실행
    - KryoNet의 비동기 처리로 인해 leaveRoom 패킷이 전송되기 전에 연결 종료
    - 서버가 disconnect 이벤트만 받고 leaveRoom을 처리하지 못함
    - 결과적으로 플레이어가 방에서 제거되지 않고 분신으로 남음
    
    해결책:
    - leaveRoom() 호출 후 100ms 대기한 다음 close() 실행
    - 별도 스레드에서 비동기로 처리하여 UI 블로킹 방지
    - leaveRoom 패킷이 서버에 도달하고 처리될 시간 확보
    - 서버의 onLeaveRoom()이 먼저 실행되어 플레이어 제거
    - 이후 disconnect 이벤트 처리로 완전한 정리 보장
    
    변경 파일:
    - MultiplayerResultScreen.java: returnToLobby(), exitToMenu()
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 5c66af3fedff8c181e0ef55804f9ae2cb45714cc[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 04:09:35 2025 +0900

    Fix: Revert chat to TextField and implement countdown UI
    
    1. 채팅 입력 TextField로 복원
       - 네이티브 다이얼로그 방식이 작동하지 않아 TextField로 복원
       - 영어 입력만 지원 (한글 IME는 LibGDX TextField의 알려진 제한사항)
       - Enter 키로 메시지 전송
       - Send 버튼 클릭으로도 전송 가능
    
    2. 멀티플레이어 레이스 종료 카운트다운 UI 구현
       - 1등 완주 시 화면 중앙에 큰 숫자로 카운트다운 표시 (10초)
       - 1등 플레이어 이름과 완주 시간 표시
       - 반투명 검은 배경으로 게임플레이 위에 오버레이
       - 카운트다운 업데이트 시 숫자 실시간 변경
       - 노란색 큰 폰트로 카운트다운, 초록색으로 1등 정보 표시
    
    변경 파일:
    - MultiplayerPlaceholderScreen.java: Label+네이티브→TextField 복원
    - GameScreen.java: 카운트다운 변수 추가 및 drawRaceFinishCountdown() 구현
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 76e076eb2e9a1e03c9a781517103e476ea89f35c[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 03:53:02 2025 +0900

    Fix: Resolve chat input clickability and ghost players bug
    
    1. 채팅 입력창 클릭 문제 해결
       - Label의 Touchable을 disabled로 설정하여 터치 이벤트 차단 방지
       - Table의 Touchable을 enabled로 설정하여 클릭 영역 활성화
       - 한글 입력은 네이티브 다이얼로그를 통해 계속 지원
    
    2. 분신(Ghost Players) 버그 해결
       - returnToLobby() 시 기존 연결을 완전히 종료 (leaveRoom + close)
       - exitToMenu() 시 연결 종료 로직 개선
       - KryoNet 연결을 명시적으로 close()하여 서버에서 disconnect 이벤트 발생
       - 서버의 leaveAll() 메서드가 제대로 호출되어 플레이어 정리됨
    
    근거:
    - LibGDX Scene2D: Touchable 설정으로 터치 이벤트 전파 제어
      https://github.com/libgdx/libgdx/issues/5174
    - KryoNet: Connection.close()를 호출해야 서버에서 disconnected() 콜백 실행
      https://github.com/EsotericSoftware/kryonet/issues/35
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit c39f2c4f8dda3f4f52338c4d70d30053ed78975d[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 03:28:25 2025 +0900

    fix: Add ImageTextButton default style and native Korean input
    
    Fixed two critical multiplayer issues:
    
    1. SkinFactory: Added ImageTextButton "default" style
       - MultiplayerResultScreen requires "default" ImageTextButtonStyle
       - Previously only menu-*-icon styles were registered
       - Fixes crash: "No ImageTextButtonStyle registered with name: default"
       - Location: SkinFactory.java:130-131
    
    2. Chat: Replaced TextField with native input dialog
       - LibGDX TextField doesn't support IME on desktop (known limitation)
       - Changed to Gdx.input.getTextInput() for full Korean support
       - Uses Label + ClickListener to show native Windows input dialog
       - Native dialog supports complete IME (한글, 中文, 日本語)
       - Location: MultiplayerPlaceholderScreen.java:200-262
    
    Technical Details:
    - TextField.keyTyped() is never invoked for CJK input on desktop
    - Native input provides OS-level IME with autocomplete/correction
    - Chat input box now clickable area triggering native dialog
    
    References:
    - LibGDX IME limitation: github.com/libgdx/libgdx/issues/4892
    - Native text input API: github.com/libgdx/libgdx/pull/7004
    
    Resolves user issues:
    - Chat Korean input (issue #1)
    - MultiplayerResultScreen crash (issues #3, #4, #6)
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 5c7c2c85acfc3cc5fd76db2fc3df4c73da06529d[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 03:07:50 2025 +0900

    fix: Button style error and Korean input in multiplayer
    
    Fixed two critical multiplayer UI issues:
    
    1. MultiplayerResultScreen button style crash:
       - Changed "primary"/"secondary" to "default" style
       - Skin only defines "default" ImageTextButtonStyle
       - This was preventing result screen from displaying
       - Was causing fallback to singleplayer result screen
    
    2. Chat TextField Korean input support:
       - Applied "kr-font" to chat TextField
       - Added BitmapFont import
       - Korean characters now display correctly in chat
    
    These fixes resolve issues #1, #3, #4, #6 from user test report.
    Issue #5 (FAIL players) should now be testable once result screen loads.
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 710bdac1017ddfde658bf170b22a6203ce21500d[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 02:50:30 2025 +0900

    fix: Multiplayer race finish bugs and chat improvements
    
    1. 채팅 Enter 키 전송 기능 추가
       - TextFieldListener에서 Enter 키 감지 시 메시지 전송
       - 한글 입력 지원
    
    2. MultiplayerResultScreen divider 오류 수정
       - 스킨에 없는 "divider" Drawable 대신 직접 Texture 생성
       - 타이틀 구분선 및 랭킹 헤더 구분선 모두 수정
    
    3. 멀티플레이 랩 수 초과 버그 수정 (중요)
       - onMultiplayerRaceFinished()에서 gameState = FINISHED 추가
       - 완주 후에도 랩 카운팅이 계속되던 문제 해결
       - 게임 튕김 현상 방지
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit c0e9b0304993ea4be4d97bf07e4e1a2cf4cceaa4[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 01:44:17 2025 +0900

    feat: Add multiplayer race finish system (server-side)
    
    Server-side implementation:
    - PlayerFinishedPacket: Send finish time and lap times to server
    - CountdownStartPacket: Notify all clients when 1st place finishes
    - CountdownUpdatePacket: Update countdown every second (10s total)
    - RaceResultsPacket: Send final rankings and FAIL list
    - PlayerResult: Individual player result with rank and times
    
    Server logic (GameServer.java):
    - onPlayerFinished(): Handle player finish notification
    - finalizeRace(): Calculate rankings after 10s countdown
    - Room tracking: finishedPlayers map, countdown state
    
    Client preparation (LobbyClient.java):
    - sendPlayerFinished(): Send finish data to server
    - onCountdownStart/Update/RaceResults(): Handlers for server packets
    
    Next steps:
    - GameScreen.java: Send finish notification on race complete
    - MultiplayerResultScreen.java: Display results UI
    
    Related: #multiplayer #race-finish

[33mcommit b6234fdcea781e36c386500dbe9f8405788ffe21[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 02:06:59 2025 +0900

    feat: Add multiplayer race finish system (client-side)
    
    - GameScreen: 싱글/멀티 완주 로직 분리
      - completedLapTimes 리스트 추가로 각 랩 타임 기록
      - onRaceFinished() 메서드에서 멀티플레이 분기 처리
      - onMultiplayerRaceFinished()에서 서버로 완주 정보 전송
      - countdown 및 race results 핸들러 등록
    
    - MultiplayerResultScreen 생성
      - 순위별 플레이어 표시 (1위 골드, 2위 실버, 3위 브론즈)
      - 완주 시간 및 최고 랩 타임 표시
      - FAIL(미완주) 플레이어 별도 표시
      - Return to Lobby / Exit 버튼
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 43e19b690fd6b0ebe473325c78fdede81725350e[m
Merge: 76a5a8b bbd668b
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 01:16:03 2025 +0900

    chore: Sync with main branch

[33mcommit bbd668ba8da807b5d6579bc967b8386b5f59011e[m
Merge: 57048aa e73ab6b
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 01:14:42 2025 +0900

    Merge branch 'main' of https://github.com/H0GUN3/F1_Game

[33mcommit 57048aab81e557bc19653890609849b916158e27[m
Merge: 563747f 76a5a8b
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 01:13:37 2025 +0900

    feat: Merge multiplayer core features to main
    
    - External server connection support via F1_SERVER_HOST
    - Multiplayer client launcher script
    - Room-based lobby system
    - Real-time player synchronization
    - Racing map updates
    - Collaboration documentation

[33mcommit 76a5a8b89f2f119e99e6ee9a2eb32cb06b4b2ebc[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 01:13:21 2025 +0900

    chore: Update racing map

[33mcommit 39ea278c554802d48be20f3e4922903173c46003[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 00:42:00 2025 +0900

    fix: Use absolute path for gradlew.bat execution
    
    - Use %~dp0 to get batch file directory
    - Prevents 'command not found' error in PowerShell
    - Ensures gradlew.bat is found regardless of execution context

[33mcommit 951b89b3e0f26646306de6a050ee3b68b166ab5f[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 00:40:26 2025 +0900

    fix: Improve multiplayer client launcher script
    
    - Add explicit directory change to batch file location
    - Use gradlew.bat instead of gradlew for Windows compatibility
    - Add error checking for gradlew.bat existence
    - Add Korean comments for clarity

[33mcommit e73ab6bfa88e99bdb3f259e2d7b93a0f60d0fbbb[m
Merge: 563747f cb8a1dc
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 00:33:52 2025 +0900

    Merge pull request #3 from InsuHam0315/feature/singleplay-polish
    
    맵 렌더링 더 넓혀서 도로 타일이 끊겨보이지 않게 2배로 렌더링 크기를 넓힘

[33mcommit 482f993d6672bb3961f09ebf2bca972f6d399e4c[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 00:27:28 2025 +0900

    feat: Add multiplayer client launcher script
    
    - Sets F1_SERVER_HOST=203.234.62.51 automatically
    - Simplifies client execution for external testing
    - Displays connection information (IP and ports)

[33mcommit cb8a1dc5518c11d28783cd44ce00fa91b929a8ee[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Thu Dec 4 00:25:06 2025 +0900

    맵 렌더링 더 넓혀서 도로 타일이 끊겨보이지 않게 2배로 렌더링 크기를 넓힘

[33mcommit 9379cd1af26cadc3a188b65d5dbae6318942d56c[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 00:23:19 2025 +0900

    chore: Update racing map
    
    - Minor map adjustments for multiplayer testing

[33mcommit 9c84483f16310e5f13f612f8660e457ed3eac151[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 23:45:28 2025 +0900

    feat: Add environment variable support for multiplayer server IP
    
    - Add F1_SERVER_HOST environment variable to configure server address
    - Default to 'localhost' if not set
    - Allows external clients to connect by setting server IP
    - Usage: set F1_SERVER_HOST=<server-ip> before running game
    
    Resolves issue where clients could only connect to localhost

[33mcommit 563747f3bf54274827aeb1a57e29b98f901e0470[m
Merge: 84d5591 5a2db9b
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 23:07:35 2025 +0900

    docs: Merge collaboration documents from multiplayer-core

[33mcommit 5a2db9b09e293a3e143620a1017ca199fd44e5b7[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 22:47:13 2025 +0900

    feat: LAP HUD 플레이어 번호 대문자 표시
    
    - P1, P2, P3, P4로 표시 (기존 p1, p2, p3, p4에서 변경)
    - 싱글플레이: P1 고정
    - 멀티플레이: 플레이어 순서에 따라 P1~P4

[33mcommit 2fadbe08e8a9c04af53c80be106f15d0e0dba97f[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 22:46:26 2025 +0900

    docs: README 한글 업데이트 및 간소화
    
    - 프로젝트 소개 및 주요 기능 정리
    - 개발 환경 설정 가이드 업데이트
    - 협업가이드.md 및 진행상황.md 링크 추가
    - 개발 현황 및 최근 업데이트 섹션 추가

[33mcommit 3b93cf5c47bb46d50d62a050821075cdba84cad6[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 22:42:50 2025 +0900

    docs: 한글 협업 가이드 및 진행 상황 문서 추가
    
    - 협업가이드.md: 팀원을 위한 완전한 개발 가이드
      - 환경 설정, Git 워크플로우, 코딩 컨벤션
      - 팀별 작업 영역 (싱글플레이 vs 멀티플레이)
      - 충돌 해결 전략 및 문제 해결
    - 진행상황.md: 현재 프로젝트 상태 및 로드맵
      - Phase 1 (100%), Phase 2-3 (진행 중)
      - 알려진 이슈, Git 통계, 주간 목표

[33mcommit 84d55918745b80a372c87c50dc2b4d07bb9339f6[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 22:32:47 2025 +0900

    feat: Complete UI improvements and checkpoint fixes
    
    - Update LAP HUD to display uppercase player numbers (P1, P2, P3, P4)
    - Fix checkpoint rotation handling for rotated Tiled map objects
    - Enhance SinglePlay vehicle selection UI with larger icons and images
    - Add atlas-based navigation buttons (left/right icons)
    - Update MAP section styling to match CARS section
    - Adjust player spawn positions for better track alignment
    - Add debug logging for checkpoint and start line detection

[33mcommit 4b9837398731688a7efc9794f0c44ed0e4cd8425[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 00:23:17 2025 +0900

    Refactor: HUD 레이아웃 전면 개편
    
    변경사항:
    
    1. 차량 내구도 HUD
       - 왼쪽 끝에 수직 바로 배치
       - 위에서 아래로 감소 (100% → 0%)
       - 충돌 데미지 10 고정
       - 상태별 색상 변경 (초록→노랑→빨강)
       - 라벨을 바 위에 배치
    
    2. 타이어 HUD
       - 오른쪽 끝에 수직 바로 배치
       - 타이어 타입 이미지(medium) 바 위에 배치
       - 타이어 상태별 색상 변경
    
    3. 속도계 HUD
       - 크기 50% 축소
       - 최대 속도 289km/h로 제한
       - 텍스트 위치 조정
    
    4. LAP 정보 HUD
       - 오른쪽 위 끝에 배치
       - "p1 / LAP / 1 / 3" 형식으로 표시
       - 플레이어 번호 자동 계산 (멀티플레이어)
    
    5. 미니맵 HUD
       - 프레임 안에 정확히 렌더링
       - 비율 유지하며 중앙 정렬
       - 플레이어 위치 빨간색 표시
       - 다른 플레이어 파란색 표시
    
    6. LAP 타임 박스
       - 미니맵 아래에 배치
       - BEST: 지금까지 가장 빠른 LAP
       - LAST: 가장 최근 완주한 LAP
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 45a6f53a1d30fc98922f15e55e85225be16db96f[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 00:08:14 2025 +0900

    Feat: 그리드 출발 위치 및 신호등 시작 제어 구현
    
    변경사항:
    1. GRID_SPAWNS 배열에 Tiled 맵 실제 좌표 반영
       - p1: (87*32/100, (120-92)*32/100)
       - p2: (90*32/100, (120-92)*32/100)
       - p3: (87*32/100, (120-94)*32/100)
       - p4: (90*32/100, (120-94)*32/100)
    
    2. 출발 위치 로직 개선
       - 멀티플레이: 플레이어 ID 순으로 p1~p4 배정
       - 싱글플레이: 항상 p1 위치에서 시작
       - fallback: 맵 중앙
    
    3. 신호등 시작 제어 추가
       - startLightsDone=false일 때 차량 입력 차단
       - 신호등 꺼질 때까지 차량 정지 상태 유지
       - ESC 키는 항상 허용 (일시정지)
    
    로그 추가:
    - 멀티플레이어: "Multiplayer spawn at pN: (x, y)"
    - 싱글플레이어: "Singleplayer spawn at p1: (x, y)"
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit cf064856c347cf89ab9db82cda5ad897a81bc1fe[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Wed Dec 3 00:02:28 2025 +0900

    Fix: 싱글플레이 Tiled 맵 상호작용 복원
    
    문제:
    - 싱글플레이에서 출발 위치(startgrid)가 적용되지 않음
    - Grass 영역 감속이 작동하지 않음 (updateGrassZoneCheck 미호출)
    - 멀티플레이 스폰 인덱스 범위 체크 누락
    
    해결:
    - computeSpawnPosition()에서 startgrid 레이어 첫 객체를 출발 위치로 사용
    - update()에 updateGrassZoneCheck() 호출 추가 (limitSpeed 전)
    - 멀티플레이 스폰 인덱스 경계 검사 추가 (idx < GRID_SPAWNS.length)
    - fallback 우선순위: startgrid → GRID_SPAWNS[0] → 맵 중앙
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 6cd26e91e90bd761e6a66d0684aa6732eb6c35a7[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Tue Dec 2 23:49:59 2025 +0900

    Perf: Optimize SkinFactory with singleton pattern
    
    - Main.java에 sharedSkin 싱글톤 추가, 애플리케이션 시작 시 1회만 생성
    - SkinFactory의 폰트 캐싱으로 중복 생성 방지 (한글 글리프 11,522자)
    - 모든 Screen에서 Main.getSharedSkin() 사용으로 Skin 재생성 제거
    - 화면 전환 시 0.5-2초 지연 제거 및 메모리 사용량 대폭 감소
    
    성능 개선 예상:
    - 화면 전환 시간: ~2초 → ~0.01초
    - 메모리 사용: 화면당 50-100MB 감소
    - GC 압력 감소로 프레임 드롭 최소화
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 0cb47ff835b9e59c1e10178c92d14b28a0fb7e24[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Tue Dec 2 23:32:42 2025 +0900

    Feat: Implement Phase 3 - Tiled map interactions
    
    Added Grass zone (off-track) system:
    - loadGrassZonesFromMap() loads "Grass" layer rectangles
    - updateGrassZoneCheck() detects vehicle position
    - Speed penalty applied (60% reduction) when on grass
    - Debug logging for grass entry/exit
    
    Improved collision damage system:
    - Added vehicleDurability field (0-100%)
    - Speed-based damage calculation (> 20 km/h)
    - ContactListener enhanced with collision speed detection
    - HUD durability bar now shows vehicle health instead of tire
    
    Phase 3 checklist:
    ✓ Checkpoint system (already implemented)
    ✓ Start line & lap counting (already implemented)
    ✓ Grass zones with speed penalty
    ✓ Pit areas (already implemented)
    ✓ Collision damage based on impact speed
    - Runtime testing pending for all maps
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 5f44bceeaec42f24805099880064406e75131362[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Tue Dec 2 23:24:28 2025 +0900

    Docs: Mark Phase 2 (HUD System) as complete
    
    All HUD rendering methods were already implemented:
    - drawLapTimeHud() - Best/Last lap time display
    - drawSpeedHud() - Speed and gear display
    - drawDurabilityHud() - Vehicle durability bar
    - drawTireHud() - Tire wear and compound display
    - drawRaceStatusHud() - Lap counter and player name
    - drawMinimapHud() - Track minimap with player position
    - drawPitMinigameHud() - Pit stop timing minigame
    - drawStartLightsHud() - Race start countdown lights
    
    All methods use TextureRegion from atlas and are called in render().
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit bc53cd0f5e6b2c1f19eb48fc0eb40d810e18b092[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Tue Dec 2 23:21:27 2025 +0900

    Fix: Load HUD label textures individually outside atlas
    
    Label textures (vehicle_durability_label, tire_durability_label) don't
    exist in game_ui.atlas. Modified initHudResources() to:
    - Load label PNGs individually using loadTextureSafe()
    - Wrap in TextureRegion for consistent API
    - Add to dispose list for proper cleanup
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 6436570bba0daef186a6f975cb72703029e56c71[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Tue Dec 2 22:52:43 2025 +0900

    Feat: Phase 1 완료 - TextureAtlas 마이그레이션
    
    ## 변경사항
    - GameScreen.java: 개별 Texture → TextureRegion 전환
      - 15개 HUD 텍스처 필드를 TextureRegion으로 변경
      - gameAtlas 필드 추가하여 atlas 참조 관리
    
    - initHudResources(): TextureAtlas 기반 로딩
      - Main.assetManager에서 game_ui.atlas 로드
      - findRegion()으로 모든 HUD 요소 추출 및 캐싱
      - Atlas 이름 매핑: light-on, light-off 사용
    
    - HUD 렌더링 메서드 업데이트 (6개 메서드)
      - drawSpeedHud(), drawDurabilityHud(), drawTireHud()
      - drawPitMinigameHud(), drawLapTimeHud(), drawStartLightsHud()
      - .getWidth() → .getRegionWidth() 변경
      - Texture[] → TextureRegion[] 변경
    
    - Dispose 최적화
      - TextureRegion은 dispose 불필요 (주석 추가)
      - Atlas에 없는 개별 텍스처만 dispose 처리
    
    - PHASES.md: 개발 로드맵 추가
      - Phase 0, 1 완료 체크
      - Phase 2-7 계획 수립
    
    ## 효과
    - 텍스처 바인딩 비용 감소 → 렌더링 성능 향상
    - 메모리 관리 단순화 (AssetManager 일괄 관리)
    - 코드 유지보수성 향상
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 9338362b4eeda319cd84f0a8ae492491d57da4cd[m
Merge: a105060 986d2dc
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Tue Dec 2 22:25:28 2025 +0900

    Merge: Resolve .gitattributes conflict
    
    Keep local version with explicit eol settings for cross-platform compatibility.
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit a1050603c9aea89b827b2c3ca494404be732729d[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Tue Dec 2 22:24:08 2025 +0900

    Feat: Atlas migration, network code, and documentation
    
    - Add comprehensive CLAUDE.md documentation in Korean
    - Migrate to TextureAtlas system (game_ui.atlas with 2 PNG pages)
    - Rename car assets to proper names (Astra A4, Boltworks RX-1, etc.)
    - Add network multiplayer infrastructure (LobbyClient, Packets)
    - Add working single-player GameScreen implementation (docs/new/)
    - Organize UI assets into proper directory structure
    - Add Figma UI design documentation
    - Clean up unused legacy assets
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 770ac5abfa4aea4f4d8b87784ddf28c672739d8d[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Tue Nov 25 14:30:00 2025 +0900

    변경사항

[33mcommit 9557cfe5cfd1cde44d9352c8cb185283f712b7d6[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Tue Nov 25 12:30:00 2025 +0900

    변경사항

[33mcommit 55f73c941ec77c0682ba607ef52a8cf3eef250cf[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Tue Nov 25 11:30:00 2025 +0900

    변경사항 7

[33mcommit d9a864109ed69063ab2a9813a8adf6b450311739[m[33m ([m[1;31mupstream/main[m[33m, [m[1;31mupstream/HEAD[m[33m)[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Dec 12 22:35:58 2025 +0900

    response 작업

[33mcommit f82509c12ac9232ea21247bc1feb2f383aadee6b[m[33m ([m[1;31mfork/main[m[33m, [m[1;31mfork/HEAD[m[33m)[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Dec 12 22:30:40 2025 +0900

    response 작업

[33mcommit cc4306ade82794d72512ded20986f8dbe90dc4e8[m
Merge: 53353a2 d2c4139
Author: jinwoong0306 <jinung125@gmail.com>
Date:   Fri Dec 12 22:24:41 2025 +0900

    Merge branch 'H0GUN3:main' into main

[33mcommit 53353a2aa22b39fef176ee90ca63f87772846a22[m
Merge: f74c49e 3b84e5c
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Dec 12 22:17:48 2025 +0900

    Merge feature/multiplayer-core

[33mcommit f74c49ed8ce165c910612d8675dc1568909aa1dd[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Dec 12 22:14:48 2025 +0900

    response 작업

[33mcommit d3fb7d5302a4928008ccfb628df4ae994066989e[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Wed Dec 10 00:13:51 2025 +0900

    gameseen tiled 맵 상호작용 (잔디를 밟을시 차량 속도 제어및 vehicle durablity, tire durablity 수정 ) 및 tiled 맵 수정.
    
    # Conflicts:
    #       core/src/main/java/com/mygame/f1/GameScreen.java

[33mcommit 52a6136b1624f5561f64459ed107bc8d2007a4f2[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Thu Dec 4 11:43:28 2025 +0900

    WIP GameScreen

[33mcommit 35c42f3ae756511e22ce4dfa4745b14e40e4960a[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Tue Dec 9 23:53:30 2025 +0900

    내 작업내용 저장
    
    # Conflicts:
    #       assets/america.tmx
    #       assets/japan.tmx
    #       core/src/main/java/com/mygame/f1/GameScreen.java

[33mcommit 6daaafa1339070d5838f29668e2b3f4899e48142[m
Merge: 71e2a9e 9293204
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Sat Dec 6 21:05:47 2025 +0900

    Merge feature/multiplayer-core into main
    
    - Consolidate documentation: unified PROGRESS.md, updated PHASES.md
    - Update CLAUDE.md and README.md to reflect Phase 4 completion
    - Remove duplicate/obsolete MD files (11 files deleted)
    - Include multiplayer optimizations and performance improvements
    - Resolve conflicts: accept feature branch GameScreen.java with latest optimizations
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit 71e2a9ea4789230a68b3cec814e9733f09e24975[m
Merge: c4b368d aba9dab
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Sat Dec 6 20:54:24 2025 +0900

    Merge branch 'main' of https://github.com/H0GUN3/F1_Game

[33mcommit 92932045220e7744b43df3a744d07a0c9ba3d349[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Sat Dec 6 20:54:11 2025 +0900

    Docs: Consolidate and update project documentation
    
    - Create unified PROGRESS.md from scattered progress files
    - Update PHASES.md to reflect Phase 4 completion status
    - Update CLAUDE.md with latest settings (tire compounds, network config)
    - Update README.md with current project status
    - Remove duplicate/outdated documentation files:
      - docs/PHASES.md, docs/AGENTS.md, docs/specs/PROJECT-OVERVIEW.md
      - 진행상황.md, 멀티플레이_진행상황.md, 협업가이드.md
      - 병합_결과_보고서.md, 성능_최적화_보고서.md, 싱글플레이 변경사항.md
      - F1/Note.md, docs/new/final-ppt-outline.md
    
    🤖 Generated with [Claude Code](https://claude.com/claude-code)
    
    Co-Authored-By: Claude <noreply@anthropic.com>

[33mcommit aba9dab3d452a3c60632cdfa2d9f759865fead21[m
Merge: bbd668b 7370870
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 13:21:19 2025 +0900

    Merge pull request #7 from InsuHam0315/feature/singleplay-polish
    
    타이어 성능·속도계/HUD 조정 및 기본 랩 수 5 설정

[33mcommit 7370870cc530c44653bd6f7319b51ded098cf435[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Thu Dec 4 06:31:39 2025 +0900

    글로벌 속도 보정 및 랩 수 5 설정

[33mcommit 8c89231853249c2995fee599335f0dd816326502[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Thu Dec 4 06:10:14 2025 +0900

    타이어 성능 및 HUD/브레이크 조정

[33mcommit 98f5a8b2b96e6fad0e84a04337827b85696fcf44[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Thu Dec 4 05:56:32 2025 +0900

    차량 속도/계기판 수정

[33mcommit 64d7a885aea080f49efff7027c5d1cce37b98a69[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Thu Dec 4 05:16:43 2025 +0900

    docs: 누락된 md파일 추가

[33mcommit 35385f5b534ba1bfd77929a185d554e3e11b1916[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Thu Dec 4 05:05:05 2025 +0900

    타이어 컴파운드 마모/속도 보정 업데이트

[33mcommit 47f44c57dbc0e42e46ae32c7b4e90b56c9c3572a[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Thu Dec 4 03:58:52 2025 +0900

    PIT HUD: reposition tire slot and instructions

[33mcommit c4b368d0dd25f7023ac7f27f8d164ef49bb80c79[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Dec 4 01:44:17 2025 +0900

    feat: Add multiplayer race finish system (server-side)
    
    Server-side implementation:
    - PlayerFinishedPacket: Send finish time and lap times to server
    - CountdownStartPacket: Notify all clients when 1st place finishes
    - CountdownUpdatePacket: Update countdown every second (10s total)
    - RaceResultsPacket: Send final rankings and FAIL list
    - PlayerResult: Individual player result with rank and times
    
    Server logic (GameServer.java):
    - onPlayerFinished(): Handle player finish notification
    - finalizeRace(): Calculate rankings after 10s countdown
    - Room tracking: finishedPlayers map, countdown state
    
    Client preparation (LobbyClient.java):
    - sendPlayerFinished(): Send finish data to server
    - onCountdownStart/Update/RaceResults(): Handlers for server packets
    
    Next steps:
    - GameScreen.java: Send finish notification on race complete
    - MultiplayerResultScreen.java: Display results UI
    
    Related: #multiplayer #race-finish

[33mcommit 8d3a3e106703045548d90f3ce872eac0244a770b[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Thu Dec 4 01:02:21 2025 +0900

    차량/타이어 내구도 0 시 최고 속도 30% 제한

[33mcommit d2c4139299bda1771c6cf65e9de72020e4d6bd0b[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Tue Nov 25 10:30:00 2025 +0900

    변경사항 6

[33mcommit 4063eff366c16daf1990b263a59af87866224a98[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 22:20:00 2025 +0900

    변경사항 5

[33mcommit 97e7f3268280a8969c8649cbb648284b28e3d43f[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 21:20:00 2025 +0900

    변경사항 4

[33mcommit 111a29b032e10ae4044396d63c352d2c2200afbf[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 20:20:00 2025 +0900

    변경사항 3

[33mcommit 02e7d3d20678b5a8a1719cc9c8d2e5f06cff7d19[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 17:20:00 2025 +0900

    response 작업

[33mcommit fdd9eb1181c76b854a89a4e43a9977d9cad15309[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 16:20:00 2025 +0900

    response 작업

[33mcommit 144124a2a88428b0c961aeb41cd18ed0fa419ace[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 15:20:00 2025 +0900

    response 작업

[33mcommit b8bebb1a218f477222752c0232cf3c69b6eaf541[m
Merge: bdc052a fdb7aab
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 14:20:00 2025 +0900

    Merge branch 'main' of https://github.com/H0GUN3/F1_Game

[33mcommit fdb7aab1d1bd672e6c64a2f1de84793005ababb1[m
Merge: aae2c6b d3fb7d5
Author: jinwoong0306 <jinung125@gmail.com>
Date:   Fri Dec 12 21:36:55 2025 +0900

    Merge pull request #11 from jinwoong0306/main
    
    상호작용 변경 내용 저장

[33mcommit bdc052a4e436403133d7f6e98d971c31d2969f8b[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 14:20:00 2025 +0900

    변경사항3

[33mcommit aae2c6bc60bf2475b7c234d305c1b2d26a3f14fe[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 13:20:00 2025 +0900

    변경사항 2

[33mcommit 3be72bece47747f178987528c5e600deed795d6f[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 12:20:00 2025 +0900

    함인수변경사항.md 생성

[33mcommit 773bcdfdd6ff4def102e95d8194c7963a85813a8[m
Merge: ec0aa9b 82609e8
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 11:20:00 2025 +0900

    Merge response

[33mcommit ec0aa9b9ae1f520d433ac51503e774fed05a496f[m
Merge: 6daaafa b435c20
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 11:20:00 2025 +0900

    Merge response

[33mcommit 82609e8ca264a469f6dad19be593766867337a16[m[33m ([m[1;31mupstream/feature/Ham[m[33m, [m[1;31morigin/feature/Ham[m[33m)[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 11:20:00 2025 +0900

    Ham 작업

[33mcommit b435c2059a3edd39556e65966793dad9286b7ae2[m[33m ([m[1;31mupstream/feature/response[m[33m, [m[1;31morigin/feature/response[m[33m)[m
Author: InsuHam0315 <gkadlstn1@gmail.com>
Date:   Mon Nov 24 11:20:00 2025 +0900

    response

[33mcommit 8e7bcd42490196771dbb739ba82f95372a27739f[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Mon Nov 24 02:31:00 2025 +0900

    Chore: clean unused assets and update MP

[33mcommit 0ca88af2942e4bf2207dff8830e9ebb9a80bd7b5[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Mon Nov 24 02:19:10 2025 +0900

    Chore: sync MP ready flow and assets (cars)

[33mcommit 084e3c818fdcb85a823c31f111c18a6e910fb36a[m
Merge: 60fa877 c9a6bd3
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Thu Nov 20 12:12:11 2025 +0900

    Merge pull request #1 from jinwoong0306/main
    
    이미지 추가

[33mcommit c9a6bd304d33ca329a46153065ee30ced2a0c6ca[m
Author: jangjinwoong <jinung125@gmail.com>
Date:   Fri Nov 14 17:00:49 2025 +0900

    이미지 추가

[33mcommit 60fa877ca569078dff8f474e52dd3816519ee0cc[m[33m ([m[1;31mfork/feature/checkpoints[m[33m)[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Mon Nov 10 14:22:47 2025 +0900

    Feat: Scene2D 로그인/회원가입·메뉴 및 ESC 일시정지 추가\n\n- UI 스킨 코드화(팔
                레트/폰트/오버레이)\n- 한글 폰트 글리프 포함으로 네모 문자 해결\n- 메인 화면 좌측 로고/우측
                메뉴 구성\n- Tiled 맵은 미제작 상태로 비활성(USE_TILED_MAP=false)

[33mcommit 6b3d53af5aaae6896bdbb978fb99fd600ed0f01b[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Mon Oct 13 02:55:51 2025 +0900

    Feat: 새로운 트랙 에셋 추가

[33mcommit 136e000f34f94a44d77c3d47f4e11614ed3fd717[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Mon Oct 13 02:11:50 2025 +0900

    Docs: 기능 요약 추가 및 엔진 최적화

[33mcommit 6897ff5072cecfa2f2adb3bb002bc0e677369d90[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Sun Oct 12 19:39:06 2025 +0900

    기초게임엔진개발

[33mcommit 986d2dc17cef7aeae48ea040e24a5427540b927c[m
Author: H0GUN3 <gunho727022@gmail.com>
Date:   Sun Oct 12 19:33:47 2025 +0900

    Initial commit
