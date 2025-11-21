# UI-DESIGN-GUIDE.md

## Overview
F1 2D Racing Game의 전체 UI/UX 디자인 철학 및 가이드라인입니다. 현대적이고 역동적인 F1 브랜드 아이덴티티를 반영합니다.

**Owner**: UI/UX Lead  
**Status**: Living Document  
**Last Updated**: 2025-01-15

---

## 🎨 1. Design Philosophy (디자인 철학)

### 1.1 핵심 가치
```
Speed (속도감) + Clarity (명확성) + Elegance (우아함)
```

- **Speed**: 역동적인 애니메이션, 날카로운 각도, 빠른 트랜지션
- **Clarity**: 중요한 정보가 즉시 눈에 들어오는 계층 구조
- **Elegance**: 미니멀하면서도 프리미엄한 F1 브랜드 감성

### 1.2 디자인 키워드
- ⚡ **Dynamic**: 정적이지 않고 살아있는 UI
- 🎯 **Functional**: 모든 요소가 목적을 가짐
- 🏁 **Racing-First**: F1 DNA가 녹아있는 디자인
- 💎 **Premium**: 고급스러운 질감과 효과

---

## 🎨 2. Color Palette (색상 팔레트)

### 2.1 Primary Colors (주요 색상)

```java
public class F1Colors {
    // Brand Colors
    public static final Color F1_RED = new Color(0xE10600FF);        // F1 시그니처 레드
    public static final Color RACING_BLACK = new Color(0x15151DFF);  // 깊은 검정
    public static final Color CARBON_GRAY = new Color(0x2A2A35FF);   // 카본 그레이

    // Accent Colors
    public static final Color NEON_CYAN = new Color(0x00F0FFFF);     // 네온 청록
    public static final Color GOLD_TROPHY = new Color(0xFFD700FF);   // 금색 (1위)
    public static final Color SILVER_MEDAL = new Color(0xC0C0C0FF);  // 은색 (2위)
    public static final Color BRONZE_MEDAL = new Color(0xCD7F32FF);  // 동색 (3위)

    // Status Colors
    public static final Color SUCCESS_GREEN = new Color(0x00FF41FF); // 성공/안전
    public static final Color WARNING_YELLOW = new Color(0xFFEB3BFF); // 경고
    public static final Color DANGER_RED = new Color(0xFF0000FF);    // 위험
    public static final Color INFO_BLUE = new Color(0x0080FFFF);     // 정보

    // Background Colors
    public static final Color BG_DARK = new Color(0x0A0A0FFF);       // 어두운 배경
    public static final Color BG_PANEL = new Color(0x1C1C24FF);      // 패널 배경
    public static final Color BG_HOVER = new Color(0x2E2E3CFF);      // 호버 상태
}
```

### 2.2 Color Usage (색상 사용 규칙)

| 요소 | 색상 | 용도 |
|------|------|------|
| **배경** | RACING_BLACK | 메인 화면 배경 |
| **패널** | CARBON_GRAY | 메뉴, 다이얼로그 배경 |
| **강조** | F1_RED | CTA 버튼, 중요 알림 |
| **액센트** | NEON_CYAN | 호버, 선택된 항목 |
| **텍스트 (주)** | WHITE | 주요 정보 |
| **텍스트 (부)** | LIGHT_GRAY | 보조 정보 |

---

## 🔤 3. Typography (타이포그래피)

### 3.1 Font Family

```java
public class F1Fonts {
    // Primary: Formula1 Display (F1 공식 폰트 느낌)
    public static final String DISPLAY_BOLD = "fonts/f1_display_bold.ttf";
    public static final String DISPLAY_REGULAR = "fonts/f1_display_regular.ttf";

    // Secondary: Racing Sans (읽기 쉬운 산세리프)
    public static final String RACING_SANS = "fonts/racing_sans.ttf";

    // Monospace: Digital (타이머, 속도 등)
    public static final String DIGITAL = "fonts/digital_7_mono.ttf";

    // 대체 폰트: Roboto (기본 제공)
    public static final String FALLBACK = "Roboto";
}
```

### 3.2 Font Scale (폰트 크기)

```java
public class FontScale {
    public static final float MEGA = 4.0f;      // 96px - 메인 타이틀
    public static final float HUGE = 3.0f;      // 72px - 섹션 헤더
    public static final float LARGE = 2.0f;     // 48px - 부제목
    public static final float MEDIUM = 1.5f;    // 36px - 강조 텍스트
    public static final float NORMAL = 1.0f;    // 24px - 본문
    public static final float SMALL = 0.8f;     // 19px - 보조 정보
    public static final float TINY = 0.6f;      // 14px - 레이블
}
```

### 3.3 Typography Rules

```
✅ DO:
- 속도/시간은 DIGITAL 폰트 사용
- 제목/버튼은 DISPLAY_BOLD
- 본문은 RACING_SANS
- 숫자는 모노스페이스 정렬

❌ DON'T:
- 너무 많은 폰트 혼용 (최대 3종)
- 12px 이하 폰트 사용 (가독성 저하)
- 과도한 이탤릭체 (F1은 직선 미학)
```

---

## 🎭 4. Visual Style (시각 스타일)

