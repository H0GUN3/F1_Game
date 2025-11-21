package com.mygame.f1.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.concurrent.TimeUnit;

public class TimeAttackHUD {
    private final Stage stage;
    private final Skin skin;
    private final Label speedLabel;
    private final Label lapLabel;
    private final Label timeLabel;
    private final Label checkpointLabel;

    public TimeAttackHUD(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());

        Table root = new Table();
        root.setFillParent(true);
        root.pad(8);
        stage.addActor(root);

        Table topLeft = new Table();
        topLeft.defaults().pad(4);
        speedLabel = new Label("SPD: 0 km/h", skin);
        lapLabel = new Label("LAP: 0", skin);
        timeLabel = new Label("CUR: 00:00.000  BEST: --", skin);
        checkpointLabel = new Label("CP: 0/0", skin);
        topLeft.add(speedLabel).left().row();
        topLeft.add(lapLabel).left().row();
        topLeft.add(timeLabel).left().row();
        topLeft.add(checkpointLabel).left().row();

        root.top().left();
        root.add(topLeft).top().left();
    }

    public Stage getStage() { return stage; }

    public void resize(int w, int h) { stage.getViewport().update(w, h, true); }

    public void dispose() { stage.dispose(); }

    public void setSpeed(float metersPerSecond) {
        int kmh = Math.round(metersPerSecond * 3.6f);
        speedLabel.setText("SPD: " + kmh + " km/h");
    }

    public void setLap(int lap) {
        lapLabel.setText("LAP: " + lap);
    }

    public void setTimes(long currentMs, long bestMs) {
        timeLabel.setText("CUR: " + fmt(currentMs) + "  BEST: " + (bestMs<0?"--":fmt(bestMs)));
    }

    public void setCheckpointProgress(int current, int total) {
        checkpointLabel.setText("CP: " + current + "/" + total);
    }

    private static String fmt(long ms) {
        long min = TimeUnit.MILLISECONDS.toMinutes(ms);
        long sec = TimeUnit.MILLISECONDS.toSeconds(ms) % 60;
        long msec = ms % 1000;
        return String.format("%02d:%02d.%03d", min, sec, msec);
    }
}
