# SHIFT-LIGHT.md

## Overview
F1 스타일의 변속 지시등(Shift Light) 시스템으로, 엔진 RPM 상태를 시각적으로 표시하여 최적의 변속 타이밍을 안내합니다.

**Owner**: UI Lead  
**Priority**: P1 (Phase 2 후반 구현)  
**Status**: Draft  
**Last Updated**: 2025-01-15  
**Related Specs**: `HUD-SPECIFICATION.md`, `VEHICLE-PHYSICS.md`

---

## 1. Feature Goals (기능 목표)

### 1.1 핵심 목적
- **시각적 피드백**: 엔진 RPM 상태를 직관적으로 전달
- **성능 가이드**: 최적의 변속 시점 안내로 플레이어 실력 향상
- **몰입감 증대**: F1 실제 차량의 시각적 요소 재현

### 1.2 사용자 경험
- 초보자: 변속 타이밍을 배우는 시각적 튜토리얼
- 숙련자: 레드존 관리로 최고 성능 추출
- 관전자: 다른 플레이어의 주행 상태 파악

---

## 2. Visual Design (시각 디자인)

### 2.1 기본 레이아웃

```
┌────────────────────────────────────┐
│        Speed: 285 km/h             │
│                                    │
│   Shift Light Bar:                 │
│   ┌─┬─┬─┬─┬─┬─┬─┐                │
│   │ │ │ │ │ │ │ │  ← 7개 LED    │
│   └─┴─┴─┴─┴─┴─┴─┘                │
│                                    │
│        Gear: 5                     │
└────────────────────────────────────┘
```

**위치**: HUD 하단 우측, 속도계 바로 아래

### 2.2 LED 구성

```java
public class ShiftLightConfig {
    public static final int NUM_LEDS = 7;
    public static final int LED_WIDTH = 40;
    public static final int LED_HEIGHT = 20;
    public static final int LED_SPACING = 5;
    
    // RPM 임계값 (0.0 ~ 1.0)
    public static final float[] RPM_THRESHOLDS = {
        0.40f,  // LED 1: 40%
        0.55f,  // LED 2: 55%
        0.70f,  // LED 3: 70%
        0.80f,  // LED 4: 80%
        0.88f,  // LED 5: 88%
        0.95f,  // LED 6: 95%
        0.98f   // LED 7: 98% (레드존)
    };
    
    // LED 색상
    public static final Color[] LED_COLORS = {
        new Color(0.0f, 1.0f, 0.0f, 1.0f),  // 초록 (1-3)
        new Color(0.0f, 1.0f, 0.0f, 1.0f),
        new Color(0.0f, 1.0f, 0.0f, 1.0f),
        new Color(1.0f, 1.0f, 0.0f, 1.0f),  // 노랑 (4-5)
        new Color(1.0f, 0.5f, 0.0f, 1.0f),  // 주황 (5)
        new Color(1.0f, 0.0f, 0.0f, 1.0f),  // 빨강 (6)
        new Color(1.0f, 0.0f, 0.0f, 1.0f)   // 빨강 (7)
    };
}
```

### 2.3 RPM 단계별 시각 효과

#### Level 1: 저속 (0-40% RPM)
```
[⚫][⚫][⚫][⚫][⚫][⚫][⚫]  ← 모두 꺼짐
```

#### Level 2: 중속 (40-70% RPM)
```
[🟢][🟢][🟢][⚫][⚫][⚫][⚫]  ← 초록 LED 점등
```

#### Level 3: 고속 (70-88% RPM)
```
[🟢][🟢][🟢][🟡][🟡][⚫][⚫]  ← 노란/주황 LED 추가
```

#### Level 4: 레드존 진입 (88-95% RPM)
```
[🟢][🟢][🟢][🟡][🟡][🔴][⚫]  ← 빨간 LED 점등
```

#### Level 5: 변속 권장 (95%+ RPM)
```
[🔴][🔴][🔴][🔴][🔴][🔴][🔴]  ← 모든 LED 빨강 + 깜빡임
```

---

## 3. Implementation (구현)

### 3.1 ShiftLight Class