### 4.1 Shape Language (도형 언어)

```
┌─────────────────────────────┐
│  F1 UI Shape DNA:           │
│                             │
│  ╱╲  = Speed (각진 삼각형)   │
│  ▬   = Precision (날카로운선)│
│  ◢   = Direction (방향성)   │
└─────────────────────────────┘
```

#### 디자인 모티프
```css
/* 버튼: 우측 상단 각진 모서리 */
.f1-button {
    border-radius: 0 8px 0 8px;  /* 대각선 각진 느낌 */
    clip-path: polygon(0 0, 100% 0, 100% 80%, 95% 100%, 0 100%);
}

/* 패널: 좌상단 잘린 모서리 */
.f1-panel {
    clip-path: polygon(8% 0, 100% 0, 100% 100%, 0 100%, 0 8%);
}

/* 속도계: 육각형 */
.f1-hexagon {
    clip-path: polygon(25% 0%, 75% 0%, 100% 50%, 75% 100%, 25% 100%, 0% 50%);
}
```

### 4.2 Effects & Materials (효과 및 재질)

#### Glassmorphism (유리 효과)
```java
// 반투명 유리 패널
public static Drawable createGlassPanel() {
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(new Color(0.1f, 0.1f, 0.15f, 0.7f)); // 반투명
    pixmap.fill();

    Texture texture = new Texture(pixmap);
    pixmap.dispose();

    return new TextureRegionDrawable(new TextureRegion(texture));
}

// 흐림 효과 (Blur Shader)
ShaderProgram blurShader = new ShaderProgram(
        Gdx.files.internal("shaders/blur.vert"),
        Gdx.files.internal("shaders/blur.frag")
);
```

#### Carbon Fiber (카본 파이버 텍스처)
```java
// 카본 패턴 생성
public static Texture createCarbonTexture() {
    Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);

    // 어두운 베이스
    pixmap.setColor(F1Colors.CARBON_GRAY);
    pixmap.fill();

    // 대각선 패턴 (카본 느낌)
    pixmap.setColor(new Color(0.15f, 0.15f, 0.2f, 1f));
    for (int i = 0; i < 64; i += 4) {
        pixmap.drawLine(i, 0, 0, i);
        pixmap.drawLine(64, i, i, 64);
    }

    return new Texture(pixmap);
}
```

#### Glow Effect (발광 효과)
```java
// 네온 발광 (레이어 기반)
public static void addGlowEffect(Image image, Color glowColor) {
    // 배경 레이어 (블러 + 확대)
    Image glowLayer = new Image(image.getDrawable());
    glowLayer.setColor(glowColor);
    glowLayer.setSize(
            image.getWidth() * 1.2f,
            image.getHeight() * 1.2f
    );
    glowLayer.setPosition(
            image.getX() - image.getWidth() * 0.1f,
            image.getY() - image.getHeight() * 0.1f
    );
    glowLayer.getColor().a = 0.5f;

    // 원본 이미지 위에 배치
    Group group = new Group();
    group.addActor(glowLayer);
    group.addActor(image);
}
```

---

## 🎬 5. Animations (애니메이션)

### 5.1 Animation Principles (애니메이션 원칙)

```
1. FAST IN, SLOW OUT (가속 → 감속)
2. OVERSHOOT (목표 초과 후 반동)
3. ANTICIPATION (예비 동작)
4. FOLLOW THROUGH (잔상 효과)
```

### 5.2 Transition Speeds (전환 속도)

```java
public class AnimationDuration {
    public static final float INSTANT = 0.0f;       // 즉시
    public static final float FAST = 0.15f;         // 빠름 (버튼 호버)
    public static final float NORMAL = 0.3f;        // 보통 (화면 전환)
    public static final float SLOW = 0.5f;          // 느림 (패널 슬라이드)
    public static final float DRAMATIC = 1.0f;      // 극적 (승리 연출)
}

public class Easing {
    // libGDX Interpolation 사용
    public static final Interpolation SPEED = Interpolation.pow2Out;
    public static final Interpolation BOUNCE = Interpolation.bounceOut;
    public static final Interpolation ELASTIC = Interpolation.elasticOut;
    public static final Interpolation SMOOTH = Interpolation.smooth;
}
```

### 5.3 UI Animation Examples (예제)

#### 버튼 호버
```java
public static void animateButtonHover(Button button) {
    button.addListener(new InputListener() {
        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            button.clearActions();
            button.addAction(Actions.parallel(
                    Actions.scaleTo(1.05f, 1.05f, 0.15f, Interpolation.pow2Out),
                    Actions.color(F1Colors.NEON_CYAN, 0.15f)
            ));

            // 사운드
            hoverSound.play(0.3f);
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            button.clearActions();
            button.addAction(Actions.parallel(
                    Actions.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.pow2Out),
                    Actions.color(Color.WHITE, 0.15f)
            ));
        }
    });
}
```

