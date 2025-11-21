# HANDOFF (세션 인수인계)

다음 세션에서 바로 작업을 재개할 수 있도록 현재 맥락과 다음 할 일을 간결하게 정리합니다.

## Context
- 진행 단계: Phase 1 (기반) — 타일맵 제외, 정적 배경 기준
- 주요 실행 명령:
## Progress (현재까지 진행)
- 타일맵 프리로드: 스플래시에서 `maps/new_map.tmx` 사전 로드 후 GameScreen 재사용(전환 지연 감소)
- 타일 이미지 경로 교정: `assets/maps/new_map.tmx`의 이미지 참조를 `last_track/...`로 통일
- 타일맵 활성화 + 월드 크기 적용: TMX 규격 기반으로 월드 경계벽 생성
- HUD 연동: 속도/랩/현재·베스트 랩 타임 및 체크포인트 진행 표시
- 피니시/랩 로직: 상향 이동 속도 성분 기준 판정, 중복 방지 쿨다운 10초, 일시정지 시간 보정
- 센서 충돌 예외: 센서(Fixture.isSensor) 접촉은 감속/회전 감쇠 제외
- 체크포인트: 화면 기반 3개 순서형 센서(맵 오브젝트 미사용 시 기본)
- 맵 오브젝트 파서(보류): Collision(사각/폴리곤)→벽, Objects `CAR_1/spawn`(스폰), `finish`(센서), `cp-*`(센서) 구현(현재 `USE_MAP_OBJECTS=false`)
- Leaderboard 1차: Top N 조회 API + 화면 + 메인 메뉴 진입점 추가
- 카메라: 회전 보간 복구(요청 반영), 디버그 렌더 기본 OFF(F1로 토글)

  - 개발 실행: `./gradlew lwjgl3:run` 또는 `scripts/run-game.ps1`
  - 빌드/검증: `./gradlew clean build` 또는 `scripts/verify-build.ps1`
- 디버그/조작: F1(디버그), ESC(일시정지), 방향키/WASD, Space(브레이크)

## Done (이번 세션)
- HUD 연동 및 표시: 속도/랩/현재·베스트 랩 타임
  - 업데이트/그리기/리사이즈/자원해제 연결 완료
- 피니시 라인 센서 생성 + 방향 판정(상향 통과만 인정)
- 랩 중복 방지 쿨다운 10초로 상향
- 타일맵 비활성화(정적 배경 진행)로 모드 고정
- 체크포인트 3개(순서형) 추가 및 HUD 진행 표시
- 고정 타임스텝 순서 조정(입력/힘 → 스텝)
- 피니시 판정: 이동 속도의 상향 성분 기준으로 변경
- 조향량 속도 의존 감쇠 도입
- 타임어택 일시정지/재개 처리(일시정지 시간 제외)

코드 참고 위치(파일:라인)
- HUD 업데이트: `core/src/main/java/com/mygame/f1/GameScreen.java:318`
- HUD 렌더: `core/src/main/java/com/mygame/f1/GameScreen.java:481`
- HUD 리사이즈/해제: `core/src/main/java/com/mygame/f1/GameScreen.java:493`, `505`
- 피니시 센서: `core/src/main/java/com/mygame/f1/GameScreen.java:191`, `200`, `214`
- 방향 판정 분기 사용: `core/src/main/java/com/mygame/f1/GameScreen.java:146`
- 쿨다운(10초): `core/src/main/java/com/mygame/f1/gameplay/race/TimeAttackManager.java:8`
- 타일맵 비활성: `core/src/main/java/com/mygame/f1/GameScreen.java:103`

## Next (3~5개)
1) 리더보드/UX (진행중)
   - TimeAttackRepository Top N API
   - LeaderboardScreen + 메인 메뉴 진입점
2) Settings 확장
   - 조향/카메라 보간/HUD/디버그/볼륨
3) LoadingScreen 도입
   - 전환 로딩 UX(진행률/취소)
4) 타일맵 렌더 최적화
   - Atlas 전환 또는 FBO 프리렌더

## Decisions (요약)
- 트랙 미도입 단계에서: 정적 배경 + 화면 기반 센서로 진행
- 피니시 판정: 상향(0,1) 방향 내적 0.25 이상일 때만 랩 인정
- 쿨다운: 10초로 상향하여 중복 카운트 예방 강화

향후: 타일맵 전환 시 센서/벽/체크포인트를 Tiled 오브젝트/Collision 레이어로 이관(별 브랜치)

## Risks / Blocks
- 트랙 지오메트리 부재로 코스 컷 방지 미흡 → 체크포인트로 1차 보완 예정
- 타일맵 전환 시 센서/벽 재이관 필요 → 경계를 모듈화하여 리팩터블하게 유지

## Validate
- Leaderboard: 기록 없을 때 “(no records)” 표기
- Leaderboard: 랩 기록 저장 후 화면에서 최신 Top N 반영
- 메뉴에서 Leaderboard 진입/ESC/Back 동작 확인