```java
public class ShiftLight extends Table {
    private Image[] ledImages;
    private Texture ledOnTexture;
    private Texture ledOffTexture;
    
    private float currentRPM = 0f;  // 0.0 ~ 1.0
    private boolean isFlashing = false;
    private float flashTimer = 0f;
    private static final float FLASH_INTERVAL = 0.15f; // 150ms
    
    private Sound shiftBeepSound;
    private boolean hasPlayedBeep = false;
    
    public ShiftLight(AssetManager assetManager) {
        // LED 텍스처 로드
        ledOnTexture = assetManager.get("ui/hud/led_on.png");
        ledOffTexture = assetManager.get("ui/hud/led_off.png");
        shiftBeepSound = assetManager.get("sounds/ui/shift_beep.ogg");
        
        // LED 이미지 배열 생성
        ledImages = new Image[ShiftLightConfig.NUM_LEDS];
        
        // 레이아웃 구성
        this.row();
        for (int i = 0; i < ShiftLightConfig.NUM_LEDS; i++) {
            ledImages[i] = new Image(ledOffTexture);
            ledImages[i].setSize(
                ShiftLightConfig.LED_WIDTH,
                ShiftLightConfig.LED_HEIGHT
            );
            
            this.add(ledImages[i])
                .size(ShiftLightConfig.LED_WIDTH, ShiftLightConfig.LED_HEIGHT)
                .pad(ShiftLightConfig.LED_SPACING / 2f);
        }
    }
    
    public void update(float delta, float speedKmh, int gear) {
        // RPM 계산 (속도 기반 근사)
        currentRPM = calculateRPM(speedKmh, gear);
        
        // 깜빡임 타이머 업데이트
        if (isFlashing) {
            flashTimer += delta;
            if (flashTimer >= FLASH_INTERVAL) {
                flashTimer = 0f;
                toggleFlash();
            }
        }
        
        // LED 업데이트
        updateLEDs();
        
        // 변속 지시 사운드
        if (currentRPM >= 0.95f && !hasPlayedBeep) {
            shiftBeepSound.play(0.7f);
            hasPlayedBeep = true;
        } else if (currentRPM < 0.90f) {
            hasPlayedBeep = false;
        }
    }
    
    private float calculateRPM(float speedKmh, int gear) {
        // 기어별 최고 속도 (간단한 모델)
        float[] gearMaxSpeeds = {
            50f,   // 1단
            80f,   // 2단
            120f,  // 3단
            160f,  // 4단
            200f,  // 5단
            240f,  // 6단
            300f   // 7단
        };
        
        if (gear < 1 || gear > gearMaxSpeeds.length) {
            return 0f; // 중립 또는 무효 기어
        }
        
        float gearMaxSpeed = gearMaxSpeeds[gear - 1];
        float rpm = speedKmh / gearMaxSpeed;
        
        // RPM 클램프 (0.0 ~ 1.0)
        return Math.min(Math.max(rpm, 0f), 1.0f);
    }
    
    private void updateLEDs() {
        // 레드존 (95% 이상): 모든 LED 빨강 + 깜빡임
        if (currentRPM >= 0.95f) {
            isFlashing = true;
            for (int i = 0; i < ShiftLightConfig.NUM_LEDS; i++) {
                ledImages[i].setDrawable(new TextureRegionDrawable(ledOnTexture));
                ledImages[i].setColor(Color.RED);
            }
            return;
        }
        
        isFlashing = false;
        flashTimer = 0f;
        
        // 일반 RPM: 순차 점등
        for (int i = 0; i < ShiftLightConfig.NUM_LEDS; i++) {
            if (currentRPM >= ShiftLightConfig.RPM_THRESHOLDS[i]) {
                // LED 켜기
                ledImages[i].setDrawable(new TextureRegionDrawable(ledOnTexture));
                ledImages[i].setColor(ShiftLightConfig.LED_COLORS[i]);
            } else {
                // LED 끄기
                ledImages[i].setDrawable(new TextureRegionDrawable(ledOffTexture));
                ledImages[i].setColor(Color.GRAY);
            }
        }
    }
    
    private void toggleFlash() {
        // 깜빡임 구현
        for (Image led : ledImages) {
            if (led.getColor().a > 0.5f) {
                led.getColor().a = 0.3f; // 어둡게
            } else {
                led.getColor().a = 1.0f; // 밝게
            }
        }
    }
}
```

### 3.2 HUDManager 통합