#### 화면 전환 (슬라이드)
```java
public static void transitionToScreen(Screen newScreen, Direction direction) {
    Table currentUI = getCurrentUI();
    Table newUI = newScreen.getUI();

    // 현재 화면: 밖으로 슬라이드
    float slideOutX = direction == Direction.LEFT ? -stage.getWidth() : stage.getWidth();
    currentUI.addAction(Actions.sequence(
            Actions.moveBy(slideOutX, 0, 0.3f, Interpolation.pow2In),
            Actions.removeActor()
    ));

    // 새 화면: 안으로 슬라이드
    float slideInX = direction == Direction.LEFT ? stage.getWidth() : -stage.getWidth();
    newUI.setPosition(slideInX, 0);
    stage.addActor(newUI);
    newUI.addAction(
            Actions.moveTo(0, 0, 0.3f, Interpolation.pow2Out)
    );
}
```

#### 팝업 등장 (바운스)
```java
public static void showPopup(Dialog dialog) {
    dialog.setTransform(true);
    dialog.setScale(0f);
    dialog.getColor().a = 0f;

    dialog.addAction(Actions.parallel(
            Actions.scaleTo(1.0f, 1.0f, 0.4f, Interpolation.bounceOut),
            Actions.fadeIn(0.3f)
    ));
}
```

#### 카운트다운 (펄스)
```java
public static void animateCountdown(Label label, int number) {
    label.setText(String.valueOf(number));
    label.setFontScale(1.0f);

    label.addAction(Actions.sequence(
            Actions.parallel(
                    Actions.scaleTo(2.0f, 2.0f, 0.3f, Interpolation.elasticOut),
                    Actions.color(F1Colors.F1_RED, 0.3f)
            ),
            Actions.delay(0.7f),
            Actions.parallel(
                    Actions.scaleTo(0f, 0f, 0.2f, Interpolation.pow3In),
                    Actions.fadeOut(0.2f)
            )
    ));
}
```

---

## 📐 6. Layout System (레이아웃 시스템)

### 6.1 Grid System (그리드 시스템)

```
1920x1080 기준:
┌─────────────────────────────────────────┐
│ [────── 12 Column Grid ──────]          │
│ Margin: 40px                            │
│ Gutter: 20px                            │
│ Column Width: (1920-80-220)/12 = 135px │
└─────────────────────────────────────────┘
```

```java
public class GridLayout {
    public static final int COLUMNS = 12;
    public static final float MARGIN = 40f;
    public static final float GUTTER = 20f;

    public static float getColumnWidth(int screenWidth) {
        return (screenWidth - MARGIN * 2 - GUTTER * (COLUMNS - 1)) / COLUMNS;
    }

    public static float getColumnX(int column, int screenWidth) {
        float colWidth = getColumnWidth(screenWidth);
        return MARGIN + (colWidth + GUTTER) * column;
    }
}
```

### 6.2 Spacing Scale (간격 체계)

```java
public class Spacing {
    public static final float XXS = 4f;    // 극소
    public static final float XS = 8f;     // 최소
    public static final float SM = 12f;    // 작음
    public static final float MD = 16f;    // 보통
    public static final float LG = 24f;    // 큼
    public static final float XL = 32f;    // 최대
    public static final float XXL = 48f;   // 극대
}
```

### 6.3 Responsive Design (반응형 디자인)

```java
public class ResponsiveUI {
    public enum ScreenSize {
        SMALL(1280, 720),    // 720p
        MEDIUM(1920, 1080),  // 1080p (기준)
        LARGE(2560, 1440);   // 1440p

        public final int width, height;
        ScreenSize(int w, int h) { width = w; height = h; }
    }

    public static float getScaleFactor(int screenWidth) {
        // 1920을 기준으로 스케일 계산
        return screenWidth / 1920f;
    }

    public static void applyResponsiveLayout(Table table, int screenWidth) {
        float scale = getScaleFactor(screenWidth);

        table.pad(Spacing.LG * scale);
        table.defaults().space(Spacing.MD * scale);

        // 폰트 크기도 조정
        for (Actor actor : table.getChildren()) {
            if (actor instanceof Label) {
                Label label = (Label) actor;
                label.setFontScale(label.getFontScaleX() * scale);
            }
        }
    }
}
```

---

## 🎮 7. Component Library (컴포넌트 라이브러리)

### 7.1 F1Button (F1 스타일 버튼)

```java
public class F1Button extends TextButton {
    private ParticleEffect hoverParticles;

    public F1Button(String text, Skin skin) {
        super(text, skin, "f1-style");

        // 기본 스타일
        setColor(F1Colors.CARBON_GRAY);
        getLabel().setColor(Color.WHITE);
        getLabel().setFontScale(FontScale.MEDIUM);

        // 호버 효과
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                clearActions();
                addAction(Actions.sequence(
                        Actions.parallel(
                                Actions.color(F1Colors.F1_RED, 0.2f),
                                Actions.scaleTo(1.05f, 1.05f, 0.2f, Interpolation.pow2Out)
                        )
                ));

                // 파티클 효과
                showHoverParticles();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                clearActions();
                addAction(Actions.sequence(
                        Actions.parallel(
                                Actions.color(F1Colors.CARBON_GRAY, 0.2f),
                                Actions.scaleTo(1.0f, 1.0f, 0.2f)
                        )
                ));
            }
        });
    }

    private void showHoverParticles() {
        // 미세한 스파크 파티클
        if (hoverParticles == null) {
            hoverParticles = new ParticleEffect();
            hoverParticles.load(
                    Gdx.files.internal("effects/button_spark.p"),
                    Gdx.files.internal("effects/")
            );
        }
        hoverParticles.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
        hoverParticles.start();
    }
}
```

