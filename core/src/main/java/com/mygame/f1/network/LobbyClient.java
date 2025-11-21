package com.mygame.f1.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygame.f1.shared.Packets;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/** Minimal lobby client for room create/join/leave and state updates. */
public class LobbyClient implements AutoCloseable {
    private final Client client = new Client(16384, 8192);
    private volatile boolean connected = false;
    private final ConcurrentLinkedQueue<Consumer<Packets.RoomState>> roomStateListeners = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Consumer<Packets.RaceStartPacket>> raceStartListeners = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Consumer<String>> errorListeners = new ConcurrentLinkedQueue<>();

    public void start() {
        PacketRegistry.register(client.getKryo());
        client.addListener(new Listener() {
            @Override public void connected(Connection connection) { connected = true; }
            @Override public void disconnected(Connection connection) { connected = false; }
            @Override public void received(Connection connection, Object object) {
                if (object instanceof Packets.RoomStatePacket pkt) {
                    notifyRoomState(pkt.state);
                } else if (object instanceof Packets.RaceStartPacket pkt) {
                    notifyRaceStart(pkt);
                } else if (object instanceof Packets.ErrorResponse err) {
                    notifyError(err.message);
                }
            }
        });
        client.start();
    }

    public void connect(String host, int tcp, int udp, int timeoutMs) throws IOException {
        client.connect(timeoutMs, host, tcp, udp);
    }

    public boolean isConnected() { return connected; }

    public CompletableFuture<Packets.CreateRoomResponse> createRoom(String roomName, String username, int maxPlayers) {
        Packets.CreateRoomRequest req = new Packets.CreateRoomRequest();
        req.roomName = roomName; req.username = username; req.maxPlayers = Math.max(1, Math.min(maxPlayers, 4));
        CompletableFuture<Packets.CreateRoomResponse> fut = new CompletableFuture<>();
        client.addListener(new Listener() {
            @Override public void received(Connection connection, Object object) {
                if (object instanceof Packets.CreateRoomResponse res) {
                    fut.complete(res);
                    client.removeListener(this);
                } else if (object instanceof Packets.ErrorResponse err) {
                    fut.completeExceptionally(new RuntimeException(err.message));
                    client.removeListener(this);
                }
            }
        });
        client.sendTCP(req);
        return fut;
    }

    public CompletableFuture<Packets.JoinRoomResponse> joinRoom(String roomId, String username) {
        Packets.JoinRoomRequest req = new Packets.JoinRoomRequest();
        req.roomId = Objects.requireNonNull(roomId); req.username = username;
        CompletableFuture<Packets.JoinRoomResponse> fut = new CompletableFuture<>();
        client.addListener(new Listener() {
            @Override public void received(Connection connection, Object object) {
                if (object instanceof Packets.JoinRoomResponse res) {
                    fut.complete(res);
                    client.removeListener(this);
                } else if (object instanceof Packets.ErrorResponse err) {
                    fut.completeExceptionally(new RuntimeException(err.message));
                    client.removeListener(this);
                }
            }
        });
        client.sendTCP(req);
        return fut;
    }

    public void leaveRoom(String roomId) {
        Packets.LeaveRoomRequest req = new Packets.LeaveRoomRequest();
        req.roomId = roomId;
        client.sendTCP(req);
    }

    public void onRoomState(Consumer<Packets.RoomState> listener) { roomStateListeners.add(listener); }
    public void onRaceStart(Consumer<Packets.RaceStartPacket> listener) { raceStartListeners.add(listener); }
    public void onError(Consumer<String> listener) { errorListeners.add(listener); }

    private void notifyRoomState(Packets.RoomState state) { roomStateListeners.forEach(l -> l.accept(state)); }
    private void notifyRaceStart(Packets.RaceStartPacket pkt) { raceStartListeners.forEach(l -> l.accept(pkt)); }
    private void notifyError(String msg) { errorListeners.forEach(l -> l.accept(msg)); }

    public void startRace(String roomId, String username, int countdownSeconds) {
        Packets.StartRaceRequest req = new Packets.StartRaceRequest();
        req.roomId = roomId; req.initiator = username; req.countdownSeconds = countdownSeconds;
        client.sendTCP(req);
    }

    @Override public void close() { client.stop(); }
}
