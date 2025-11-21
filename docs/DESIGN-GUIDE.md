# DESIGN.md - F1 2D 레이싱 게임 UI 디자인 가이드

## 목차
1. [디자인 철학](#디자인-철학)
2. [컬러 팔레트](#컬러-팔레트)
3. [타이포그래피](#타이포그래피)
4. [UI 컴포넌트 시스템](#ui-컴포넌트-시스템)
5. [화면별 레이아웃](#화면별-레이아웃)
6. [HUD 시스템 (인게임)](#hud-시스템-인게임)
7. [애니메이션 가이드](#애니메이션-가이드)
8. [반응형 디자인](#반응형-디자인)

---

## 디자인 철학

### 핵심 원칙

**1. F1의 프리미엄 감성**
- 현대적이고 세련된 디자인
- 하이테크 느낌의 기하학적 UI
- 레이싱의 속도감과 역동성 표현

**2. 가독성 최우선**
- 고속 주행 중에도 즉시 파악 가능한 정보
- 명확한 계층 구조와 대비
- 충분한 여백과 구분선

**3. 몰입감 강화**
- 최소한의 UI로 시야 확보
- 모서리 배치로 중앙 시야 방해 최소화
- 반투명 배경으로 게임 화면 가시성 유지

**4. 레퍼런스 기반**
- 제공된 이미지의 레이아웃 구조 반영
- 실제 F1 텔레메트리 스타일
- 직관적이고 프로페셔널한 느낌

---

## 컬러 팔레트

### 주요 색상 (Primary Colors)

#### Racing Colors

```
Pure Black (배경)
HEX: #000000
RGB: 0, 0, 0
용도: 상하단 레터박스, 깊은 배경

Dark Gray (UI 배경)
HEX: #1A1A1A
RGB: 26, 26, 26
Alpha: 0.85 (반투명)
용도: UI 패널 배경

Steel Gray (테두리/구분선)
HEX: #404040
RGB: 64, 64, 64
용도: 테두리, 구분선

Pure White (텍스트/아이콘)
HEX: #FFFFFF
RGB: 255, 255, 255
용도: 주요 텍스트, 숫자, 아이콘
```

#### Performance Indicators (상태 표시)

```
Perfect Green (최적)
HEX: #00FF00
RGB: 0, 255, 0
용도: 높은 내구도 (70-100%)

Lime Green (양호)
HEX: #7FFF00
RGB: 127, 255, 0
용도: 중상 내구도 (50-69%)

Warning Yellow (주의)
HEX: #FFFF00
RGB: 255, 255, 0
용도: 중간 내구도 (30-49%)

Orange Alert (경고)
HEX: #FF8800
RGB: 255, 136, 0
용도: 낮은 내구도 (20-29%)

Danger Red (위험)
HEX: #FF0000
RGB: 255, 0, 0
용도: 매우 낮은 내구도 (0-19%)
```

#### Tire Compound Colors

```
Soft Red (소프트)
HEX: #FF1744
RGB: 255, 23, 68
표시: 빨간 원 아이콘

Medium Yellow (미디움)
HEX: #FFEA00
RGB: 255, 234, 0
표시: 노란 원 아이콘

Hard White (하드)
HEX: #FFFFFF
RGB: 255, 255, 255
표시: 흰 원 아이콘
```

#### Position Colors (순위)

```
Position 1-3 (상위권)
HEX: #FFD700
RGB: 255, 215, 0
Gold - 강조 표시

Position 4+ (일반)
HEX: #FFFFFF
RGB: 255, 255, 255
White - 기본 표시
```

#### Accent Colors

```
Electric Blue (강조)
HEX: #00B8FF
RGB: 0, 184, 255
용도: 선택/호버 상태

Neon Cyan (활성)
HEX: #00FFFF
RGB: 0, 255, 255
용도: 활성화된 버튼
```

---

## 타이포그래피

### 폰트 시스템

#### 주 폰트

**Roboto Condensed Bold** - 주요 정보 표시
```
용도: 순위(P2), LAP 카운터, 타이머
굵기: Bold (700)
특징: 압축형으로 공간 효율적, 가독성 우수
대안: DIN Condensed, Eurostile
```

**Roboto Mono Bold** - 숫자/데이터 표시
```
용도: 속도(285 km/h), 타이머, 순위 숫자
굵기: Bold (700)
특징: 등폭 폰트로 숫자 정렬 일관성
대안: JetBrains Mono, Source Code Pro
```

**Roboto Bold** - 라벨/설명
```
용도: BEST, LAST, VEHICLE DURABILITY 등
굵기: Bold (700)
특징: 명확한 라벨 표시
```

### 텍스트 크기 스케일

```
Hero Number (속도계)
크기: 96px
폰트: Roboto Mono Bold
용도: 속도 숫자 (285)
색상: #FFFFFF
효과: 드롭 섀도우

Large Position (순위)
크기: 72px
폰트: Roboto Condensed Bold
용도: P2, P1 등 순위 표시
색상: #FFFFFF
배경: 사각형 컨테이너

Medium Data (타이머)
크기: 32-40px
폰트: Roboto Mono Bold
용도: 01:15.321 (랩 타임)
색상: #FFFFFF

Small Label (라벨)
크기: 18-24px
폰트: Roboto Bold
용도: LAP 5/10, BEST, LAST
색상: #FFFFFF
효과: 대문자(uppercase)

Tiny Text (보조 정보)
크기: 14-16px
폰트: Roboto Regular
용도: km/h, th (순위 접미사)
색상: #CCCCCC
```

### 텍스트 스타일 가이드

#### 텍스트 효과

**드롭 섀도우 (가독성 강화)**
```css
text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
```
용도: 속도, 순위 등 주요 숫자

**아웃라인 (대비 강화)**
```css
text-stroke: 2px #000000;
```
용도: 배경이 밝은 경우

**대문자 + 자간**
```css
text-transform: uppercase;
letter-spacing: 1px;
```
용도: 모든 라벨 텍스트

---

## UI 컴포넌트 시스템

### 버튼 (Buttons)

#### Primary Button (주요 버튼)
```
크기: 200x60px
배경: #E10600 (F1 Red)
텍스트: #FFFFFF, Roboto Bold 18px
테두리: 없음
모서리: 4px border-radius
효과:
  - Hover: 배경 #FF1744, scale(1.05)
  - Active: 배경 #C10500, scale(0.98)
  - 빛남: box-shadow 0 0 20px rgba(225, 6, 0, 0.6)
```

#### Secondary Button (보조 버튼)
```
크기: 200x60px
배경: #2D2D2D
테두리: 2px solid #FFFFFF
텍스트: #FFFFFF, Roboto Bold 18px
모서리: 4px border-radius
효과:
  - Hover: 배경 #3D3D3D, 테두리 #00B8FF
  - Active: 배경 #1D1D1D
```

#### Icon Button (아이콘 버튼)
```
크기: 48x48px
배경: rgba(26, 26, 26, 0.8)
아이콘: 24x24px, #FFFFFF
모서리: 4px
효과:
  - Hover: 배경 #2D2D2D, 아이콘 #00B8FF
```

### 프로그레스 바 (Progress Bars)

#### Horizontal Bar (수평 바)
```
높이: 8px
배경: #2D2D2D
채움: 그라디언트 (상태별)
  - 70-100%: #00FF00 → #7FFF00
  - 40-69%: #FFFF00
  - 20-39%: #FF8800
  - 0-19%: #FF0000
모서리: 2px border-radius
테두리: 1px solid #404040
```

#### Vertical Bar (수직 바 - 게이지)
```
너비: 32px
높이: 가변 (100-200px)
배경: #2D2D2D
채움: 하단에서 상단으로
  - 세그먼트 방식 (칸으로 구분)
  - 칸 간격: 2px
모서리: 4px border-radius
테두리: 2px solid #404040
```

### 카드 (Cards)

#### Standard Card
```
배경: rgba(26, 26, 26, 0.9)
테두리: 2px solid #404040
패딩: 20px
모서리: 8px border-radius
그림자: 0 4px 16px rgba(0, 0, 0, 0.7)

효과:
  - Hover: 테두리 #FFFFFF, 살짝 위로 이동
```

### 입력 필드 (Input Fields)

```
크기: 300x50px
배경: #1A1A1A
테두리: 2px solid #404040
텍스트: #FFFFFF, Roboto Regular 16px
플레이스홀더: #808080
패딩: 12px
모서리: 4px border-radius

상태:
  - Focus: 테두리 #00B8FF, 빛남 효과
  - Error: 테두리 #FF0000
```

---

## 화면별 레이아웃

### 1. 메인 메뉴 (Main Menu)

```
┌────────────────────────────────────────┐
│                                        │
│         F1 RACING 2D                   │ ← Hero Title
│         [로고/이미지]                    │   72px Bold
│                                        │
│                                        │
│     ┌──────────────┐                   │
│     │ SINGLE PLAY  │ ← Primary Btn    │
│     └──────────────┘   200x60px        │
│                                        │
│     ┌──────────────┐                   │
│     │ MULTIPLAYER  │ ← Primary Btn    │
│     └──────────────┘                   │
│                                        │
│     ┌──────────────┐                   │
│     │   SETTINGS   │ ← Secondary Btn  │
│     └──────────────┘                   │
│                                        │
│     ┌──────────────┐                   │
│     │     EXIT     │ ← Secondary Btn  │
│     └──────────────┘                   │
│                                        │
│            v1.0.0 ← Caption            │
└────────────────────────────────────────┘

레이아웃:
- 중앙 정렬
- 버튼 간격: 20px
- 배경: 어두운 트랙 배경 + vignette
```

### 2. 차량 선택 화면

```
┌────────────────────────────────────────┐
│  [←]  SELECT YOUR CAR                  │
├────────────────────────────────────────┤
│                                        │
│  ┌──────┐  ┌──────┐  ┌──────┐         │
│  │ CAR1 │  │ CAR2 │  │ CAR3 │         │
│  │[IMG] │  │[IMG] │  │[IMG] │         │
│  │      │  │      │  │      │         │
│  │Name  │  │Name  │  │ Lock │         │
│  └──────┘  └──────┘  └──────┘         │
│     ↑                                  │
│  Selected                              │
│                                        │
│  ┌─────────────────────────────────┐   │
│  │ CAR STATS                        │  │
│  │                                  │  │
│  │ Speed:     ████████░░ 80%       │  │
│  │ Accel:     ██████░░░░ 60%       │  │
│  │ Handling:  ███████░░░ 70%       │  │
│  │ Durability:█████████░ 90%       │  │
│  └─────────────────────────────────┘   │
│                                        │
│     [BACK]              [START RACE]   │
└────────────────────────────────────────┘
```

### 3. 멀티플레이 로비

```
┌────────────────────────────────────────┐
│  MULTIPLAYER LOBBY            [🔄]     │
├────────────────────────────────────────┤
│  [CREATE ROOM]  [FILTER: All ▼]        │
├────────────────────────────────────────┤
│                                        │
│  ┌────────────────────────────────┐    │
│  │ #1234  Monaco    2/4  [JOIN]   │    │
│  └────────────────────────────────┘    │
│                                        │
│  ┌────────────────────────────────┐    │
│  │ #5678  Monza     3/4  [JOIN]   │    │
│  └────────────────────────────────┘    │
│                                        │
│  ┌────────────────────────────────┐    │
│  │ #9012  Silverstone 4/4  FULL   │    │
│  └────────────────────────────────┘    │
│                                        │
│  [< 1 2 3 >]                           │
│                                        │
│  [BACK]                                │
└────────────────────────────────────────┘
```

---

## HUD 시스템 (인게임)

### 전체 레이아웃 (레퍼런스 이미지 기반)

```
┌────────────────────────────────────────────────┐
│ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ │ ← 상단 레터박스
│                                                │   (100px 높이)
│ ┌────────┐                    ┌─────────────┐ │
│ │  P2    │                    │  MINIMAP    │ │ ← 좌상단: 순위
│ │LAP 5/10│                    │  [트랙맵]    │ │   우상단: 미니맵
│ └────────┘                    │             │ │
│                                │  BEST:      │ │
│                                │  01:15.321  │ │
│                                │  LAST:      │ │
│   [게임 플레이 영역]              │  01:16.890  │ │
│                                └─────────────┘ │
│                                                │
│ ┌──────────┐                  ┌──────────┐   │
│ │ VEHICLE  │                  │   TIRE   │   │ ← 좌하단: 차량
│ │DURABILITY│                  │DURABILITY│   │   우하단: 타이어
│ │          │                  │          │   │
│ │ ████████ │                  │ ████████ │   │
│ │          │                  │          │   │
│ │ [차아이콘]│                  │ [타이어]  │   │
│ └──────────┘                  │  MEDIUM  │   │
│                                └──────────┘   │
│ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ │ ← 하단 레터박스
│         ┌─────────────────────┐              │   (100px 높이)
│         │  [변속LED] 285 7th  │              │   중앙: 속도계
│         └─────────────────────┘              │
└────────────────────────────────────────────────┘

레터박스:
- 상단/하단: 100px 높이의 검은색 바
- 용도: 시네마틱 느낌 + UI 배치 공간
```

### 1. 순위 & 랩 카운터 (좌상단)

```
┌────────────────────┐
│  P2  │  LAP 5/10   │  ← 크기: 200x80px
└────────────────────┘

위치: 좌상단 (20px, 120px)
배경: rgba(26, 26, 26, 0.85)
테두리: 2px solid #FFFFFF (좌측, 상단만)
패딩: 12px

구성 요소:
- P2: Roboto Condensed Bold 72px
  - 색상: #FFFFFF (또는 순위별 색상)
  - 위치: 좌측 정렬
  
- 구분선: 2px solid #FFFFFF (세로)

- LAP 5/10: Roboto Bold 24px
  - 색상: #FFFFFF
  - 위치: 우측 정렬
  - "LAP"은 작게 (18px), 숫자는 크게 (24px)
```

### 2. 미니맵 & 타이머 (우상단)

```
┌─────────────────────────┐
│    ┌──────────────┐     │  크기: 200x280px
│    │   MINIMAP    │     │
│    │  [트랙 맵]    │     │  미니맵: 180x180px
│    │              │     │
│    └──────────────┘     │
│                         │
│  BEST:  01:15.321       │  타이머: 각 40px 높이
│  LAST:  01:16.890       │
└─────────────────────────┘

위치: 우상단 (화면 우측에서 20px, 120px)
배경: rgba(26, 26, 26, 0.85)
테두리: 2px solid #FFFFFF (우측, 상단만)
패딩: 12px

미니맵 구성:
- 크기: 180x180px
- 배경: #0A0A0A
- 테두리: 1px solid #FFFFFF
- 트랙 라인: #404040 (회색)
- 플레이어 (본인): 빨간 점 + 방향 화살표
- 다른 플레이어: 주황/흰색 점
- 체크포인트: 노란 점선

타이머:
- BEST: Roboto Mono Bold 32px, #FFD700 (골드)
- LAST: Roboto Mono Bold 32px, #FFFFFF
- 라벨: Roboto Bold 18px, #CCCCCC
```

### 3. 속도계 & 변속 LED (하단 중앙)

```
┌──────────────────────────────────┐
│  [●●●●●●●]  285  7th            │  크기: 400x120px
│              km/h                │
└──────────────────────────────────┘

위치: 하단 중앙 (레터박스 내부)
배경: 
  - 육각형 또는 사다리꼴 형태
  - rgba(26, 26, 26, 0.9)
  - 테두리: 2px solid #FFFFFF

변속 LED (좌측):
- LED 개수: 7-10개
- LED 크기: 12x12px (사각형)
- LED 간격: 4px
- 색상:
  - 비활성: #2D2D2D
  - 활성 (0-60%): #00FF00 (초록)
  - 활성 (61-80%): #FFFF00 (노랑)
  - 활성 (81-95%): #FF8800 (주황)
  - 활성 (96-100%): #FF0000 (빨강 + 점멸)

속도 숫자 (중앙):
- 크기: 96px
- 폰트: Roboto Mono Bold
- 색상: #FFFFFF
- 효과: 드롭 섀도우

단위 (km/h):
- 크기: 24px
- 폰트: Roboto Regular
- 색상: #CCCCCC
- 위치: 숫자 하단 우측

기어 (7th):
- 크기: 48px
- 폰트: Roboto Bold
- 색상: #FFFFFF
- 위치: 숫자 우측
```

### 4. 차량 내구도 게이지 (좌하단)

```
┌─────────────────┐
│ VEHICLE         │  크기: 140x200px
│ DURABILITY      │
│                 │
│  ████████████   │  ← 세로 바
│  ████████████   │
│  ████████████   │
│  ░░░░░░░░░░░░   │
│  ░░░░░░░░░░░░   │
│                 │
│   [차 아이콘]    │  ← 간단한 차 실루엣
└─────────────────┘

위치: 좌하단 (20px, 하단에서 120px)
배경: rgba(26, 26, 26, 0.85)
테두리: 2px solid #FFFFFF (좌측, 하단만)
패딩: 12px

라벨:
- "VEHICLE DURABILITY"
- Roboto Bold 14px
- 색상: #FFFFFF
- 대문자, 중앙 정렬

게이지:
- 타입: 세로 바 (수직)
- 크기: 32px x 140px
- 배경: #2D2D2D
- 테두리: 2px solid #404040
- 방향: 하단에서 상단으로 채움
- 세그먼트: 10칸으로 구분
- 칸 간격: 2px

색상 (하단에서 상단):
- 70-100%: #00FF00 (초록)
- 40-69%: #FFFF00 (노랑)
- 20-39%: #FF8800 (주황)
- 0-19%: #FF0000 (빨강)

차량 아이콘:
- 크기: 48x24px
- 색상: #FFFFFF
- 위치: 게이지 하단
- 스타일: 단순한 실루엣
```

### 5. 타이어 내구도 게이지 (우하단)

```
┌─────────────────┐
│  TIRE           │  크기: 140x200px
│  DURABILITY     │
│                 │
│  ████████████   │  ← 세로 바
│  ████████████   │
│  ████████████   │
│  ████████████   │
│  ░░░░░░░░░░░░   │
│                 │
│  [타이어 아이콘] │  ← 타이어 + 색상 점
│    MEDIUM       │  ← 타이어 종류
└─────────────────┘

위치: 우하단 (화면 우측에서 20px, 하단에서 120px)
배경: rgba(26, 26, 26, 0.85)
테두리: 2px solid #FFFFFF (우측, 하단만)
패딩: 12px

라벨:
- "TIRE DURABILITY"
- Roboto Bold 14px
- 색상: #FFFFFF

게이지:
- 타입: 세로 바
- 크기: 32px x 140px
- 세그먼트: 10칸
- 색상: 차량 내구도와 동일

타이어 아이콘:
- 크기: 48x48px
- 배경: 검정 원
- 테두리: 2px solid #FFFFFF
- 중앙: 작은 색상 점
  - SOFT: 빨간 점
  - MEDIUM: 노란 점
  - HARD: 흰 점

타이어 종류 라벨:
- "MEDIUM" 등
- Roboto Bold 16px
- 색상: 타이어별 색상
- 위치: 아이콘 하단
```

### 6. 레이스 시작 신호등 (중앙 상단)

```
┌───────────────────────────┐
│  ●  ●  ●  ●  ●            │  크기: 300x100px
│                           │
│      READY                │
└───────────────────────────┘

위치: 상단 중앙 (레터박스 바로 아래)
배경: rgba(0, 0, 0, 0.95)
테두리: 4px solid #FFFFFF
모서리: 8px border-radius

LED:
- 개수: 5개
- 크기: 40x40px (원형)
- 간격: 16px
- 배경 (꺼짐): #2D2D2D
- 활성 (켜짐): #FF0000 (빨강)
- 효과: 켜질 때 빛남

시퀀스:
1초마다 하나씩 점등 → 모두 켜진 후 1-2초 대기 → 동시 소등

상태 텍스트:
- "READY" → "GO!"
- Roboto Bold 36px
- 색상: #FFFFFF
```

### 7. 피트인 알림 (옵션)

```
┌─────────────────────────────┐
│  ⚠ PIT LANE AHEAD           │
│  Press [SPACE] to Enter     │
└─────────────────────────────┘

위치: 상단 중앙 (신호등 위치)
크기: 400x60px
배경: rgba(255, 215, 0, 0.95)
텍스트: #000000, Roboto Bold 18px
테두리: 3px solid #000000
점멸: 1초 간격
```

---

## 피트인 미니게임 UI

### 미니게임 화면

```
┌────────────────────────────────────────┐
│                                        │
│         PIT STOP CHALLENGE             │
│                                        │
│  ┌──────────────────────────────────┐  │
│  │                                  │  │
│  │    ┌──┐                          │  │
│  │    │██│→                         │  │
│  │    └──┘                          │  │
│  │  │  │  │PERFECT│  │  │          │  │
│  │  └──┴──┴───────┴──┴──┘          │  │
│  │  BAD GOOD  │   GOOD BAD          │  │
│  │                                  │  │
│  └──────────────────────────────────┘  │
│                                        │
│    Press [SPACE] at Perfect Zone!      │
│                                        │
└────────────────────────────────────────┘

배경: rgba(0, 0, 0, 0.95)
게임 일시정지
```

### 타이밍 바

```
전체 크기: 600x80px
위치: 화면 중앙

구성:
┌──────────────────────────────────────┐
│                                      │
│  ┌──┐                                │ ← 이동 바
│  │██│ →                              │
│  └──┘                                │
│                                      │
│  │  │    │         │    │  │        │ ← 구분선
│  └──┴────┴─────────┴────┴──┘        │
│  BAD  GOOD  PERFECT  GOOD  BAD       │
└──────────────────────────────────────┘

존 크기:
- BAD (좌): 100px - rgba(255, 0, 0, 0.3)
- GOOD (좌): 120px - rgba(255, 215, 0, 0.3)
- PERFECT: 160px - rgba(0, 255, 0, 0.5)
- GOOD (우): 120px - rgba(255, 215, 0, 0.3)
- BAD (우): 100px - rgba(255, 0, 0, 0.3)

이동 바:
- 크기: 32x64px
- 색상: #FFFFFF
- 빛남: 0 0 20px rgba(255, 255, 255, 0.8)
- 속도: 2초/왕복
```

### 결과 화면

```
PERFECT:
┌────────────────────────────────────────┐
│         ★ PERFECT! ★                   │
│                                        │
│       Pit Stop Time: 3 sec             │
│                                        │
│  ✓ Tires Changed                       │
│  ✓ Durability Restored                 │
│                                        │
│         Returning to race...           │
└────────────────────────────────────────┘

- 제목: Orbitron Bold 64px, #00FF41
- 시간: Roboto Mono Bold 36px, #FFFFFF
- 표시 시간: 3초
```

### 타이어 선택 UI

```
┌────────────────────────────────────────┐
│         SELECT TIRE COMPOUND           │
│                                        │
│  ┌────────┐  ┌────────┐  ┌────────┐   │
│  │ SOFT   │  │ MEDIUM │  │  HARD  │   │
│  │   🔴   │  │   🟡   │  │   ⚪   │   │
│  │        │  │        │  │        │   │
│  │ +15%   │  │  +5%   │  │  Base  │   │
│  │ Grip   │  │ Grip   │  │ Grip   │   │
│  │        │  │        │  │        │   │
│  │ 30sec  │  │ 60sec  │  │ 100sec │   │
│  │        │  │        │  │        │   │
│  │[SELECT]│  │[SELECT]│  │[SELECT]│   │
│  └────────┘  └────────┘  └────────┘   │
│                                        │
│  Current: MEDIUM (45% remaining)       │
└────────────────────────────────────────┘

카드 크기: 180x280px
간격: 24px
배경: #1A1A1A
테두리: 2px solid (타이어 색상)

호버 효과: scale(1.05) + 빛남
```

---

## 애니메이션 가이드

### 기본 원칙

```
Duration (지속 시간):
- Instant: 0.1s (LED 점등)
- Fast: 0.15s (버튼 호버)
- Normal: 0.3s (패널 전환)
- Slow: 0.5s (화면 전환)

Easing (가속도):
- ease-out: 대부분의 UI
- ease-in-out: 부드러운 전환
- linear: 프로그레스 바, LED
- bounce: 알림
```

### HUD 애니메이션

#### 1. 내구도 바 감소

```java
// libGDX Actions
progressBar.addAction(Actions.sequence(
    Actions.sizeTo(newWidth, height, 0.3f, Interpolation.smooth)
));

// 색상 변경 (70% → 69% 경계)
progressBar.addAction(Actions.sequence(
    Actions.color(Color.YELLOW, 0.2f)
));
```

#### 2. 속도 숫자 업데이트

```java
// 숫자 카운트 업 애니메이션
// 284 → 285 부드럽게 전환
label.addAction(Actions.sequence(
    Actions.scaleTo(1.1f, 1.1f, 0.1f),
    Actions.scaleTo(1.0f, 1.0f, 0.1f)
));
```

#### 3. LED 순차 점등

```java
// 변속 LED 점등
for (int i = 0; i < ledCount; i++) {
    leds[i].addAction(Actions.sequence(
        Actions.delay(i * 0.05f),
        Actions.alpha(1.0f, 0.1f, Interpolation.linear)
    ));
}
```

#### 4. 순위 변동 알림

```java
// 슬라이드 인
notification.addAction(Actions.sequence(
    Actions.moveTo(-notification.getWidth(), y),
    Actions.moveTo(20, y, 0.3f, Interpolation.pow2Out),
    Actions.delay(2.0f),
    Actions.moveTo(-notification.getWidth(), y, 0.3f, Interpolation.pow2In)
));
```

#### 5. 점멸 효과 (20% 미만)

```java
// 내구도 낮을 때 점멸
progressBar.addAction(Actions.forever(
    Actions.sequence(
        Actions.alpha(0.3f, 0.5f),
        Actions.alpha(1.0f, 0.5f)
    )
));
```

#### 6. 충돌 시 화면 흔들림

```java
// 카메라 shake
camera.addAction(Actions.sequence(
    Actions.moveBy(8, 0, 0.05f),
    Actions.moveBy(-16, 0, 0.05f),
    Actions.moveBy(8, 0, 0.05f)
));
```

### 메뉴 애니메이션

#### 화면 전환 (Fade)

```java
// 현재 화면 페이드 아웃
currentScreen.addAction(Actions.sequence(
    Actions.fadeOut(0.3f),
    Actions.run(() -> game.setScreen(newScreen))
));

// 새 화면 페이드 인
newScreen.addAction(Actions.sequence(
    Actions.alpha(0),
    Actions.fadeIn(0.3f)
));
```

#### 모달 열기

```java
// 오버레이
overlay.addAction(Actions.fadeIn(0.2f));

// 모달
modal.addAction(Actions.sequence(
    Actions.alpha(0),
    Actions.scaleTo(0.8f, 0.8f),
    Actions.parallel(
        Actions.fadeIn(0.3f),
        Actions.scaleTo(1.0f, 1.0f, 0.3f, Interpolation.bounceOut)
    )
));
```

#### 버튼 호버

```java
button.addListener(new InputListener() {
    public void enter(...) {
        button.addAction(Actions.scaleTo(1.05f, 1.05f, 0.15f));
    }
    public void exit(...) {
        button.addAction(Actions.scaleTo(1.0f, 1.0f, 0.15f));
    }
});
```

---

## 반응형 디자인

### 해상도 대응

#### 기준 해상도

```
Primary (데스크톱):
- 1920x1080 (Full HD) - 메인 타겟
- 1280x720 (HD) - 최소 지원

Secondary:
- 2560x1440 (2K)
- 3840x2160 (4K)
```

### Scene2D 레이아웃 전략

#### 1. Viewport 설정

```java
// 게임 해상도 고정, 화면 비율 유지
FitViewport gameViewport = new FitViewport(1920, 1080, camera);

// HUD는 화면 크기에 맞춤
ScreenViewport hudViewport = new ScreenViewport();
```

#### 2. 앵커 기반 배치

```java
// 좌상단 고정
Table topLeft = new Table();
topLeft.setPosition(20, Gdx.graphics.getHeight() - 20, Align.topLeft);

// 우상단 고정
Table topRight = new Table();
topRight.setPosition(Gdx.graphics.getWidth() - 20, 
                     Gdx.graphics.getHeight() - 20, 
                     Align.topRight);

// 하단 중앙
Table bottomCenter = new Table();
bottomCenter.setPosition(Gdx.graphics.getWidth() / 2, 
                         20, 
                         Align.bottom);
```

#### 3. 비율 기반 크기

```java
// 화면 너비의 30%
float buttonWidth = Gdx.graphics.getWidth() * 0.3f;

// 최소/최대 크기 제한
buttonWidth = Math.max(200, Math.min(buttonWidth, 400));
```

#### 4. 그리드 시스템

```java
Table table = new Table();
table.setFillParent(true);

// 3열 그리드
table.add(card1).width(300).pad(10);
table.add(card2).width(300).pad(10);
table.add(card3).width(300).pad(10);
table.row();
```

### 해상도별 조정

#### 1920x1080 (기준)

```
레터박스: 100px
HUD 요소: 기본 크기
폰트: 기본 크기
여백: 20px
```

#### 1280x720 (축소)

```
레터박스: 60px
HUD 요소: 0.8 스케일
폰트: 0.8 스케일
여백: 12px
```

#### 2560x1440 (확대)

```
레터박스: 120px
HUD 요소: 1.2 스케일
폰트: 1.2 스케일
여백: 24px
```

### 스케일 계산 코드

```java
public class UIScaler {
    private static final float BASE_WIDTH = 1920f;
    private static final float BASE_HEIGHT = 1080f;
    
    public static float getScale() {
        float widthScale = Gdx.graphics.getWidth() / BASE_WIDTH;
        float heightScale = Gdx.graphics.getHeight() / BASE_HEIGHT;
        return Math.min(widthScale, heightScale);
    }
    
    public static float scaleSize(float baseSize) {
        return baseSize * getScale();
    }
    
    public static BitmapFont getScaledFont(BitmapFont font) {
        font.getData().setScale(getScale());
        return font;
    }
}
```

---

## 추가 UI 요소

### 1. 로딩 화면

```
┌────────────────────────────────────────┐
│                                        │
│                                        │
│         F1 RACING 2D                   │
│                                        │
│         [로딩 스피너]                    │
│                                        │
│         Loading Track...               │
│         ████████░░░░░░░░ 60%          │
│                                        │
└────────────────────────────────────────┘

배경: #000000
스피너: 회전하는 원형 (60x60px)
프로그레스 바: 400x8px
텍스트: Roboto Regular 18px
```

### 2. 일시정지 메뉴

```
┌────────────────────────────────────────┐
│           [오버레이]                     │
│                                        │
│     ┌──────────────────────┐           │
│     │      PAUSED          │           │
│     │                      │           │
│     │  [RESUME]            │           │
│     │  [RESTART]           │           │
│     │  [SETTINGS]          │           │
│     │  [QUIT TO MENU]      │           │
│     │                      │           │
│     └──────────────────────┘           │
│                                        │
└────────────────────────────────────────┘

오버레이: rgba(0, 0, 0, 0.8)
패널: rgba(26, 26, 26, 0.95)
버튼: Secondary Button 스타일
```

### 3. 결과 화면

```
┌────────────────────────────────────────┐
│                                        │
│         RACE FINISHED!                 │
│                                        │
│     ┌──────────────────────┐           │
│     │  FINAL POSITION: P2  │           │
│     │                      │           │
│     │  BEST LAP:           │           │
│     │  01:15.321          │           │
│     │                      │           │
│     │  TOTAL TIME:         │           │
│     │  08:45.678          │           │
│     │                      │           │
│     │  [VIEW STATS]        │           │
│     │  [RACE AGAIN]        │           │
│     │  [MAIN MENU]         │           │
│     └──────────────────────┘           │
│                                        │
└────────────────────────────────────────┘

배경: 흐린 레이스 화면 + 어두운 오버레이
순위에 따라 금/은/동 효과
```

### 4. 설정 화면

```
┌────────────────────────────────────────┐
│  [←]  SETTINGS                         │
├────────────────────────────────────────┤
│  [GRAPHICS] [AUDIO] [CONTROLS] [GAME]  │
│  ─────────                             │
│                                        │
│  GRAPHICS SETTINGS                     │
│  ┌────────────────────────────────┐    │
│  │ Resolution                      │    │
│  │ [1920x1080 ▼]                  │    │
│  │                                 │    │
│  │ Fullscreen                      │    │
│  │ [Toggle: ON]                    │    │
│  │                                 │    │
│  │ VSync                           │    │
│  │ [Toggle: ON]                    │    │
│  │                                 │    │
│  │ Particle Effects                │    │
│  │ ────────────────●── 80%         │    │
│  └────────────────────────────────┘    │
│                                        │
│  [RESET TO DEFAULT]      [APPLY]       │
└────────────────────────────────────────┘

탭: 활성화 탭은 밑줄 표시
설정 항목: 좌우 정렬
```

---

## 구현 팁 (libGDX Scene2D)

### 1. HUD Stage 설정

```java
public class RaceHUD {
    private Stage stage;
    private Skin skin;
    
    // 각 UI 요소
    private Table positionTable;
    private Table minimapTable;
    private Table speedometerTable;
    private Table vehicleDurabilityTable;
    private Table tireDurabilityTable;
    
    public RaceHUD() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/racing-skin.json"));
        
        createPositionUI();
        createMinimapUI();
        createSpeedometerUI();
        createDurabilityUIs();
    }
    
    private void createPositionUI() {
        positionTable = new Table();
        positionTable.setBackground(skin.getDrawable("panel-bg"));
        positionTable.pad(12);
        
        Label posLabel = new Label("P2", skin, "huge");
        Label lapLabel = new Label("LAP 5/10", skin, "large");
        
        positionTable.add(posLabel).padRight(20);
        positionTable.add(lapLabel);
        
        positionTable.setPosition(20, 
            Gdx.graphics.getHeight() - 20, 
            Align.topLeft);
        
        stage.addActor(positionTable);
    }
    
    public void update(float delta) {
        stage.act(delta);
    }
    
    public void render() {
        stage.draw();
    }
}
```

### 2. 반투명 배경 생성

```java
// Pixmap으로 반투명 배경 생성
Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
pixmap.setColor(26f/255f, 26f/255f, 26f/255f, 0.85f);
pixmap.fill();

Texture texture = new Texture(pixmap);
Drawable drawable = new TextureRegionDrawable(new TextureRegion(texture));

pixmap.dispose();

// Table에 적용
table.setBackground(drawable);
```

### 3. 프로그레스 바 커스텀

```java
public class DurabilityBar extends Actor {
    private float value = 1.0f; // 0.0 ~ 1.0
    private ShapeRenderer shapeRenderer;
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // 배경
        shapeRenderer.setColor(0.18f, 0.18f, 0.18f, 1);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        
        // 채워진 부분 (색상은 value에 따라 변경)
        Color fillColor = getColorForValue(value);
        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(getX(), getY(), getWidth() * value, getHeight());
        
        shapeRenderer.end();
        batch.begin();
    }
    
    private Color getColorForValue(float v) {
        if (v > 0.7f) return Color.GREEN;
        if (v > 0.4f) return Color.YELLOW;
        if (v > 0.2f) return Color.ORANGE;
        return Color.RED;
    }
    
    public void setValue(float value) {
        this.value = MathUtils.clamp(value, 0, 1);
    }
}
```

### 4. LED 배열 구현

```java
public class ShiftLightLED extends Table {
    private Image[] leds;
    private int ledCount = 7;
    
    public ShiftLightLED(Skin skin) {
        leds = new Image[ledCount];
        
        for (int i = 0; i < ledCount; i++) {
            Image led = new Image(skin.getDrawable("led-off"));
            led.setSize(12, 12);
            leds[i] = led;
            add(led).size(12, 12).pad(2);
        }
    }
    
    public void updateLEDs(float speedPercent) {
        int activeLEDs = (int)(speedPercent * ledCount);
        
        for (int i = 0; i < ledCount; i++) {
            if (i < activeLEDs) {
                Color color = getLEDColor(speedPercent);
                leds[i].setDrawable(skin.getDrawable("led-on"));
                leds[i].setColor(color);
            } else {
                leds[i].setDrawable(skin.getDrawable("led-off"));
                leds[i].setColor(Color.GRAY);
            }
        }
    }
    
    private Color getLEDColor(float percent) {
        if (percent > 0.95f) return Color.RED;
        if (percent > 0.8f) return Color.ORANGE;
        if (percent > 0.6f) return Color.YELLOW;
        return Color.GREEN;
    }
}
```

### 5. 미니맵 렌더링

```java
public class Minimap {
    private OrthographicCamera minimapCamera;
    private FrameBuffer fbo;
    private Texture minimapTexture;
    
    public Minimap(float trackWidth, float trackHeight) {
        minimapCamera = new OrthographicCamera();
        minimapCamera.setToOrtho(false, trackWidth, trackHeight);
        minimapCamera.position.set(trackWidth / 2, trackHeight / 2, 0);
        minimapCamera.update();
        
        fbo = new FrameBuffer(Pixmap.Format.RGB888, 180, 180, false);
    }
    
    public void render(SpriteBatch batch, TiledMapRenderer mapRenderer) {
        fbo.begin();
        Gdx.gl.glClearColor(0.04f, 0.04f, 0.04f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // 트랙 맵 렌더링
        mapRenderer.setView(minimapCamera);
        mapRenderer.render();
        
        // 차량 위치 점 렌더링
        batch.setProjectionMatrix(minimapCamera.combined);
        batch.begin();
        // 차량들 그리기...
        batch.end();
        
        fbo.end();
        
        minimapTexture = fbo.getColorBufferTexture();
    }
    
    public Texture getTexture() {
        return minimapTexture;
    }
}
```

---

## Skin (JSON) 정의 예시

```json
{
  "com.badlogic.gdx.graphics.Color": {
    "white": { "r": 1, "g": 1, "b": 1, "a": 1 },
    "black": { "r": 0, "g": 0, "b": 0, "a": 1 },
    "red": { "r": 0.88, "g": 0.02, "b": 0, "a": 1 },
    "green": { "r": 0, "g": 1, "b": 0, "a": 1 },
    "yellow": { "r": 1, "g": 1, "b": 0, "a": 1 }
  },
  "com.badlogic.gdx.graphics.g2d.BitmapFont": {
    "huge": { "file": "fonts/roboto-bold-72.fnt" },
    "large": { "file": "fonts/roboto-bold-48.fnt" },
    "medium": { "file": "fonts/roboto-bold-32.fnt" },
    "default": { "file": "fonts/roboto-regular-16.fnt" },
    "mono": { "file": "fonts/roboto-mono-bold-32.fnt" }
  },
  "com.badlogic.gdx.scenes.scene2d.ui.TextButton$TextButtonStyle": {
    "primary": {
      "up": "button-primary-up",
      "down": "button-primary-down",
      "over": "button-primary-over",
      "font": "default",
      "fontColor": "white"
    },
    "secondary": {
      "up": "button-secondary-up",
      "down": "button-secondary-down",
      "over": "button-secondary-over",
      "font": "default",
      "fontColor": "white"
    }
  },
  "com.badlogic.gdx.scenes.scene2d.ui.Label$LabelStyle": {
    "huge": {
      "font": "huge",
      "fontColor": "white"
    },
    "large": {
      "font": "large",
      "fontColor": "white"
    },
    "default": {
      "font": "default",
      "fontColor": "white"
    }
  }
}
```

---

## 체크리스트

### UI 구현 전

- [ ] 모든 필요한 폰트 파일 준비
- [ ] UI 스프라이트/아이콘 제작
- [ ] Skin JSON 파일 작성
- [ ] 색상 팔레트 정의

### HUD 구현

- [ ] 순위 & 랩 카운터 (좌상단)
- [ ] 미니맵 & 타이머 (우상단)
- [ ] 속도계 & 변속 LED (하단 중앙)
- [ ] 차량 내구도 게이지 (좌하단)
- [ ] 타이어 내구도 게이지 (우하단)
- [ ] 레터박스 (상하단)

### 메뉴 화면

- [ ] 메인 메뉴
- [ ] 차량 선택 화면
- [ ] 멀티플레이 로비
- [ ] 설정 화면
- [ ] 일시정지 메뉴
- [ ] 결과 화면

### 애니메이션

- [ ] 프로그레스 바 애니메이션
- [ ] LED 점등 효과
- [ ] 순위 변동 알림
- [ ] 화면 전환 효과
- [ ] 버튼 호버 효과

### 최적화

- [ ] Texture Atlas 사용
- [ ] Batch 렌더링 최적화
- [ ] 해상도별 테스트
- [ ] 메모리 누수 체크

---

## 마무리

이 디자인 가이드는 제공된 레퍼런스 이미지를 기반으로 작성되었으며, libGDX Scene2D를 활용한 구현을 고려하여 설계되었습니다.

**핵심 포인트:**
1. 모서리 배치로 중앙 시야 확보
2. 반투명 배경으로 게임 화면 가시성 유지
3. 명확한 계층과 대비로 가독성 확보
4. 일관된 색상과 타이포그래피
5. 부드러운 애니메이션과 피드백

모든 수치와 색상은 프로젝트 상황에 맞게 조정 가능하며, 이 가이드를 기반으로 팀원들과 협업하여 통일된 UI를 구현할 수 있습니다.