### 7.2 F1Panel (F1 스타일 패널)

```java
public class F1Panel extends Table {
    private Image carbonBG;
    private Image glowBorder;

    public F1Panel(String title, Skin skin) {
        // 카본 파이버 배경
        carbonBG = new Image(createCarbonTexture());
        carbonBG.setColor(F1Colors.BG_PANEL);
        setBackground(new TextureRegionDrawable((TextureRegion) carbonBG.getDrawable()));

        // 발광 테두리
        glowBorder = new Image(Gdx.files.internal("ui/glow_border.png"));
        glowBorder.setColor(F1Colors.NEON_CYAN);
        glowBorder.getColor().a = 0.3f;

        // 제목 (좌상단 각진 헤더)
        if (title != null) {
            Label titleLabel = new Label(title, skin, "panel-title");
            titleLabel.setFontScale(FontScale.LARGE);
            titleLabel.setColor(F1Colors.NEON_CYAN);

            Table header = new Table();
            header.setBackground(createClippedBackground());
            header.add(titleLabel).pad(Spacing.MD);

            add(header).growX().row();
        }

        pad(Spacing.LG);
        defaults().space(Spacing.MD);
    }

    private Drawable createClippedBackground() {
        // 좌상단 잘린 모서리 배경
        // (PolygonSpriteBatch 또는 ShapeRenderer 사용)
        return new BaseDrawable() {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                ShapeRenderer shapeRenderer = new ShapeRenderer();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(F1Colors.F1_RED);

                // 오각형 (좌상단 잘린 형태)
                float[] vertices = {
                        x + 20, y,                    // 좌하단
                        x + width, y,                 // 우하단
                        x + width, y + height,        // 우상단
                        x, y + height,                // 좌상단 (잘림 시작)
                        x, y + 20                     // 좌상단 (잘림 끝)
                };
                shapeRenderer.polygon(vertices);
                shapeRenderer.end();
            }
        };
    }
}
```

### 7.3 F1ProgressBar (진행 바)

```java
public class F1ProgressBar extends ProgressBar {
    private ParticleEffect trailEffect;

    public F1ProgressBar(float min, float max, float step, Skin skin) {
        super(min, max, step, false, skin, "f1-progress");

        // 그라데이션 배경
        setColor(F1Colors.CARBON_GRAY);

        // 진행 색상 (그라데이션: 초록 → 노랑 → 빨강)
        getStyle().knobBefore = createGradientDrawable();

        // 트레일 파티클
        trailEffect = new ParticleEffect();
        trailEffect.load(
                Gdx.files.internal("effects/progress_trail.p"),
                Gdx.files.internal("effects/")
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // 파티클 위치 업데이트
        float knobX = getX() + getWidth() * getPercent();
        float knobY = getY() + getHeight() / 2;
        trailEffect.setPosition(knobX, knobY);
        trailEffect.update(delta);
    }

    private Drawable createGradientDrawable() {
        // 수평 그라데이션 생성
        Pixmap pixmap = new Pixmap(256, 1, Pixmap.Format.RGBA8888);

        for (int i = 0; i < 256; i++) {
            float t = i / 255f;
            Color color;

            if (t < 0.5f) {
                // 초록 → 노랑
                color = new Color().set(F1Colors.SUCCESS_GREEN).lerp(F1Colors.WARNING_YELLOW, t * 2);
            } else {
                // 노랑 → 빨강
                color = new Color().set(F1Colors.WARNING_YELLOW).lerp(F1Colors.DANGER_RED, (t - 0.5f) * 2);
            }

            pixmap.drawPixel(i, 0, Color.rgba8888(color));
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
```

---

## 🎯 8. HUD Design (인게임 HUD 디자인)

### 8.1 HUD 레이아웃 (개선된 버전)

```
┌─────────────────────────────────────────────────┐
│ ╔═ LAP 2/5 ═╗           [MONACO]  ╔═ 1ST ═╗   │
│ ║  1:23.456  ║                     ║ +0.5s ║   │
│ ║★ 1:18.234  ║                     ╚═══════╝   │
│ ╚════════════╝                                  │
│                                      ┌─────┐    │
│                                      │ MAP │    │
│                                      └─────┘    │
│                                                 │
│          [── GAMEPLAY AREA ──]                  │
│                                                 │
│ ╔═ TIRES ════╗                  ╔═ SPEED ════╗│
│ ║ SOFT [🔴]  ║                  ║  285 km/h  ║│
│ ║ ██████░░░  ║                  ║ ▰▰▰▰▰▰▰▱▱ ║│ ← Shift Light
│ ║ Dur: ████  ║                  ║  Gear: 6   ║│
│ ╚═════════════╝                  ╚════════════╝│
└─────────────────────────────────────────────────┘
```

### 8.2 HUD 스타일 가이드