```java
public class HUDManager {
    private ShiftLight shiftLight;
    
    private void createHUD() {
        // ... 기존 HUD 요소들
        
        // Shift Light 추가
        shiftLight = new ShiftLight(assetManager);
        
        // 레이아웃
        Table bottomRight = new Table();
        bottomRight.add(speedometer).row();
        bottomRight.add(shiftLight).padTop(10).row();
        bottomRight.add(gearDisplay).padTop(5);
        
        // ...
    }
    
    public void update(float delta) {
        // ...
        
        // Shift Light 업데이트
        float speedKmh = vehicleController.getSpeed() * 3.6f;
        int gear = vehicleController.getCurrentGear();
        shiftLight.update(delta, speedKmh, gear);
    }
}
```

---

## 4. Advanced Features (고급 기능)

### 4.1 기어별 RPM 맵핑 (정교한 버전)

```java
public class RPMCalculator {
    // 기어별 RPM 커브 (실제 F1 데이터 기반)
    private static final float[][] GEAR_RPM_CURVES = {
        // {최소 RPM, 최대 RPM, 최적 변속 RPM}
        {0.30f, 1.00f, 0.95f},  // 1단
        {0.40f, 1.00f, 0.95f},  // 2단
        {0.50f, 1.00f, 0.96f},  // 3단
        {0.55f, 1.00f, 0.96f},  // 4단
        {0.60f, 1.00f, 0.97f},  // 5단
        {0.65f, 1.00f, 0.97f},  // 6단
        {0.70f, 1.00f, 0.98f}   // 7단
    };
    
    public static float calculateRPM(float speed, int gear, float maxSpeed) {
        if (gear < 1 || gear > GEAR_RPM_CURVES.length) {
            return 0f;
        }
        
        float[] curve = GEAR_RPM_CURVES[gear - 1];
        float minRPM = curve[0];
        float maxRPM = curve[1];
        
        // 속도를 기어 범위 내 RPM으로 변환
        float gearRatio = (float) gear / GEAR_RPM_CURVES.length;
        float speedRatio = speed / (maxSpeed * gearRatio);
        
        return MathUtils.lerp(minRPM, maxRPM, speedRatio);
    }
}
```

### 4.2 커스터마이징 옵션

```java
public class ShiftLightSettings {
    private int ledCount = 7;           // 5 ~ 10
    private ColorScheme colorScheme;
    private float sensitivity = 1.0f;   // RPM 임계값 조정
    
    public enum ColorScheme {
        F1_CLASSIC,      // 초록 → 노랑 → 빨강
        RALLY,           // 파랑 → 흰색 → 빨강
        NEON,            // 보라 → 청록 → 분홍
        MONOCHROME       // 회색 → 흰색
    }
    
    public Color[] getColors(ColorScheme scheme) {
        switch(scheme) {
            case F1_CLASSIC:
                return ShiftLightConfig.LED_COLORS;
            case RALLY:
                return new Color[]{
                    Color.BLUE, Color.CYAN, Color.WHITE,
                    Color.YELLOW, Color.ORANGE, Color.RED, Color.RED
                };
            case NEON:
                return new Color[]{
                    Color.PURPLE, Color.MAGENTA, Color.PINK,
                    Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED
                };
            case MONOCHROME:
                return new Color[]{
                    Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY,
                    Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE
                };
            default:
                return ShiftLightConfig.LED_COLORS;
        }
    }
}
```

### 4.3 애니메이션 효과

```java
// LED 점등 애니메이션 (순차적으로 켜짐)
public void animateLEDOn(int ledIndex) {
    ledImages[ledIndex].addAction(Actions.sequence(
        Actions.alpha(0f),
        Actions.fadeIn(0.1f),
        Actions.scaleTo(1.2f, 1.2f, 0.05f),
        Actions.scaleTo(1.0f, 1.0f, 0.05f)
    ));
}

// 레드존 깜빡임 (더 역동적)
public void animateRedzone() {
    for (Image led : ledImages) {
        led.addAction(Actions.forever(
            Actions.sequence(
                Actions.color(Color.RED, 0.1f),
                Actions.color(Color.SCARLET, 0.1f)
            )
        ));
    }
}

// 최고 성능 포인트 (Perfect Shift) 표시
public void showPerfectShift() {
    Label perfectLabel = new Label("PERFECT SHIFT!", skin);
    perfectLabel.setFontScale(2.0f);
    perfectLabel.setColor(Color.GOLD);
    perfectLabel.addAction(Actions.sequence(
        Actions.fadeIn(0.2f),
        Actions.delay(1.0f),
        Actions.fadeOut(0.5f),
        Actions.removeActor()
    ));
    stage.addActor(perfectLabel);
}
```

---

## 5. Audio Integration (오디오 통합)

