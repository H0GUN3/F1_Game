# Changelog

이 프로젝트의 중요한 변경 사항을 버전과 함께 기록합니다. 형식은 Keep a Changelog 지침을 따릅니다.

## [Unreleased]

### Added
- Leaderboard screen with Top N lap times and menu entry
- TimeAttackRepository.getTopN(trackId, limit) API
- HUD Stage 연동: 속도(km/h), 랩 수, 현재/베스트 랩 타임 표시
- 피니시 라인 센서 및 방향 판정(상향 통과 시에만 랩 인정)
- 타일맵 비활성화 상태에서 정적 배경 기반 진행 모드
- 간이 체크포인트(화면 기준) 3개 및 순서형 통과 로직, HUD에 CP 진행 표시
- 타임어택 일시정지/재개 처리(일시정지 시간 제외)

### Changed
- 랩 중복 방지 쿨다운: 0.8초 → 10초로 상향
- 피니시 판정 기준을 '바라보는 방향' → '이동 속도의 상향 성분'으로 변경
- 고정 타임스텝: 입력/힘 적용을 스텝 이전에 계산하도록 순서 조정
- 조향(선회)량을 속도에 비례해 감쇠(고속일수록 작게)

### Fixed
- (없음)

### Removed
- (없음)