```java
public class HUDStyle {
    // 모든 HUD 요소는 동일한 디자인 언어 사용

    // 1. 테두리: 이중선 + 발광
    public static Drawable createHUDFrame() {
        // 외부 테두리 (두꺼운 선)
        // 내부 테두리 (얇은 네온 선)
        // 모서리: 대각선 잘림
    }

    // 2. 배경: 반투명 + 블러
    public static Drawable createHUDBackground() {
        // 80% 불투명 검정
        // 약간의 블러 효과 (배경과 구분)
    }

    // 3. 텍스트: 강한 대비
    public static LabelStyle createHUDTextStyle() {
        // 흰색 또는 네온 색상
        // 미세한 그림자/외곽선 (가독성)
    }

    // 4. 아이콘: 선명한 실루엣
    public static TextureRegion createHUDIcon(String name) {
        // 단색 아이콘 (빠른 인식)
        // 발광 효과 옵션
    }
}
```

---

## 🎨 9. Menu Design (메뉴 디자인)

### 9.1 메인 메뉴 개선안

```
┌────────────────────────────────────────────────┐
│                                                 │
│        ╔═══════════════════════════╗           │
│        ║    F1 RACING LEAGUE      ║           │
│        ╚═══════════════════════════╝           │
│                                                 │
│              ╱╲  3D LOGO  ╱╲                   │ ← 회전하는 3D 로고
│                                                 │
│       ┌──────────────────────────┐             │
│       │  ▶ SINGLE PLAYER         │             │
│       │  ▶ MULTIPLAYER           │             │
│       │  ▶ GARAGE                │             │
│       │  ▶ LEADERBOARD           │             │
│       │  ▶ SETTINGS              │             │
│       └──────────────────────────┘             │
│                                                 │
│  [Profile: Player1]    [News: 🏆 New Record!] │
└────────────────────────────────────────────────┘
```

### 9.2 메뉴 애니메이션

```java
public class MenuAnimations {
    // 메뉴 항목 순차 등장 (스태거)
    public static void staggerMenuItems(Array<Button> buttons) {
        for (int i = 0; i < buttons.size; i++) {
            Button button = buttons.get(i);
            
            // 초기 상태 (왼쪽 밖, 투명)
            button.setPosition(-button.getWidth(), button.getY());
            button.getColor().a = 0f;
            
            // 순차적으로 슬라이드 인 (0.1초 간격)
            button.addAction(Actions.sequence(
                Actions.delay(i * 0.1f),
                Actions.parallel(
                    Actions.moveTo(button.getX(), button.getY(), 0.4f, Interpolation.pow3Out),
                    Actions.fadeIn(0.3f)
                )
            ));
        }
    }
    
    // 배경 파티클 효과
    public static ParticleEffect createMenuBackground() {
        ParticleEffect particles = new ParticleEffect();
        particles.load(
            Gdx.files.internal("effects/menu_particles.p"),
            Gdx.files.internal("effects/")
        );
        
        // 설정: 느리게 떠다니는 기하학적 형태
        // 색상: F1_RED + NEON_CYAN
        // 움직임: 무작위 방향, 느린 속도
        
        return particles;
    }
    
    // 로고 회전 애니메이션
    public static void animateLogo(Image logo) {
        logo.addAction(Actions.forever(
            Actions.sequence(
                Actions.rotateBy(360f, 10f, Interpolation.linear)
            )
        ));
        
        // 펄스 효과 (호흡하듯이)
        logo.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleTo(1.1f, 1.1f, 2.0f, Interpolation.sine),
                Actions.scaleTo(1.0f, 1.0f, 2.0f, Interpolation.sine)
            )
        ));
    }
}
```

---

## 🎭 10. Visual Effects Library (시각 효과 라이브러리)

### 10.1 Speed Lines (속도선)

```java
public class SpeedLinesEffect {
    private ShapeRenderer shapeRenderer;
    private Array<SpeedLine> lines;
    
    private class SpeedLine {
        float x, y;
        float length;
        float speed;
        float alpha;
    }
    
    public SpeedLinesEffect() {
        shapeRenderer = new ShapeRenderer();
        lines = new Array<>();
        
        // 화면 가장자리에서 중앙으로 향하는 선 생성
        for (int i = 0; i < 30; i++) {
            SpeedLine line = new SpeedLine();
            line.x = MathUtils.random(0, Gdx.graphics.getWidth());
            line.y = MathUtils.random(0, Gdx.graphics.getHeight());
            line.length = MathUtils.random(50, 150);
            line.speed = MathUtils.random(500, 1500);
            line.alpha = MathUtils.random(0.3f, 0.8f);
            lines.add(line);
        }
    }
    
    public void render(float delta, float vehicleSpeed) {
        // 속도에 비례하여 선의 움직임 가속
        float speedMultiplier = vehicleSpeed / 100f; // 정규화
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (SpeedLine line : lines) {
            // 중앙에서 밖으로 이동
            Vector2 center = new Vector2(
                Gdx.graphics.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f
            );
            Vector2 direction = new Vector2(line.x, line.y).sub(center).nor();
            
            line.x += direction.x * line.speed * speedMultiplier * delta;
            line.y += direction.y * line.speed * speedMultiplier * delta;
            
            // 화면 밖으로 나가면 재생성
            if (line.x < 0 || line.x > Gdx.graphics.getWidth() ||
                line.y < 0 || line.y > Gdx.graphics.getHeight()) {
                line.x = center.x + MathUtils.random(-100, 100);
                line.y = center.y + MathUtils.random(-100, 100);
            }
            
            // 선 그리기 (그라데이션)
            shapeRenderer.setColor(1f, 1f, 1f, line.alpha);
            shapeRenderer.rectLine(
                line.x, line.y,
                line.x + direction.x * line.length,
                line.y + direction.y * line.length,
                2f
            );
        }
        
        shapeRenderer.end();
    }
}
```