### 5.1 사운드 효과

```java
public class ShiftLightAudio {
    private Sound shiftBeep;           // 변속 지시 비프음
    private Sound perfectShiftSound;   // 완벽한 변속 사운드
    private Sound overRevSound;        // 과회전 경고음
    
    private long beepSoundId = -1;
    
    public void load(AssetManager assetManager) {
        shiftBeep = assetManager.get("sounds/ui/shift_beep.ogg");
        perfectShiftSound = assetManager.get("sounds/ui/perfect_shift.ogg");
        overRevSound = assetManager.get("sounds/ui/over_rev.ogg");
    }
    
    public void playShiftBeep(float rpm) {
        // RPM에 따라 피치 조정 (높은 RPM = 높은 음)
        float pitch = 0.8f + (rpm * 0.4f); // 0.8 ~ 1.2
        
        if (beepSoundId != -1) {
            shiftBeep.stop(beepSoundId);
        }
        beepSoundId = shiftBeep.play(0.6f, pitch, 0f);
    }
    
    public void playPerfectShift() {
        perfectShiftSound.play(0.8f);
    }
    
    public void playOverRev() {
        overRevSound.play(0.5f);
    }
}
```

---

## 6. Testing & Validation

### 6.1 Unit Tests

```java
@Test
@DisplayName("LED should light sequentially as RPM increases")
public void testLEDSequence() {
    ShiftLight shiftLight = new ShiftLight(assetManager);
    
    // 40% RPM: 1개 LED
    shiftLight.update(0f, 120f, 3); // ~40% RPM
    assertThat(shiftLight.getActiveLEDCount()).isEqualTo(1);
    
    // 70% RPM: 3개 LED
    shiftLight.update(0f, 210f, 3);
    assertThat(shiftLight.getActiveLEDCount()).isEqualTo(3);
    
    // 95% RPM: 7개 LED (레드존)
    shiftLight.update(0f, 285f, 3);
    assertThat(shiftLight.getActiveLEDCount()).isEqualTo(7);
    assertThat(shiftLight.isFlashing()).isTrue();
}

@Test
@DisplayName("Shift beep should play only once per redzone entry")
public void testShiftBeep() {
    ShiftLight shiftLight = new ShiftLight(assetManager);
    int beepCount = 0;
    
    // 레드존 진입
    shiftLight.update(0f, 290f, 3);
    if (shiftLight.hasPlayedBeep()) beepCount++;
    
    // 레드존 유지 (비프 반복 없음)
    shiftLight.update(0f, 295f, 3);
    if (shiftLight.hasPlayedBeep()) beepCount++;
    
    assertThat(beepCount).isEqualTo(1);
}
```

### 6.2 시각적 검증

- [ ] 모든 LED가 올바른 색상으로 점등되는가?
- [ ] 깜빡임 효과가 눈에 띄는가?
- [ ] 레드존 진입이 명확하게 구분되는가?
- [ ] 다양한 해상도에서 잘 보이는가? (720p, 1080p, 1440p)

---

## 7. Performance Considerations

### 7.1 최적화

```java
// LED 텍스처 아틀라스 사용 (Draw Call 최소화)
TextureAtlas hudAtlas = assetManager.get("ui/hud/hud_atlas.atlas");
TextureRegion ledOn = hudAtlas.findRegion("led_on");
TextureRegion ledOff = hudAtlas.findRegion("led_off");

// 불필요한 업데이트 방지
private float lastRPM = -1f;

public void update(float delta, float speedKmh, int gear) {
    float newRPM = calculateRPM(speedKmh, gear);
    
    // RPM 변화가 미미하면 스킵
    if (Math.abs(newRPM - lastRPM) < 0.01f) {
        return;
    }
    
    lastRPM = newRPM;
    updateLEDs();
}
```

---

## 8. Future Enhancements

### Phase 6+ 추가 기능
- [ ] **DRS (Drag Reduction System) 표시**: DRS 활성화 시 특수 LED 점등
- [ ] **KERS (에너지 회수) 게이지**: 하이브리드 파워 잔량 표시
- [ ] **타이어 온도 연동**: 차가운 타이어는 LED 파랑, 뜨거우면 빨강
- [ ] **멀티플레이어 비교**: 상대방의 Shift Light도 표시 (작게)

---

**Version**: 1.0.0  
**Status**: Ready for Implementation  
**Priority**: P1 (Phase 2 후반)  
**Estimated Effort**: 2-3 days
