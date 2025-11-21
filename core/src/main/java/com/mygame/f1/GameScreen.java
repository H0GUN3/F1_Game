package com.mygame.f1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygame.f1.screens.MainMenuScreen;
import com.mygame.f1.ui.SkinFactory;
import com.mygame.f1.hud.TimeAttackHUD;
import com.mygame.f1.gameplay.race.TimeAttackManager;
import com.mygame.f1.data.TimeAttackRepository;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private final Main gameRef;

    // --- 재사용 변수 (성능 최적화) ---
    private final Vector2 v2_tmp1 = new Vector2();
    private final Vector2 v2_tmp2 = new Vector2();

    // --- 물리 시뮬레이션 ---
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Body playerCar;
    private float accumulator = 0;
    private static final float TIME_STEP = 1 / 60f;

    // --- 카메라 ---
    private OrthographicCamera camera;
    private Viewport viewport;
    private float cameraAngle = 0f;
    private float positionSmoothness = 5.0f;
    private float cameraRotationSmoothness = 3.0f;
    private float cameraOffsetFromCar = 0.8f;
    private Vector2 cameraTargetPosition = new Vector2();

    // --- 렌더링 ---
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private boolean mapManagedByAM = false;
    private SpriteBatch batch;
    private Texture carTexture;
    private Texture backgroundTexture;
    private boolean debugDraw = false; // 기본 OFF (F1로 토글)

    // --- Pause UI ---
    private boolean paused = false;
    private Stage pauseStage;
    private Skin pauseSkin;

    // --- Time Attack ---
    private TimeAttackManager timeAttack;
    private TimeAttackHUD hud;
    private Skin hudSkin;
    private TimeAttackRepository taRepo;
    private Body finishSensor;
    // --- 월드 크기(타일맵 기준 우선) ---
    private float worldWidthWorld = 0f;
    private float worldHeightWorld = 0f;
    // Map-based spawn/sensors (fallback to defaults if not provided)
    private float spawnX = -1f;
    private float spawnY = -1f;
    private float spawnAngleRad = 0f;
    private boolean finishFromMap = false;
    private boolean checkpointsFromMap = false;
    // --- 체크포인트 ---
    private java.util.List<Body> checkpointSensors;
    private int checkpointTotal = 3;
    private int nextCheckpoint = 0;

    // --- 물리 파라미터 ---
    private float maxForwardSpeed = 3.5f;
    private float maxReverseSpeed = 1.5f;
    private float forwardAcceleration = 2.5f;
    private float reverseAcceleration = 1.5f;
    private float turningPower = 10f;
    private float grip = 18.0f;
    private float minSpeedForTurn = 0.8f;
    private float maxSteeringAngle = 60f;
    private float initialAngle = 0f;
    private float currentAcceleration = 0f;
    private float accelerationSmoothness = 7f;
    private float currentTorque = 0f;
    private float torqueSmoothness = 15f;
    private float defaultLinearDamping = 2.0f;
    private float brakingLinearDamping = 5.0f;
    private float collisionDamping = 4.0f;

    // --- 충돌 감지 ---
    private boolean isColliding = false;
    private boolean wasColliding = false;
    private float collisionTimer = 0f;
    private float collisionDuration = 0.2f;

    // --- 상수 ---
    public static final float PPM = 100;
    private static final boolean USE_TILED_MAP = true;
    // 맵 오브젝트 기반 상호작용 사용 여부(맵 개발 완료 전까지 false)
    private static final boolean USE_MAP_OBJECTS = false;

    public GameScreen() { this(null); }

    public GameScreen(Main game) { this.gameRef = game; }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1600 / PPM, 900 / PPM, camera);
        camera.zoom = 0.5f;

        batch = new SpriteBatch();
        // AssetManager를 통해 에셋 로드
        carTexture = Main.assetManager.get("pitstop_car_3.png", Texture.class);
        backgroundTexture = Main.assetManager.get("Track_t2.png", Texture.class);
        // Time Attack init
        hudSkin = SkinFactory.createDefaultSkin();
        hud = new com.mygame.f1.hud.TimeAttackHUD(hudSkin);
        timeAttack = new com.mygame.f1.gameplay.race.TimeAttackManager();
        timeAttack.start();
        taRepo = new com.mygame.f1.data.TimeAttackRepository();

        world = new World(new Vector2(0, 0), true);
        box2DDebugRenderer = new Box2DDebugRenderer();

        // 충돌 리스너 설정
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();
                Body bodyA = a.getBody();
                Body bodyB = b.getBody();
                boolean sensorContact = a.isSensor() || b.isSensor();
                if ((bodyA == playerCar || bodyB == playerCar) && !sensorContact) {
                    isColliding = true;
                    collisionTimer = collisionDuration;
                    Vector2 velocity = playerCar.getLinearVelocity();
                    playerCar.setLinearVelocity(velocity.scl(0.4f));
                    playerCar.setAngularVelocity(playerCar.getAngularVelocity() * 0.3f);
                }
                boolean aFinish = "finish".equals(a.getUserData());
                boolean bFinish = "finish".equals(b.getUserData());
                if ((aFinish && bodyB == playerCar) || (bFinish && bodyA == playerCar)) {
                    float upwardVel = playerCar.getLinearVelocity().dot(0, 1);
                    if (upwardVel > 0.5f && timeAttack != null && nextCheckpoint >= checkpointTotal) {
                        long lapMs = timeAttack.onFinishCrossed();
                        if (lapMs >= 0) {
                            String user = (gameRef!=null && gameRef.playerName!=null)? gameRef.playerName : "Player";
                            if (taRepo != null) taRepo.updateBestIfImproved(user, "default", lapMs);
                            nextCheckpoint = 0;
                            if (hud != null) hud.setCheckpointProgress(nextCheckpoint, checkpointTotal);
                        }
                    }
                }

                // 체크포인트 처리(순서형)
                String aTag = a.getUserData() instanceof String ? (String) a.getUserData() : null;
                String bTag = b.getUserData() instanceof String ? (String) b.getUserData() : null;
                String tag = null;
                if (aTag != null && aTag.startsWith("cp-") && bodyB == playerCar) tag = aTag;
                else if (bTag != null && bTag.startsWith("cp-") && bodyA == playerCar) tag = bTag;
                if (tag != null) {
                    try {
                        int idx = Integer.parseInt(tag.substring(3));
                        if (idx == nextCheckpoint && nextCheckpoint < checkpointTotal) {
                            nextCheckpoint++;
                            if (hud != null) hud.setCheckpointProgress(nextCheckpoint, checkpointTotal);
                        }
                    } catch (Exception ignored) {}
                }
            }

            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        if (USE_TILED_MAP) {
            try {
                String path = null;
                if (Gdx.files.internal("maps/new_map.tmx").exists()) path = "maps/new_map.tmx";
                else if (Gdx.files.internal("maps/track1.tmx").exists()) path = "maps/track1.tmx";
                else if (Gdx.files.internal("maps/track.tmx").exists()) path = "maps/track.tmx";
                else if (Gdx.files.internal("track.tmx").exists()) path = "track.tmx";
                if (path != null) {
                    boolean loadedByAM = Main.assetManager.isLoaded(path, TiledMap.class);
                    if (loadedByAM) {
                        map = Main.assetManager.get(path, TiledMap.class);
                        mapManagedByAM = true;
                    } else {
                        map = new TmxMapLoader().load(path);
                        mapManagedByAM = false;
                    }
                    mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / PPM);
                    // 타일맵 규격으로 월드 크기 계산
                    com.badlogic.gdx.maps.MapProperties props = map.getProperties();
                    Integer mw = props.get("width", Integer.class);
                    Integer mh = props.get("height", Integer.class);
                    Integer tw = props.get("tilewidth", Integer.class);
                    Integer th = props.get("tileheight", Integer.class);
                    if (mw != null && mh != null && tw != null && th != null) {
                        worldWidthWorld = (mw * tw) / PPM;
                        worldHeightWorld = (mh * th) / PPM;
                    }
                    // Read optional objects from map (spawn/finish/checkpoints)
                    if (USE_MAP_OBJECTS) readObjectsFromMap();
                }
            } catch (Exception ignored) {
                map = null; mapRenderer = null;
            }
            // 현재 타일맵은 배경 전용이므로 Collision 레이어를 사용하지 않음
        }

        // 타일맵이 없거나 규격을 못 읽은 경우, 뷰포트 크기를 월드 크기로 사용
        if (worldWidthWorld <= 0f || worldHeightWorld <= 0f) {
            worldWidthWorld = viewport.getWorldWidth();
            worldHeightWorld = viewport.getWorldHeight();
        }

        // Default spawn to map center when not provided by map
        if (spawnX < 0f || spawnY < 0f) {
            spawnX = worldWidthWorld * 0.5f;
            spawnY = worldHeightWorld * 0.5f;
        }
        createPlayerCar();
        createScreenBoundaryWalls();

        // Create a horizontal finish-line sensor near the top of the world.
        // Cross upward (positive Y) to count laps (matches dot(forward,(0,1)) > 0.25 check).
        float worldW = worldWidthWorld;
        float worldH = worldHeightWorld;
        if (!finishFromMap) {
        float finishWidth = worldW * 0.8f;
        float finishX = (worldW - finishWidth) / 2f;
        float finishY = worldH * 0.8f;
        if (!finishFromMap) {
            createFinishSensor(finishX, finishY, finishWidth, 0.05f);
        }
        }

        // 기본 체크포인트 센서(화면 기준) 생성 및 HUD 초기화
        if (!checkpointsFromMap) {
        if (!checkpointsFromMap) {
            createCheckpointSensors(worldW, worldH);
        }
        }
        if (hud != null) hud.setCheckpointProgress(nextCheckpoint, checkpointTotal);

        initialAngle = playerCar.getAngle();

        // pause UI init
        pauseSkin = SkinFactory.createDefaultSkin();
        pauseStage = new Stage(new ScreenViewport());
    }

    private void createFinishSensor(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2f, y + height / 2f);
        finishSensor = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        Fixture fx = finishSensor.createFixture(fixtureDef);
        fx.setUserData("finish");
        shape.dispose();
    }

    private void readObjectsFromMap() {
        if (map == null) return;
        // Collision layer: rectangles only for now
        com.badlogic.gdx.maps.MapLayer coll = map.getLayers().get("Collision");
        if (coll != null) {
            for (com.badlogic.gdx.maps.MapObject obj : coll.getObjects()) {
                if (obj instanceof com.badlogic.gdx.maps.objects.RectangleMapObject) {
                    com.badlogic.gdx.math.Rectangle r = ((com.badlogic.gdx.maps.objects.RectangleMapObject) obj).getRectangle();
                    createWall(r.x / PPM, r.y / PPM, r.width / PPM, r.height / PPM);
                } else if (obj instanceof com.badlogic.gdx.maps.objects.PolygonMapObject) {
                    // Create a static chain from polygon for collision
                    com.badlogic.gdx.maps.objects.PolygonMapObject pobj = (com.badlogic.gdx.maps.objects.PolygonMapObject) obj;
                    float[] vs = pobj.getPolygon().getTransformedVertices(); // pixels
                    if (vs != null && vs.length >= 6) { // at least 3 points
                        float[] verts = new float[vs.length];
                        for (int i = 0; i < vs.length; i += 2) {
                            verts[i] = vs[i] / PPM;
                            verts[i + 1] = vs[i + 1] / PPM;
                        }
                        BodyDef bd = new BodyDef();
                        bd.type = BodyDef.BodyType.StaticBody;
                        Body body = world.createBody(bd);
                        ChainShape chain = new ChainShape();
                        chain.createLoop(verts);
                        FixtureDef fd = new FixtureDef();
                        fd.shape = chain;
                        fd.friction = 0.3f;
                        fd.restitution = 0.02f;
                        body.createFixture(fd);
                        chain.dispose();
                    }
                }
            }
        }
        // Objects layer: spawn/finish/cp-*
        com.badlogic.gdx.maps.MapLayer objects = map.getLayers().get("Objects");
        if (objects != null) {
            for (com.badlogic.gdx.maps.MapObject obj : objects.getObjects()) {
                String name = obj.getName();
                String tag = null;
                if (obj.getProperties().containsKey("tag")) tag = String.valueOf(obj.getProperties().get("tag"));
                String key = (tag != null && !tag.isEmpty()) ? tag : name;
                if (key == null || key.isEmpty()) continue;

                // Support rectangle and polygon objects
                if (obj instanceof com.badlogic.gdx.maps.objects.RectangleMapObject) {
                    com.badlogic.gdx.maps.objects.RectangleMapObject robj = (com.badlogic.gdx.maps.objects.RectangleMapObject) obj;
                    com.badlogic.gdx.math.Rectangle r = robj.getRectangle();
                    float x = r.x / PPM;
                    float y = r.y / PPM;
                    float w = r.width / PPM;
                    float h = r.height / PPM;

                    if (isSpawnKey(key)) {
                        spawnX = x + w / 2f;
                        spawnY = y + h / 2f;
                        float rotDeg = 0f;
                        if (robj.getProperties().containsKey("rotation")) {
                            try { rotDeg = Float.parseFloat(String.valueOf(robj.getProperties().get("rotation"))); } catch (Exception ignored) {}
                        }
                        spawnAngleRad = rotDeg * MathUtils.degreesToRadians;
                    } else if ("finish".equalsIgnoreCase(key)) {
                        createFinishSensor(x, y, w, h);
                        finishFromMap = true;
                    } else if (key.startsWith("cp-")) {
                        try {
                            int idx = Integer.parseInt(key.substring(3));
                            ensureCheckpointCapacity(idx + 1);
                            BodyDef bd = new BodyDef();
                            bd.type = BodyDef.BodyType.StaticBody;
                            bd.position.set(x + w / 2f, y + h / 2f);
                            Body body = world.createBody(bd);
                            PolygonShape shape = new PolygonShape();
                            shape.setAsBox(w / 2f, h / 2f);
                            FixtureDef fd = new FixtureDef();
                            fd.shape = shape;
                            fd.isSensor = true;
                            Fixture fx = body.createFixture(fd);
                            fx.setUserData("cp-" + idx);
                            shape.dispose();
                            checkpointsFromMap = true;
                        } catch (Exception ignored) {}
                    }
                } else if (obj instanceof com.badlogic.gdx.maps.objects.PolygonMapObject) {
                    com.badlogic.gdx.maps.objects.PolygonMapObject pobj = (com.badlogic.gdx.maps.objects.PolygonMapObject) obj;
                    float[] vs = pobj.getPolygon().getTransformedVertices();
                    if (vs != null && vs.length >= 6) {
                        // compute AABB in pixels
                        float minX = vs[0], maxX = vs[0], minY = vs[1], maxY = vs[1];
                        for (int i = 2; i < vs.length; i += 2) {
                            if (vs[i] < minX) minX = vs[i];
                            if (vs[i] > maxX) maxX = vs[i];
                            if (vs[i + 1] < minY) minY = vs[i + 1];
                            if (vs[i + 1] > maxY) maxY = vs[i + 1];
                        }
                        float x = minX / PPM;
                        float y = minY / PPM;
                        float w = (maxX - minX) / PPM;
                        float h = (maxY - minY) / PPM;

                        if (isSpawnKey(key)) {
                            spawnX = x + w / 2f;
                            spawnY = y + h / 2f;
                            float rotDeg = 0f;
                            if (pobj.getProperties().containsKey("rotation")) {
                                try { rotDeg = Float.parseFloat(String.valueOf(pobj.getProperties().get("rotation"))); } catch (Exception ignored) {}
                            }
                            spawnAngleRad = rotDeg * MathUtils.degreesToRadians;
                        } else if ("finish".equalsIgnoreCase(key)) {
                            createFinishSensor(x, y, w, h);
                            finishFromMap = true;
                        } else if (key.startsWith("cp-")) {
                            try {
                                int idx = Integer.parseInt(key.substring(3));
                                ensureCheckpointCapacity(idx + 1);
                                BodyDef bd = new BodyDef();
                                bd.type = BodyDef.BodyType.StaticBody;
                                bd.position.set(x + w / 2f, y + h / 2f);
                                Body body = world.createBody(bd);
                                PolygonShape shape = new PolygonShape();
                                shape.setAsBox(w / 2f, h / 2f);
                                FixtureDef fd = new FixtureDef();
                                fd.shape = shape;
                                fd.isSensor = true;
                                Fixture fx = body.createFixture(fd);
                                fx.setUserData("cp-" + idx);
                                shape.dispose();
                                checkpointsFromMap = true;
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        }
    }

    private void ensureCheckpointCapacity(int total) {
        if (total > checkpointTotal) {
            checkpointTotal = total;
        }
    }

    private boolean isSpawnKey(String key) {
        if (key == null) return false;
        String k = key.toLowerCase();
        return k.equals("spawn") || k.equals("playerspawn") || k.equals("car_1") || k.equals("car1");
    }

    private void createCheckpointSensors(float worldW, float worldH) {
        checkpointSensors = new java.util.ArrayList<>();
        // 3개의 수평 센서를 서로 다른 Y 위치에 배치 (타일맵 도입 전 간이 체크포인트)
        float[] ys = new float[]{ worldH * 0.25f, worldH * 0.50f, worldH * 0.65f };
        float width = worldW * 0.6f;
        float x = (worldW - width) / 2f;
        float height = 0.05f;
        for (int i = 0; i < checkpointTotal; i++) {
            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            bd.position.set(x + width / 2f, ys[i] + height / 2f);
            Body body = world.createBody(bd);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2f, height / 2f);

            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.isSensor = true;

            Fixture fx = body.createFixture(fd);
            fx.setUserData("cp-" + i);
            shape.dispose();
            checkpointSensors.add(body);
        }
    }

    private void createWall(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2, y + height / 2);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.02f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    private void createScreenBoundaryWalls() {
        float worldWidth = worldWidthWorld;
        float worldHeight = worldHeightWorld;
        float wallThickness = 0.1f;

        createWall(0, worldHeight - wallThickness, worldWidth, wallThickness);
        createWall(0, 0, worldWidth, wallThickness);
        createWall(0, 0, wallThickness, worldHeight);
        createWall(worldWidth - wallThickness, 0, wallThickness, worldHeight);
    }

    private void createPlayerCar() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(spawnX, spawnY);
        bodyDef.linearDamping = defaultLinearDamping;
        bodyDef.angularDamping = 20.0f;
        bodyDef.angle = spawnAngleRad;
        playerCar = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape carShape = new PolygonShape();
        carShape.setAsBox(6.40f / PPM, 12.80f / PPM);
        fixtureDef.shape = carShape;
        fixtureDef.density = 3.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.02f;

        playerCar.createFixture(fixtureDef);
        carShape.dispose();
    }

    public void update(float delta) {
        // Apply input/forces before stepping
        handleInput(delta);
        updateSteering(delta);
        updateFriction();
        limitSpeed();
        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 8, 3);
            accumulator -= TIME_STEP;
        }

        if (collisionTimer > 0) {
            collisionTimer -= delta;
            if (collisionTimer <= 0) {
                wasColliding = isColliding;
                isColliding = false;
            }
        }



        if (isColliding) {
            playerCar.setLinearDamping(collisionDamping);
            if (playerCar.getLinearVelocity().len() < 0.5f) {
                Vector2 forwardDirection = playerCar.getWorldVector(new Vector2(0, -0.3f));
                playerCar.applyLinearImpulse(forwardDirection, playerCar.getWorldCenter(), true);
            }
        }

        Vector2 forwardVector = new Vector2(0, cameraOffsetFromCar);
        Vector2 worldSpaceOffset = playerCar.getWorldVector(forwardVector);
        Vector2 targetPosition = new Vector2(playerCar.getPosition()).add(worldSpaceOffset);

        camera.position.x = MathUtils.lerp(camera.position.x, targetPosition.x, positionSmoothness * delta);
        camera.position.y = MathUtils.lerp(camera.position.y, targetPosition.y, positionSmoothness * delta);

        float targetAngle = -playerCar.getAngle() * MathUtils.radiansToDegrees;
        cameraAngle = MathUtils.lerpAngleDeg(cameraAngle, targetAngle, cameraRotationSmoothness * delta);

        camera.up.set(0, 1, 0);
        camera.direction.set(0, 0, -1);
        camera.rotate(cameraAngle);
        camera.update();

        if (USE_TILED_MAP && mapRenderer != null) {
            mapRenderer.setView(camera);
        }

        // Update HUD values
        if (hud != null && playerCar != null) {
            float speedMs = playerCar.getLinearVelocity().len();
            hud.setSpeed(speedMs);
            if (timeAttack != null) {
                hud.setLap(timeAttack.getLapCount());
                hud.setTimes(timeAttack.getCurrentLapMs(), timeAttack.getBestLapMs());
                hud.setCheckpointProgress(nextCheckpoint, checkpointTotal);
            }
        }
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            debugDraw = !debugDraw;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            playerCar.setLinearDamping(brakingLinearDamping);
        } else if (!isColliding) {
            playerCar.setLinearDamping(defaultLinearDamping);
        }

        Vector2 forwardNormal = playerCar.getWorldVector(new Vector2(0, 1));
        float forwardSpeed = playerCar.getLinearVelocity().dot(forwardNormal);
        float currentSpeed = playerCar.getLinearVelocity().len();

        float targetAcceleration = 0;
        boolean movingForward = false;
        boolean movingReverse = false;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            targetAcceleration = forwardAcceleration;
            movingForward = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            targetAcceleration = -reverseAcceleration;
            movingReverse = true;
        }

        currentAcceleration = MathUtils.lerp(currentAcceleration, targetAcceleration, accelerationSmoothness * delta);

        if (Math.abs(currentAcceleration) > 0.1f) {
            Vector2 forceVector = playerCar.getWorldVector(new Vector2(0, currentAcceleration));
            playerCar.applyForceToCenter(forceVector, true);
        }

        if (currentSpeed >= minSpeedForTurn && (movingForward || movingReverse)) {
            if (movingForward && forwardSpeed > 0.5f) {
                // speedFactor logic was here, seems incomplete in original, handled in updateSteering
            } else if (movingReverse && forwardSpeed < -0.5f) {
                // speedFactor logic was here, seems incomplete in original, handled in updateSteering
            }
        }
    }

    private void updateSteering(float delta) {
        float targetAngularVelocity = 0;

        Vector2 forwardNormal = playerCar.getWorldVector(new Vector2(0, 1));
        float forwardSpeed = playerCar.getLinearVelocity().dot(forwardNormal);
        float speedAbs = Math.abs(forwardSpeed);
        float speedRatio = MathUtils.clamp(speedAbs / Math.max(0.0001f, maxForwardSpeed), 0f, 1f);
        float steerScale = MathUtils.lerp(1.0f, 0.35f, speedRatio); // 고속일수록 선회량 축소
        float maxAngularVelocity = MathUtils.degreesToRadians * 190 * steerScale;
        boolean movingForward = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean movingReverse = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            targetAngularVelocity = (movingReverse && forwardSpeed < -0.5f) ? -maxAngularVelocity : maxAngularVelocity;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            targetAngularVelocity = (movingReverse && forwardSpeed < -0.5f) ? maxAngularVelocity : -maxAngularVelocity;
        }

        float currentAngularVelocity = playerCar.getAngularVelocity();
        float velocityChange = targetAngularVelocity - currentAngularVelocity;
        float impulse = playerCar.getInertia() * velocityChange;
        playerCar.applyAngularImpulse(impulse, true);
    }

    private void updateFriction() {
        boolean isSteering = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D);
        float gripFactor = isSteering ? 0.9f : 1.0f;

        Vector2 lateralVelocity = getLateralVelocity();
        v2_tmp1.set(lateralVelocity).scl(-playerCar.getMass() * grip * gripFactor);
        playerCar.applyForceToCenter(v2_tmp1, true);

        Vector2 forwardVelocity = getForwardVelocity();
        float forwardSpeed = forwardVelocity.len();
        if (forwardSpeed > 0.1f) {
            float dragCoefficient = 0.05f * forwardSpeed;
            v2_tmp1.set(forwardVelocity).scl(-dragCoefficient * playerCar.getMass());
            playerCar.applyForceToCenter(v2_tmp1, true);
        }
    }

    private void limitSpeed() {
        Vector2 forwardNormal = playerCar.getWorldVector(new Vector2(0, 1));
        float forwardSpeed = playerCar.getLinearVelocity().dot(forwardNormal);
        float speed = playerCar.getLinearVelocity().len();

        if (forwardSpeed > 0 && speed > maxForwardSpeed) {
            playerCar.setLinearVelocity(playerCar.getLinearVelocity().scl(maxForwardSpeed / speed));
        } else if (forwardSpeed < 0 && speed > maxReverseSpeed) {
            playerCar.setLinearVelocity(playerCar.getLinearVelocity().scl(maxReverseSpeed / speed));
        }
    }

    private Vector2 getLateralVelocity() {
        v2_tmp1.set(playerCar.getWorldVector(new Vector2(1, 0)));
        float rightSpeed = playerCar.getLinearVelocity().dot(v2_tmp1);
        return v2_tmp1.scl(rightSpeed);
    }

    private Vector2 getForwardVelocity() {
        v2_tmp2.set(playerCar.getWorldVector(new Vector2(0, 1)));
        float forwardSpeed = playerCar.getLinearVelocity().dot(v2_tmp2);
        return v2_tmp2.scl(forwardSpeed);
    }

    @Override
    public void render(float delta) {
        if (paused && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }
        if (!paused) {
            update(delta);
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (USE_TILED_MAP && mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (!USE_TILED_MAP) {
            float bgWidth = viewport.getWorldWidth();
            float bgHeight = viewport.getWorldHeight();
            batch.draw(backgroundTexture, 0, 0, bgWidth, bgHeight);
        }

        Vector2 carPos = playerCar.getPosition();
        float carWidth = 12.80f / PPM;
        float carHeight = 25.60f / PPM;
        batch.draw(carTexture,
            carPos.x - carWidth / 2, carPos.y - carHeight / 2,
            carWidth / 2, carHeight / 2,
            carWidth, carHeight,
            1, 1,
            playerCar.getAngle() * MathUtils.radiansToDegrees,
            0, 0, carTexture.getWidth(), carTexture.getHeight(),
            false, false);

        batch.end();

        if (debugDraw) {
            box2DDebugRenderer.render(world, camera.combined);
        }

        // Draw HUD on top of world
        if (hud != null) {
            hud.getStage().act(Gdx.graphics.getDeltaTime());
            hud.getStage().draw();
        }

        if (paused) {
            drawPauseOverlay();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (pauseStage != null) pauseStage.getViewport().update(width, height, true);
        if (hud != null) hud.resize(width, height);
    }

    @Override
    public void dispose() {
        world.dispose();
        box2DDebugRenderer.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null && !mapManagedByAM) map.dispose();
        batch.dispose();
        if (pauseStage != null) pauseStage.dispose();
        if (pauseSkin != null) pauseSkin.dispose();
        if (hud != null) hud.dispose();
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            Gdx.input.setInputProcessor(pauseStage);
            buildPauseUI();
            if (timeAttack != null) timeAttack.pause();
        } else {
            Gdx.input.setInputProcessor(null);
            if (pauseStage != null) pauseStage.clear();
            if (timeAttack != null) timeAttack.resume();
        }
    }

    private void buildPauseUI() {
        pauseStage.clear();
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(pauseSkin.getDrawable("bg"));
        pauseStage.addActor(root);

        Table panel = new Table();
        panel.setBackground(pauseSkin.getDrawable("panel"));
        panel.defaults().pad(8).width(300).height(44);

        Label title = new Label("Pause", pauseSkin, "title");
        TextButton resume = new TextButton("Resume", pauseSkin);
        TextButton mainMenu = new TextButton("Main Menu", pauseSkin);
        TextButton exit = new TextButton("Exit", pauseSkin);

        resume.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y){ togglePause(); }
        });
        mainMenu.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y){
                if (gameRef != null) gameRef.setScreen(new MainMenuScreen(gameRef)); else Gdx.app.exit();
            }
        });
        exit.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y){ Gdx.app.exit(); }
        });

        panel.add(title).row();
        panel.add(resume).row();
        panel.add(mainMenu).row();
        panel.add(exit).row();
        root.add(panel).center();
    }

    private void drawPauseOverlay() {
        if (pauseStage != null) {
            pauseStage.act(Gdx.graphics.getDeltaTime());
            pauseStage.draw();
        }
    }

    @Override
    public void hide() {}

    @Override
    public void pause() { if (timeAttack != null) timeAttack.pause(); }

    @Override
    public void resume() { if (timeAttack != null && !paused) timeAttack.resume(); }
}
