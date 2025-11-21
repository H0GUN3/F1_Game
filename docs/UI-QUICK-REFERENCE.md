# UI Quick Reference

> F1 2D Racing Game UI 개발을 위한 빠른 참조 가이드

---

## 🎨 색상 코드 (복사해서 사용)

```java
// Primary
Color F1_RED = new Color(0xE10600FF);
Color RACING_BLACK = new Color(0x15151DFF);
Color NEON_CYAN = new Color(0x00F0FFFF);

// Status
Color SUCCESS_GREEN = new Color(0x00FF41FF);
Color WARNING_YELLOW = new Color(0xFFEB3BFF);
Color DANGER_RED = new Color(0xFF0000FF);
```

---

## 📐 간격 (Spacing)

```java
XXS = 4f   XS = 8f   SM = 12f   MD = 16f
LG = 24f   XL = 32f  XXL = 48f
```

---

## 🔤 폰트 크기

```java
MEGA = 4.0f    HUGE = 3.0f    LARGE = 2.0f
MEDIUM = 1.5f  NORMAL = 1.0f  SMALL = 0.8f
```

---

## ⏱️ 애니메이션 시간

```java
FAST = 0.15f    // 버튼 호버
NORMAL = 0.3f   // 화면 전환
SLOW = 0.5f     // 패널 슬라이드
DRAMATIC = 1.0f // 승리 연출
```

---

## 🎭 자주 쓰는 애니메이션

### 버튼 호버
```java
button.addAction(Actions.parallel(
    Actions.scaleTo(1.05f, 1.05f, 0.15f, Interpolation.pow2Out),
    Actions.color(NEON_CYAN, 0.15f)
));
```

### 팝업 등장
```java
dialog.addAction(Actions.sequence(
    Actions.scaleTo(0f, 0f),
    Actions.scaleTo(1.0f, 1.0f, 0.4f, Interpolation.bounceOut),
    Actions.fadeIn(0.3f)
));
```

### 화면 슬라이드
```java
screen.addAction(
    Actions.moveTo(0, 0, 0.3f, Interpolation.pow2Out)
);
```

---

## 🎯 HUD 위치 (1920x1080 기준)

```
좌상단: (40, 1040)    - 랩 타이머
중상단: (960, 1040)   - 트랙명
우상단: (1840, 1040)  - 순위
우상단: (1680, 880)   - 미니맵

좌하단: (40, 40)      - 타이어/내구도
우하단: (1840, 40)    - 속도/시프트라이트
```

---

## 🔊 사운드 볼륨

```java
UI 효과음: 0.3f
버튼 클릭: 0.5f
중요 알림: 0.7f
BGM: 0.6f
```

---

## 🎨 Shift Light 설정

```java
// RPM 임계값
float[] THRESHOLDS = {0.40f, 0.55f, 0.70f, 0.80f, 0.88f, 0.95f, 0.98f};

// LED 색상
GREEN (1-3), YELLOW (4-5), RED (6-7)

// 레드존 깜빡임 속도
FLASH_INTERVAL = 0.15f (150ms)
```

---

## 📊 성능 목표

```
Draw Calls: < 50 per frame
Heap Memory: < 512 MB
Frame Time: < 16.67ms (60 FPS)
Asset Loading: < 3 seconds
```

---

## 🛠️ 필수 Import

```java
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
```

---

## 🎯 체크리스트 (Phase 2)

### HUD 기본 요소
- [ ] 속도계 (디지털)
- [ ] Shift Light (7 LED)
- [ ] 기어 표시
- [ ] 랩 카운터
- [ ] 타이어 게이지
- [ ] 내구도 바

### 스타일
- [ ] F1 색상 적용
- [ ] 커스텀 폰트
- [ ] 버튼 호버 효과
- [ ] 카본 텍스처

### 사운드
- [ ] 클릭 효과음
- [ ] Shift 비프음
- [ ] BGM

---

## 🚀 빠른 시작 코드

```java
// 1. HUD Manager 생성
HUDManager hud = new HUDManager(viewport, assetManager);

// 2. 매 프레임 업데이트
hud.update(delta, vehicleController, raceManager);

// 3. 렌더링
hud.render();

// 4. 정리
hud.dispose();
```

---

**빠른 접근**: `Ctrl+F`로 필요한 내용 검색  
**전체 가이드**: `UI-DESIGN-GUIDE.md` 참조
