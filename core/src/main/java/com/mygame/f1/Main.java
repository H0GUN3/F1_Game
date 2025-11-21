package com.mygame.f1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.mygame.f1.screens.SplashScreen;
import com.mygame.f1.util.ClientBase; // 1. ClientBase 클래스를 임포트합니다.

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    // 게임 전체에서 유일하게 사용될 static final AssetManager
    public static final AssetManager assetManager = new AssetManager();
    public static String mapPath = null;
    public String playerName = "Player";

    // 2. 게임 전체에서 사용할 ClientBase 인스턴스를 멤버 변수로 선언
    public static ClientBase client;

    @Override
    public void create() {
        // 에셋 로딩
        assetManager.load("pitstop_car_3.png", Texture.class);
        assetManager.load("new_map2.png", Texture.class);
        assetManager.load("sukit.png", Texture.class);
        assetManager.load("Hockenheim_fix.png", Texture.class);
        assetManager.load("track_3.png", Texture.class);
        assetManager.load("x_track.png", Texture.class);
        assetManager.load("track_grey.png", Texture.class);
        assetManager.load("Track_t2.png", Texture.class);

        assetManager.finishLoading(); // 모든 에셋 로딩이 끝날 때까지 대기


        // ▼▼▼ 3. 요청하신 클라이언트 생성 및 연결 테스트 ▼▼▼
        System.out.println("--- 클라이언트 연결 테스트 시작 ---");

        // ClientBase 인스턴스 생성
        client = new ClientBase();

        // 서버에 연결 (이전 예제에서 사용한 포트 12345 기준)
        // TODO: "localhost"와 12345는 실제 서버 주소와 포트로 변경해야 합니다.
        if (Boolean.parseBoolean(System.getenv().getOrDefault("CLIENT_NET_ENABLED","false")) || Boolean.getBoolean("client.net")) { client.connect("localhost", 12345); }

        // "Hello" 테스트 메시지 전송
        if (Boolean.parseBoolean(System.getenv().getOrDefault("CLIENT_NET_ENABLED","false")) || Boolean.getBoolean("client.net")) { client.sendTest(); }

        System.out.println("--- 클라이언트 연결 테스트 완료 ---");
        // ▲▲▲ 테스트 완료 ▲▲▲


        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose() {
        // 4. (중요) 게임이 종료될 때 서버 연결을 안전하게 해제합니다.
        if (client != null) {
            client.disconnect();
        }

        assetManager.dispose();
        super.dispose();
    }
}