### 10.2 Screen Shake (화면 흔들림)

```java
public class ScreenShake {
    private float intensity = 0f;
    private float duration = 0f;
    private float elapsed = 0f;
    
    public void shake(float intensity, float duration) {
        this.intensity = intensity;
        this.duration = duration;
        this.elapsed = 0f;
    }
    
    public Vector2 update(float delta, OrthographicCamera camera) {
        if (elapsed < duration) {
            elapsed += delta;
            
            // 감쇠 (시간에 따라 줄어듦)
            float currentIntensity = intensity * (1f - elapsed / duration);
            
            // 무작위 오프셋
            float offsetX = MathUtils.random(-currentIntensity, currentIntensity);
            float offsetY = MathUtils.random(-currentIntensity, currentIntensity);
            
            return new Vector2(offsetX, offsetY);
        }
        
        return Vector2.Zero;
    }
}

// 사용 예시: 충돌 시
if (collision) {
    screenShake.shake(10f, 0.3f); // 10 픽셀, 0.3초
}
```

### 10.3 Motion Blur (모션 블러)

```java
public class MotionBlur {
    private FrameBuffer fbo;
    private Array<Texture> frameHistory;
    private static final int HISTORY_SIZE = 5;
    
    public MotionBlur() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 
            Gdx.graphics.getWidth(), 
            Gdx.graphics.getHeight(), 
            false
        );
        frameHistory = new Array<>();
    }
    
    public void capture() {
        fbo.begin();
        // 현재 프레임 캡처
        fbo.end();
        
        frameHistory.add(fbo.getColorBufferTexture());
        
        if (frameHistory.size > HISTORY_SIZE) {
            frameHistory.removeIndex(0);
        }
    }
    
    public void render(SpriteBatch batch) {
        batch.begin();
        
        // 이전 프레임들을 반투명하게 오버레이
        for (int i = 0; i < frameHistory.size; i++) {
            float alpha = (i + 1f) / frameHistory.size * 0.3f;
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(frameHistory.get(i), 0, 0);
        }
        
        batch.setColor(Color.WHITE);
        batch.end();
    }
}
```

---

## 🎵 11. Audio-Visual Sync (오디오-비주얼 동기화)

### 11.1 음악 비트에 맞춘 UI 펄스

```java
public class MusicSyncUI {
    private Music backgroundMusic;
    private float bpm = 140f; // Beats Per Minute
    private float beatInterval;
    private float beatTimer = 0f;
    
    public MusicSyncUI(Music music, float bpm) {
        this.backgroundMusic = music;
        this.bpm = bpm;
        this.beatInterval = 60f / bpm; // 초 단위
    }
    
    public void update(float delta, Array<Actor> uiElements) {
        beatTimer += delta;
        
        if (beatTimer >= beatInterval) {
            beatTimer = 0f;
            onBeat(uiElements);
        }
    }
    
    private void onBeat(Array<Actor> uiElements) {
        // 모든 UI 요소에 미세한 펄스 효과
        for (Actor actor : uiElements) {
            actor.addAction(Actions.sequence(
                Actions.scaleTo(1.05f, 1.05f, 0.1f, Interpolation.pow2Out),
                Actions.scaleTo(1.0f, 1.0f, 0.1f, Interpolation.pow2In)
            ));
        }
        
        // 배경 플래시
        flashBackground(F1Colors.NEON_CYAN, 0.1f);
    }
    
    private void flashBackground(Color color, float intensity) {
        // 화면 전체에 색상 오버레이
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        
        Image flash = new Image(new Texture(pixmap));
        flash.setFillParent(true);
        flash.getColor().a = 0f;
        
        flash.addAction(Actions.sequence(
            Actions.fadeIn(0.05f),
            Actions.fadeOut(0.15f),
            Actions.removeActor()
        ));
        
        stage.addActor(flash);
        pixmap.dispose();
    }
}
```

### 11.2 사운드 이펙트 + 시각 피드백

```java
public class AudioVisualFeedback {
    public static void playWithFeedback(Sound sound, Actor target) {
        // 사운드 재생
        long soundId = sound.play(0.7f);
        
        // 동시에 시각 효과
        target.addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(1.2f, 1.2f, 0.1f, Interpolation.pow2Out),
                Actions.color(Color.WHITE, 0.1f)
            ),
            Actions.parallel(
                Actions.scaleTo(1.0f, 1.0f, 0.2f, Interpolation.elasticOut),
                Actions.color(Color.GRAY, 0.2f)
            )
        ));
        
        // 파티클 이펙트
        showImpactParticles(target.getX() + target.getWidth() / 2,
                           target.getY() + target.getHeight() / 2);
    }
    
    private static void showImpactParticles(float x, float y) {
        ParticleEffect effect = new ParticleEffect();
        effect.load(
            Gdx.files.internal("effects/ui_impact.p"),
            Gdx.files.internal("effects/")
        );
        effect.setPosition(x, y);
        effect.start();
    }
}
```

