package com.mygame.f1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    // 게임 전체에서 유일하게 사용될 static final AssetManager
    public static final AssetManager assetManager = new AssetManager();

    @Override
    public void create() {
        // 에셋 로딩
        assetManager.load("pitstop_car_3.png", Texture.class);
        assetManager.load("new_map2.png", Texture.class);
        assetManager.finishLoading(); // 모든 에셋 로딩이 끝날 때까지 대기

        setScreen(new GameScreen()); // GameScreen에 더 이상 Main 인스턴스를 전달하지 않음
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        super.dispose();
    }
}
