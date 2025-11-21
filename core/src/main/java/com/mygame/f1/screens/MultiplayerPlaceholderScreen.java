package com.mygame.f1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygame.f1.Main;
import com.mygame.f1.network.LobbyClient;
import com.mygame.f1.shared.Packets;
import com.mygame.f1.ui.SkinFactory;

public class MultiplayerPlaceholderScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;

    // UI fields
    private TextField hostField, tcpField, udpField, userField, roomIdField;
    private Label statusLabel, roomStateLabel, countdownLabel;
    private Packets.RoomState lastState;

    // Net
    private LobbyClient lobby;

    public MultiplayerPlaceholderScreen(Main game) { this.game = game; }

    @Override
    public void show() {
        skin = SkinFactory.createDefaultSkin();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(skin.getDrawable("bg"));
        stage.addActor(root);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("panel"));
        panel.defaults().pad(6).left();

        Label title = new Label("Multiplayer", skin, "title");
        title.setAlignment(Align.center);
        panel.add(title).colspan(2).growX().row();

        hostField = new TextField("localhost", skin);
        tcpField = new TextField("54555", skin);
        udpField = new TextField("54777", skin);
        userField = new TextField(game!=null? game.playerName : "Player", skin);
        roomIdField = new TextField("", skin);

        // Load last settings (optional)
        try {
            com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("mp_settings");
            String ph = prefs.getString("host", hostField.getText());
            int pt = prefs.getInteger("tcp", parseIntSafe(tcpField.getText(), 54555));
            int pu = prefs.getInteger("udp", parseIntSafe(udpField.getText(), 54777));
            String puName = prefs.getString("user", userField.getText());
            hostField.setText(ph);
            tcpField.setText(String.valueOf(pt));
            udpField.setText(String.valueOf(pu));
            userField.setText(puName);
        } catch (Exception ignored) {}

        panel.add(new Label("Host", skin)).width(120); panel.add(hostField).width(320).row();
        panel.add(new Label("TCP", skin)).width(120); panel.add(tcpField).width(120).row();
        panel.add(new Label("UDP", skin)).width(120); panel.add(udpField).width(120).row();
        panel.add(new Label("User", skin)).width(120); panel.add(userField).width(240).row();
        panel.add(new Label("Room ID", skin)).width(120); panel.add(roomIdField).width(240).row();

        TextButton hostBtn = new TextButton("Host", skin);
        TextButton joinBtn = new TextButton("Join", skin);
        TextButton startRace = new TextButton("Start", skin);
        TextButton back = new TextButton("Back", skin);

        Table buttons = new Table();
        buttons.defaults().pad(4).width(140).height(44);
        buttons.add(hostBtn);
        buttons.add(joinBtn);
        buttons.add(startRace);
        buttons.add(back);
        panel.add(buttons).colspan(2).row();

        statusLabel = new Label("Status: idle", skin);
        roomStateLabel = new Label("Room: - | players=0 | phase=-", skin);
        countdownLabel = new Label("", skin);
        panel.add(statusLabel).colspan(2).left().row();
        panel.add(roomStateLabel).colspan(2).left().row();
        panel.add(countdownLabel).colspan(2).left().row();

        root.add(panel).center();

        // Listeners
        hostBtn.addListener(new ClickListener(){ @Override public void clicked(InputEvent event, float x, float y){ onQuickHost(); }});
        joinBtn.addListener(new ClickListener(){ @Override public void clicked(InputEvent event, float x, float y){ onQuickJoin(); }});
        startRace.addListener(new ClickListener(){ @Override public void clicked(InputEvent event, float x, float y){ onStartRace(); }});
        back.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ if (lobby != null) try { lobby.close(); } catch (Exception ignored) {} game.setScreen(new MainMenuScreen(game)); }});
    }

    private void onConnect() {
        if (lobby != null && lobby.isConnected()) { setStatus("Already connected"); return; }
        setStatus("Connecting...");
        new Thread(() -> {
            try {
                lobby = new LobbyClient();
                lobby.start();
                String host = hostField.getText().trim();
                int tcp = parseIntSafe(tcpField.getText(), 54555);
                int udp = parseIntSafe(udpField.getText(), 54777);
                lobby.connect(host, tcp, udp, 3000);
                lobby.onRoomState(this::updateRoomState);
                lobby.onRaceStart(this::onRaceStartPacket);
                lobby.onError(this::onErrorMessage);
                postStatus("Connected to "+host+":"+tcp+"/"+udp);
                // Save settings
                try {
                    com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("mp_settings");
                    prefs.putString("host", host);
                    prefs.putInteger("tcp", tcp);
                    prefs.putInteger("udp", udp);
                    prefs.putString("user", userField.getText());
                    prefs.flush();
                } catch (Exception ignored) {}
            } catch (Exception e) {
                postStatus("Connect failed: "+e.getMessage());
            }
        }, "lobby-connect").start();
    }

    private void onCreateRoom() {
        if (!ensureConnected()) return;
        String user = userField.getText().trim().isEmpty()? "Player" : userField.getText().trim();
        setStatus("Creating room...");
        lobby.createRoom("Room-"+user, user, 4).whenComplete((res, ex) -> {
            if (ex != null) { postStatus("Create failed: "+ex.getMessage()); return; }
            if (res.ok) {
                postStatus("Room created: "+res.roomId);
                Gdx.app.postRunnable(() -> roomIdField.setText(res.roomId));
            } else {
                postStatus("Create failed: "+res.message);
            }
        });
    }

    private void onJoinRoom() {
        if (!ensureConnected()) return;
        String user = userField.getText().trim().isEmpty()? "Player" : userField.getText().trim();
        String roomId = roomIdField.getText().trim();
        if (roomId.isEmpty()) { setStatus("Enter Room ID"); return; }
        setStatus("Joining room...");
        lobby.joinRoom(roomId, user).whenComplete((res, ex) -> {
            if (ex != null) { postStatus("Join failed: "+ex.getMessage()); return; }
            if (res.ok) {
                postStatus("Joined: "+roomId);
            } else {
                postStatus("Join failed: "+res.message);
            }
        });
    }

    private void onLeaveRoom() {
        if (!ensureConnected()) return;
        String roomId = roomIdField.getText().trim();
        if (roomId.isEmpty()) { setStatus("Enter Room ID"); return; }
        lobby.leaveRoom(roomId);
        setStatus("Leave sent");
    }

    private boolean ensureConnected() {
        if (lobby == null || !lobby.isConnected()) { setStatus("Not connected"); return false; }
        return true;
    }

    private void setStatus(String s) { if (statusLabel!=null) statusLabel.setText("Status: "+s); }
    private void postStatus(String s) { Gdx.app.postRunnable(() -> setStatus(s)); }

    private void updateRoomState(Packets.RoomState state) {
        lastState = state;
        String text = "Room: "+state.roomId+" | players="+ (state.players==null?0:state.players.size()) + " | phase="+state.phase;
        Gdx.app.postRunnable(() -> roomStateLabel.setText(text));
    }

    private void onStartRace() {
        if (!ensureConnected()) return;
        String roomId = roomIdField.getText().trim();
        if (roomId.isEmpty()) { setStatus("Enter Room ID"); return; }
        if (lastState == null || lastState.players == null || lastState.players.size() < 2) {
            setStatus("Need >=2 players to start");
            return;
        }
        String user = userField.getText().trim().isEmpty()? "Player" : userField.getText().trim();
        setStatus("Start requested");
        lobby.startRace(roomId, user, 5);
    }

    private void onRaceStartPacket(Packets.RaceStartPacket pkt) {
        long startAt = pkt.startTimeMillis;
        int secs = pkt.countdownSeconds;
        new Thread(() -> {
            try {
                while (true) {
                    long now = System.currentTimeMillis();
                    long remainMs = Math.max(0, startAt - now);
                    int remain = (int)Math.ceil(remainMs / 1000.0);
                    String msg = remain > 0 ? ("Countdown: "+remain) : "GO!";
                    Gdx.app.postRunnable(() -> countdownLabel.setText(msg));
                    if (remain <= 0) break;
                    Thread.sleep(Math.min(200, remainMs));
                }
            } catch (InterruptedException ignored) {}
        }, "countdown-ui").start();
        postStatus("Race starting in "+secs+"s");
    }

    private void onErrorMessage(String msg) {
        postStatus("Error: "+msg);
    }

    private void onQuickHost() {
        setStatus("QuickHost...");
        if (lobby == null || !lobby.isConnected()) {
            onConnect();
            new Thread(() -> { try { Thread.sleep(400); doQuickHost(); } catch (InterruptedException ignored) {} }, "qh").start();
        } else {
            doQuickHost();
        }
    }

    private void doQuickHost() {
        if (!ensureConnected()) return;
        String user = userField.getText().trim().isEmpty()? "Player" : userField.getText().trim();
        lobby.createRoom("Room-"+user, user, 4).whenComplete((res, ex) -> {
            if (ex != null || !res.ok) { postStatus("QuickHost failed"); return; }
            Gdx.app.postRunnable(() -> {
                roomIdField.setText(res.roomId);
                try { Gdx.app.getClipboard().setContents(res.roomId); } catch (Exception ignored) {}
            });
            postStatus("Hosted: "+res.roomId+" (copied)");
        });
    }

    private void onQuickJoin() {
        setStatus("QuickJoin...");
        if (lobby == null || !lobby.isConnected()) {
            onConnect();
            new Thread(() -> { try { Thread.sleep(400); doQuickJoin(); } catch (InterruptedException ignored) {} }, "qj").start();
        } else {
            doQuickJoin();
        }
    }

    private void doQuickJoin() {
        if (!ensureConnected()) return;
        String roomId = roomIdField.getText().trim();
        if (roomId.isEmpty()) {
            try { roomId = String.valueOf(Gdx.app.getClipboard().getContents()); } catch (Exception ignored) {}
        }
        if (roomId == null || roomId.isBlank()) { postStatus("No Room ID (field/clipboard)"); return; }
        String user = userField.getText().trim().isEmpty()? "Player" : userField.getText().trim();
        final String rid = roomId.trim();
        lobby.joinRoom(rid, user).whenComplete((res, ex) -> {
            if (ex != null || !res.ok) { postStatus("QuickJoin failed"); return; }
            postStatus("Joined: "+rid);
            Gdx.app.postRunnable(() -> roomIdField.setText(rid));
        });
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    @Override public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { if (lobby!=null) try{ lobby.close(); } catch(Exception ignored) {} game.setScreen(new MainMenuScreen(game)); return; }
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { if (stage!=null) stage.getViewport().update(w,h,true);} 
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { if (stage!=null) stage.dispose(); if (skin!=null) skin.dispose(); if (lobby!=null) lobby.close(); }
}