---

## 📱 12. Accessibility (접근성)

### 12.1 색맹 모드

```java
public class ColorBlindMode {
    public enum Type {
        NORMAL,
        PROTANOPIA,    // 적색맹
        DEUTERANOPIA,  // 녹색맹
        TRITANOPIA     // 청색맹
    }
    
    public static Color adjustColor(Color original, Type mode) {
        switch(mode) {
            case PROTANOPIA:
                // 빨강 → 노랑/갈색
                if (original.equals(F1Colors.F1_RED)) {
                    return new Color(1f, 0.6f, 0f, 1f); // 주황
                }
                break;
            case DEUTERANOPIA:
                // 초록 → 파랑
                if (original.equals(F1Colors.SUCCESS_GREEN)) {
                    return Color.CYAN;
                }
                break;
            case TRITANOPIA:
                // 파랑 → 빨강
                if (original.equals(Color.BLUE)) {
                    return Color.MAGENTA;
                }
                break;
        }
        return original;
    }
}
```

### 12.2 고대비 모드

```java
public class HighContrastMode {
    public static void apply(Stage stage) {
        // 모든 텍스트를 흰색으로
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Label) {
                ((Label) actor).setColor(Color.WHITE);
            }
        }
        
        // 배경을 완전 검정으로
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        
        // UI 요소에 두꺼운 테두리
        // ...
    }
}
```

### 12.3 폰트 크기 조정

```java
public class FontSizeSettings {
    public enum Size {
        SMALL(0.8f),
        NORMAL(1.0f),
        LARGE(1.25f),
        XLARGE(1.5f);
        
        public final float scale;
        Size(float s) { scale = s; }
    }
    
    public static void applyFontSize(Stage stage, Size size) {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Label) {
                Label label = (Label) actor;
                float currentScale = label.getFontScaleX();
                label.setFontScale(currentScale * size.scale);
            }
        }
    }
}
```

---

## 🎨 13. Asset Creation Guidelines (에셋 제작 가이드)

### 13.1 이미지 스펙

```
파일 형식: PNG (알파 채널 지원)
컬러 스페이스: sRGB
해상도 기준: 1920x1080 (1080p)

UI 요소 크기:
- 아이콘: 64x64 px (2x: 128x128)
- 버튼: 최소 200x60 px
- 패널: 가변 (9-patch 사용 권장)
- 로고: 512x256 px

최적화:
- TinyPNG로 압축 (30-50% 용량 감소)
- Texture Atlas 사용 (TexturePacker)
- Mipmap 생성 (축소 시 선명도)
```

### 13.2 파티클 이펙트 가이드

```
툴: libGDX Particle Editor

타이어 연기:
- 색상: 흰색 → 회색 (페이드 아웃)
- 크기: 10~30 픽셀
- 수명: 0.5~1.0초
- 발생: 연속 (10개/초)

충돌 스파크:
- 색상: 노랑 → 주황 → 빨강
- 크기: 5~15 픽셀
- 수명: 0.2~0.5초
- 발생: 버스트 (20개 한번에)

피트 스톱 먼지:
- 색상: 갈색/회색
- 크기: 15~40 픽셀
- 수명: 1.0~2.0초
- 발생: 연속 (5개/초)
```

### 13.3 사운드 스펙

```
파일 형식: OGG (압축률 우수)
샘플레이트: 44.1 kHz
비트레이트: 128-192 kbps
채널: Mono (효과음), Stereo (음악)

볼륨 가이드:
- UI 효과음: -20dB (조용함)
- 엔진 사운드: -10dB (중간)
- 충돌음: -5dB (큼)
- BGM: -15dB (배경)

길이:
- 효과음: < 2초
- BGM: 2-5분 (루프 가능)
```

---

## 🛠️ 14. Implementation Checklist (구현 체크리스트)

### Phase 2: 기본 UI (Week 3-4)

**HUD 요소**
- [ ] 속도계 (디지털 + 아날로그)
- [ ] Shift Light (7단계 LED)
- [ ] 기어 표시
- [ ] 랩 카운터 + 타이머
- [ ] 미니맵
- [ ] 타이어 게이지
- [ ] 내구도 바

**스타일링**
- [ ] F1 색상 팔레트 적용
- [ ] 커스텀 폰트 로드
- [ ] 기본 애니메이션 (호버, 클릭)
- [ ] 카본 파이버 텍스처

**사운드**
- [ ] 버튼 클릭 효과음
- [ ] Shift Light 비프음
- [ ] 메뉴 배경음악

### Phase 3: 고급 UI (Week 5-6)

**시각 효과**
- [ ] Speed Lines (고속 주행 시)
- [ ] Screen Shake (충돌 시)
- [ ] Glow Effect (LED 발광)
- [ ] 파티클 시스템 (타이어 연기, 스파크)

