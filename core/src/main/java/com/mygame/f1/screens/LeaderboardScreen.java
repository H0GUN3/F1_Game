package com.mygame.f1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygame.f1.Main;
import com.mygame.f1.data.TimeAttackRepository;
import com.mygame.f1.ui.SkinFactory;

import java.util.List;

public class LeaderboardScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private final String trackId;
    private TimeAttackRepository repo;

    public LeaderboardScreen(Main game) {
        this(game, "default");
    }

    public LeaderboardScreen(Main game, String trackId) {
        this.game = game;
        this.trackId = trackId;
    }

    @Override
    public void show() {
        skin = SkinFactory.createDefaultSkin();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        repo = new TimeAttackRepository();

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(skin.getDrawable("bg"));
        stage.addActor(root);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("panel"));
        panel.defaults().pad(8);

        Label title = new Label("Leaderboard - " + trackId, skin, "title");
        title.setAlignment(Align.center);
        panel.add(title).colspan(3).growX().row();

        // Header
        panel.add(new Label("#", skin)).left();
        panel.add(new Label("User", skin)).left();
        panel.add(new Label("Best Lap", skin)).right().row();

        List<TimeAttackRepository.Entry> top = repo.getTopN(trackId, 10);
        if (top.isEmpty()) {
            panel.add(new Label("(no records)", skin)).colspan(3).padTop(6).left().row();
        } else {
            int rank = 1;
            for (TimeAttackRepository.Entry e : top) {
                panel.add(new Label(String.valueOf(rank), skin)).left();
                panel.add(new Label(e.username, skin)).left();
                panel.add(new Label(fmt(e.bestTimeMs), skin)).right().row();
                rank++;
            }
        }

        TextButton back = new TextButton("Back", skin);
        back.addListener(new ClickListener(){ @Override public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y){ game.setScreen(new MainMenuScreen(game)); }});

        root.add(panel).center().width(600).row();
        root.add(back).padTop(12);
    }

    private static String fmt(long ms) {
        long min = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(ms);
        long sec = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(ms) % 60;
        long msec = ms % 1000;
        return String.format("%02d:%02d.%03d", min, sec, msec);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { game.setScreen(new MainMenuScreen(game)); return; }
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { if (stage!=null) stage.getViewport().update(width,height,true);} 
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { if (stage!=null) stage.dispose(); if (skin!=null) skin.dispose(); }
}

