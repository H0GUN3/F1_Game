package com.mygame.f1.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygame.f1.Main;
import com.mygame.f1.network.LobbyClient;
import com.mygame.f1.shared.Packets;
import com.mygame.f1.ui.SkinFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 간단한 멀티플레이 로비 화면.
 * - 방 생성/입장
 * - 방 목록 조회
 * - 플레이어 리스트(Ready/Host 표시)
 * - Ready 토글, Host만 Start
 */
public class MultiplayerPlaceholderScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;

    private TextField tfUser;
    private TextField tfRoomName;
    private TextField tfRoomId;
    private TextField tfHost;
    private Label statusLabel;
    private Table roomListTable;
    private Table playersTable;
    private TextButton readyBtn;
    private TextButton startBtn;

    private LobbyClient client;
    private String currentRoomId;
    private int selfId = -1;
    private boolean isReady = false;
    private Packets.RoomState lastRoomState;

    public MultiplayerPlaceholderScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        skin = SkinFactory.createDefaultSkin();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.pad(10);
        stage.addActor(root);

        // 상단 입력/버튼
        tfUser = new TextField(game.playerName != null ? game.playerName : "Player", skin);
        tfRoomName = new TextField("Room", skin);
        tfRoomId = new TextField("", skin);
        tfHost = new TextField("localhost", skin);
        tfHost.setMessageText("host");

        TextButton btnCreate = new TextButton("Host(Create)", skin);
        TextButton btnJoin = new TextButton("Join", skin);
        TextButton btnRefresh = new TextButton("Refresh Rooms", skin);
        readyBtn = new TextButton("Ready: OFF", skin);
        startBtn = new TextButton("Start (Host)", skin);
        TextButton btnExit = new TextButton("Exit Room", skin);
        TextButton btnBack = new TextButton("Back", skin);

        statusLabel = new Label("Ready when all players are ready. Need 2+ to start.", skin);
        statusLabel.setWrap(true);

        // 방 목록 테이블
        roomListTable = new Table(skin);
        roomListTable.defaults().pad(4);
        ScrollPane roomScroll = new ScrollPane(roomListTable, skin);
        roomScroll.setFadeScrollBars(false);

        // 플레이어 리스트 테이블
        playersTable = new Table(skin);
        playersTable.defaults().pad(4);
        ScrollPane playerScroll = new ScrollPane(playersTable, skin);
        playerScroll.setFadeScrollBars(false);

        // 레이아웃
        Table top = new Table();
        top.defaults().pad(4).height(32);
        top.add(new Label("User", skin)).width(60);
        top.add(tfUser).width(160);
        top.add(new Label("Host", skin)).width(60);
        top.add(tfHost).width(160);
        top.add(btnRefresh).width(160);
        top.row();
        top.add(new Label("Room Name", skin)).colspan(1);
        top.add(tfRoomName).width(160);
        top.add(new Label("Room Id", skin)).width(60);
        top.add(tfRoomId).width(160);
        top.add(btnCreate).width(160);
        top.add(btnJoin).width(120);
        top.row();

        Table center = new Table();
        center.defaults().pad(6).top().left();
        center.add(new Label("Rooms", skin, "title")).left();
        center.add(new Label("Players", skin, "title")).left().padLeft(20);
        center.row();
        center.add(roomScroll).width(360).height(240);
        center.add(playerScroll).width(360).height(240).padLeft(20);

        Table bottom = new Table();
        bottom.defaults().pad(6).height(44);
        bottom.add(readyBtn).width(140);
        bottom.add(startBtn).width(140);
        bottom.add(btnExit).width(120);
        bottom.add(btnBack).width(120);
        bottom.row();
        bottom.add(statusLabel).colspan(4).width(760).padTop(4);

        root.add(top).expandX().fillX().row();
        root.add(center).expand().fill().row();
        root.add(bottom).expandX().fillX().row();

        hookEvents(btnCreate, btnJoin, btnRefresh, btnExit, btnBack);
        updateReadyButton();
        updateStartButton();

        initClient();
    }

    private void initClient() {
        client = new LobbyClient();
        client.start();
        try {
            client.connect(tfHost.getText(), 54555, 54777, 3000);
            setStatus("Connected to " + tfHost.getText());
            refreshRooms();
        } catch (IOException e) {
            setStatus("Connect failed: " + e.getMessage());
        }

        client.onRoomList(rooms -> Gdx.app.postRunnable(() -> renderRoomList(rooms)));
        client.onRoomState(state -> Gdx.app.postRunnable(() -> {
            lastRoomState = state;
            renderPlayers(state);
            updateStartButton();
        }));
        client.onRaceStart(pkt -> Gdx.app.postRunnable(() -> setStatus("Race starting in " + pkt.countdownSeconds + "s")));
        client.onError(msg -> Gdx.app.postRunnable(() -> setStatus("Error: " + msg)));
    }

    private void hookEvents(TextButton btnCreate, TextButton btnJoin, TextButton btnRefresh, TextButton btnExit, TextButton btnBack) {
        btnCreate.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { createRoom(); }});
        btnJoin.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { joinRoom(); }});
        btnRefresh.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { refreshRooms(); }});
        readyBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { toggleReady(); }});
        startBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { startRace(); }});
        btnExit.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { leaveRoom(); }});
        btnBack.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { leaveRoom(); game.setScreen(new MainMenuScreen(game)); }});
    }

    private void createRoom() {
        if (!client.isConnected()) { setStatus("Not connected"); return; }
        String user = safeName(tfUser.getText());
        String room = tfRoomName.getText().isEmpty() ? "Room" : tfRoomName.getText();
        client.createRoom(room, user, 4).whenComplete((res, err) -> Gdx.app.postRunnable(() -> {
            if (err != null || res == null || !res.ok) {
                setStatus("Create failed: " + (err != null ? err.getMessage() : res != null ? res.message : ""));
                return;
            }
            currentRoomId = res.roomId;
            selfId = res.self.playerId;
            isReady = false;
            setStatus("Created room " + res.roomId);
            refreshRooms();
        }));
    }

    private void joinRoom() {
        if (!client.isConnected()) { setStatus("Not connected"); return; }
        String user = safeName(tfUser.getText());
        String roomId = tfRoomId.getText();
        if (roomId == null || roomId.isEmpty()) { setStatus("Enter room id to join"); return; }
        client.joinRoom(roomId, user).whenComplete((res, err) -> Gdx.app.postRunnable(() -> {
            if (err != null || res == null || !res.ok) {
                setStatus("Join failed: " + (err != null ? err.getMessage() : res != null ? res.message : ""));
                return;
            }
            currentRoomId = roomId;
            selfId = res.self.playerId;
            isReady = false;
            setStatus("Joined room " + roomId);
            renderPlayers(res.state);
            updateStartButton();
        }));
    }

    private void refreshRooms() {
        if (!client.isConnected()) { setStatus("Not connected"); return; }
        client.requestRoomList();
    }

    private void toggleReady() {
        if (currentRoomId == null) { setStatus("Join or create a room first"); return; }
        isReady = !isReady;
        client.setReady(currentRoomId, isReady);
        updateReadyButton();
    }

    private void startRace() {
        if (!currentRoomIdValid()) return;
        if (!isHost()) { setStatus("Only host can start"); return; }
        if (!canStart()) { setStatus("Need 2+ players and all Ready"); return; }
        String user = safeName(tfUser.getText());
        client.startRace(currentRoomId, user, 5);
        setStatus("Start requested");
    }

    private void leaveRoom() {
        if (currentRoomId != null) {
            client.leaveRoom(currentRoomId);
        }
        currentRoomId = null;
        selfId = -1;
        isReady = false;
        lastRoomState = null;
        renderPlayers(null);
        updateReadyButton();
        updateStartButton();
    }

    private boolean currentRoomIdValid() {
        if (currentRoomId == null) {
            setStatus("No room joined/created");
            return false;
        }
        return true;
    }

    private void renderRoomList(List<Packets.RoomState> rooms) {
        roomListTable.clear();
        roomListTable.add(new Label("Room", skin)).left();
        roomListTable.add(new Label("Players", skin)).left();
        roomListTable.row();
        if (rooms == null) return;
        for (Packets.RoomState r : rooms) {
            TextButton btn = new TextButton(r.roomName + " (" + r.roomId + ")", skin);
            btn.getLabel().setAlignment(Align.left);
            btn.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { tfRoomId.setText(r.roomId); }});
            roomListTable.add(btn).left().width(260);
            roomListTable.add(new Label(r.players.size() + "/" + r.maxPlayers, skin)).left();
            roomListTable.row();
        }
    }

    private void renderPlayers(Packets.RoomState state) {
        playersTable.clear();
        playersTable.add(new Label("#", skin)).width(20);
        playersTable.add(new Label("Nick", skin)).width(180);
        playersTable.add(new Label("Ready", skin)).width(60);
        playersTable.add(new Label("Host", skin)).width(60);
        playersTable.row();
        if (state == null || state.players == null) return;
        List<Packets.PlayerInfo> players = new ArrayList<>(state.players);
        for (int i = 0; i < 4; i++) {
            String nick = "-";
            String ready = "-";
            String host = "-";
            if (i < players.size()) {
                Packets.PlayerInfo p = players.get(i);
                nick = p.username != null ? p.username : ("Player" + p.playerId);
                ready = p.ready ? "O" : "X";
                host = (i == 0) ? "Host" : "";
            }
            playersTable.add(new Label(String.valueOf(i + 1), skin)).width(20);
            playersTable.add(new Label(nick, skin)).width(180);
            playersTable.add(new Label(ready, skin)).width(60);
            playersTable.add(new Label(host, skin)).width(60);
            playersTable.row();
        }
        lastRoomState = state;
    }

    private boolean isHost() {
        if (lastRoomState == null || lastRoomState.players == null || lastRoomState.players.isEmpty()) return false;
        return lastRoomState.players.get(0).playerId == selfId;
    }

    private boolean canStart() {
        if (lastRoomState == null || lastRoomState.players == null) return false;
        if (lastRoomState.players.size() < 2) return false;
        for (Packets.PlayerInfo p : lastRoomState.players) {
            if (!p.ready) return false;
        }
        return true;
    }

    private void updateReadyButton() {
        readyBtn.setText("Ready: " + (isReady ? "ON" : "OFF"));
    }

    private void updateStartButton() {
        boolean enabled = isHost() && canStart();
        startBtn.setDisabled(!enabled);
        startBtn.setText(enabled ? "Start" : "Start (Need Ready)");
    }

    private String safeName(String s) {
        String n = s == null ? "" : s.trim();
        if (n.isEmpty()) n = "Player";
        if (n.length() > 24) n = n.substring(0, 24);
        return n;
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { leaveRoom(); game.setScreen(new MainMenuScreen(game)); return; }

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { if (stage != null) stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (client != null) client.close();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
    }
}