**애니메이션**
- [ ] 화면 전환 (슬라이드, 페이드)
- [ ] 팝업 등장 (바운스)
- [ ] 로고 회전 (메인 메뉴)
- [ ] 메뉴 항목 스태거

**컴포넌트**
- [ ] F1Button 클래스
- [ ] F1Panel 클래스
- [ ] F1ProgressBar 클래스
- [ ] 커스텀 다이얼로그

### Phase 5: 폴리싱 (Week 10)

**최적화**
- [ ] Texture Atlas 생성
- [ ] 9-patch 이미지 적용
- [ ] 불필요한 Draw Call 제거
- [ ] 파티클 이펙트 최적화

**접근성**
- [ ] 색맹 모드 (3종)
- [ ] 고대비 모드
- [ ] 폰트 크기 조정 (4단계)
- [ ] 키보드 네비게이션

**최종 점검**
- [ ] 모든 해상도 테스트 (720p, 1080p, 1440p)
- [ ] 애니메이션 부드러움 확인 (60 FPS)
- [ ] 사운드 볼륨 밸런스
- [ ] 색상 일관성 검토

---

## 📚 15. References & Inspiration (참고 자료)

### 15.1 F1 Official Assets
- F1 공식 브랜드 가이드라인
- F1 TV 앱 UI/UX
- F1 게임 시리즈 (Codemasters)

### 15.2 Design Systems
- Material Design 3 (애니메이션 원칙)
- Apple Human Interface Guidelines (접근성)
- Microsoft Fluent Design (깊이감)

### 15.3 Tools
- **디자인**: Figma, Adobe XD
- **이미지 편집**: Photoshop, GIMP
- **파티클**: libGDX Particle Editor
- **사운드**: Audacity, Bfxr
- **폰트**: Google Fonts, DaFont

### 15.4 Inspiration
- Forza Horizon 5 (레이싱 UI)
- Gran Turismo 7 (미니멀 HUD)
- Cyberpunk 2077 (네온 효과)
- F1 2023 (공식 게임)

---

## 🎬 16. Example Implementations (예제 코드)

### 16.1 완성된 HUD Manager

```java
public class ModernHUDManager {
    private Stage stage;
    private Skin skin;
    
    // Components
    private Speedometer speedometer;
    private ShiftLight shiftLight;
    private F1Panel tirePanel;
    private F1Panel statusPanel;
    private Minimap minimap;
    
    // Effects
    private SpeedLinesEffect speedLines;
    private ScreenShake screenShake;
    private ParticleEffectPool sparkPool;
    
    // Audio
    private Sound shiftBeep;
    private Sound impactSound;
    
    public ModernHUDManager(Viewport viewport, AssetManager assetManager) {
        stage = new Stage(viewport);
        skin = assetManager.get("ui/skins/f1/f1.json", Skin.class);
        
        createModernHUD();
        loadEffects(assetManager);
    }
    
    private void createModernHUD() {
        Table root = new Table();
        root.setFillParent(true);
        
        // Top Bar
        Table topBar = new Table();
        topBar.add(createLapDisplay()).left().expandX();
        topBar.add(createTrackLabel()).center();
        topBar.add(createPositionBadge()).right();
        topBar.add(minimap = new Minimap()).right().padLeft(20);
        
        // Bottom Bar
        Table bottomBar = new Table();
        
        // Left: Tire & Durability
        tirePanel = new F1Panel("TIRES", skin);
        tirePanel.add(createTireGauge()).row();
        tirePanel.add(createDurabilityBar()).padTop(10);
        
        // Right: Speed & Shift Light
        statusPanel = new F1Panel(null, skin);
        speedometer = new Speedometer(skin);
        shiftLight = new ShiftLight(assetManager);
        
        statusPanel.add(speedometer).row();
        statusPanel.add(shiftLight).padTop(10).row();
        statusPanel.add(createGearDisplay()).padTop(5);
        
        bottomBar.add(tirePanel).left().expandX();
        bottomBar.add(statusPanel).right();
        
        // Layout
        root.add(topBar).top().growX().pad(20).row();
        root.add().expand().row(); // Gameplay area
        root.add(bottomBar).bottom().growX().pad(20);
        
        stage.addActor(root);
    }
    
    public void update(float delta, VehicleController vehicle, RaceManager race) {
        // Update data
        float speedKmh = vehicle.getSpeed() * 3.6f;
        int gear = vehicle.getCurrentGear();
        
        speedometer.update(speedKmh);
        shiftLight.update(delta, speedKmh, gear);
        
        // Update effects
        if (speedKmh > 200f) {
            speedLines.render(delta, speedKmh);
        }
        
        // Screen shake on collision
        if (vehicle.hasCollided()) {
            screenShake.shake(15f, 0.4f);
            showCollisionSpark(vehicle.getPosition());
        }
        
        Vector2 shakeOffset = screenShake.update(delta, camera);
        camera.translate(shakeOffset.x, shakeOffset.y, 0);
        
        stage.act(delta);
    }
    
    public void render() {
        stage.draw();
    }
}
```

---

**Version**: 1.0.0  
**Status**: Living Document  
**Last Updated**: 2025-01-15  
**Priority**: Core Feature (All Phases)
