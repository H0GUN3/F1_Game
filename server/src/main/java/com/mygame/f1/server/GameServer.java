package com.mygame.f1.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygame.f1.shared.Packets;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Minimal lobby server: create/join/leave rooms with max 4 players; broadcasts room state.
 * Physics/state-sync will be added later per docs/specs/network/MULTIPLAYER-SYNC.md.
 */
public class GameServer {
    private final int tcpPort;
    private final int udpPort;
    private final Server server;

    // roomId -> Room
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    // connectionId -> roomId (single membership for now)
    private final Map<Integer, String> membership = new ConcurrentHashMap<>();

    public GameServer(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort; this.udpPort = udpPort;
        this.server = new Server(16384, 8192);
        Kryo kryo = server.getKryo();
        PacketRegistry.register(kryo);
        server.addListener(new ServerListener());
    }

    public void start() throws IOException {
        server.bind(tcpPort, udpPort);
        server.start();
        System.out.printf("Server started TCP=%d UDP=%d%n", tcpPort, udpPort);
    }

    private class ServerListener extends Listener {
        @Override public void connected(Connection connection) {
            System.out.printf("connected: %d%n", connection.getID());
        }

        @Override public void disconnected(Connection connection) {
            System.out.printf("disconnected: %d%n", connection.getID());
            leaveAll(connection);
        }

        @Override public void received(Connection c, Object object) {
            try {
                if (object instanceof Packets.CreateRoomRequest req) {
                    onCreateRoom(c, req);
                } else if (object instanceof Packets.JoinRoomRequest req) {
                    onJoinRoom(c, req);
                } else if (object instanceof Packets.LeaveRoomRequest req) {
                    onLeaveRoom(c, req);
                } else if (object instanceof Packets.ReadyRequest req) {
                    onReady(c, req);
                } else if (object instanceof Packets.RoomListRequest) {
                    onRoomList(c);
                } else if (object instanceof Packets.StartRaceRequest req) {
                    onStartRace(c, req);
                }
            } catch (Exception e) {
                Packets.ErrorResponse err = new Packets.ErrorResponse();
                err.message = "server error: " + e.getMessage();
                c.sendTCP(err);
            }
        }
    }

    private void onCreateRoom(Connection c, Packets.CreateRoomRequest req) {
        int max = req.maxPlayers > 0 ? req.maxPlayers : 4;
        if (max > 4) max = 4; // hard cap per request
        String roomId = UUID.randomUUID().toString().substring(0, 8);
        Room room = new Room(roomId, Optional.ofNullable(req.roomName).orElse("Room"), max);
        rooms.put(roomId, room);
        System.out.printf("createRoom: %s by %s%n", roomId, req.username);

        Packets.PlayerInfo self = new Packets.PlayerInfo();
        self.playerId = c.getID();
        self.username = safeName(req.username);
        room.join(c, self);
        membership.put(c.getID(), roomId);

        Packets.CreateRoomResponse res = new Packets.CreateRoomResponse();
        res.ok = true; res.roomId = roomId; res.message = "created"; res.self = self;
        c.sendTCP(res);
        broadcastRoomState(room);
    }

    private void onJoinRoom(Connection c, Packets.JoinRoomRequest req) {
        Room room = rooms.get(req.roomId);
        Packets.JoinRoomResponse res = new Packets.JoinRoomResponse();
        if (room == null) {
            res.ok = false; res.message = "room not found";
            c.sendTCP(res); return;
        }
        if (room.players.size() >= room.maxPlayers) {
            res.ok = false; res.message = "room full";
            c.sendTCP(res); return;
        }
        Packets.PlayerInfo self = new Packets.PlayerInfo();
        self.playerId = c.getID();
        self.username = safeName(req.username);
        room.join(c, self);
        membership.put(c.getID(), room.id);

        res.ok = true; res.message = "joined"; res.self = self; res.state = room.toState();
        c.sendTCP(res);
        broadcastRoomState(room);
    }

    private void onLeaveRoom(Connection c, Packets.LeaveRoomRequest req) {
        Room room = rooms.get(req.roomId);
        if (room != null) {
            room.leave(c.getID());
            membership.remove(c.getID());
            broadcastRoomState(room);
            cleanupIfEmpty(room);
        }
    }

    private void onReady(Connection c, Packets.ReadyRequest req) {
        Room room = rooms.get(req.roomId);
        if (room == null) { sendError(c, "room not found"); return; }
        room.setReady(c.getID(), req.ready);
        broadcastRoomState(room);
    }

    private void onRoomList(Connection c) {
        Packets.RoomListResponse res = new Packets.RoomListResponse();
        res.rooms = new ArrayList<>();
        for (Room r : rooms.values()) {
            res.rooms.add(r.toState());
        }
        c.sendTCP(res);
    }

    private void onStartRace(Connection c, Packets.StartRaceRequest req) {
        Room room = rooms.get(req.roomId);
        if (room == null) {
            sendError(c, "start denied: room not found");
            return;
        }
        if (room.phase != Packets.RoomPhase.WAITING) {
            sendError(c, "start denied: not in WAITING phase");
            return;
        }
        if (room.players.size() < 2) {
            sendError(c, "start denied: need at least 2 players");
            return;
        }
        if (!room.allReady()) {
            sendError(c, "start denied: not all players ready");
            return;
        }
        int seconds = req.countdownSeconds <= 0 ? 5 : Math.min(req.countdownSeconds, 10);
        room.phase = Packets.RoomPhase.COUNTDOWN;
        Packets.RaceStartPacket start = new Packets.RaceStartPacket();
        start.countdownSeconds = seconds;
        start.startTimeMillis = System.currentTimeMillis() + seconds * 1000L;
        for (int connId : room.connectionIds()) {
            server.sendToTCP(connId, start);
        }
        broadcastRoomState(room);
    }

    private void sendError(Connection c, String message) {
        Packets.ErrorResponse err = new Packets.ErrorResponse();
        err.message = message;
        c.sendTCP(err);
    }

    private void leaveAll(Connection c) {
        String roomId = membership.remove(c.getID());
        if (roomId != null) {
            Room room = rooms.get(roomId);
            if (room != null) {
                room.leave(c.getID());
                broadcastRoomState(room);
                cleanupIfEmpty(room);
            }
        }
    }

    private void broadcastRoomState(Room room) {
        Packets.RoomStatePacket pkt = new Packets.RoomStatePacket();
        pkt.state = room.toState();
        for (int connId : room.connectionIds()) {
            server.sendToTCP(connId, pkt);
        }
    }

    private void cleanupIfEmpty(Room room) {
        if (room.players.isEmpty()) {
            rooms.remove(room.id);
            System.out.printf("cleanup room: %s%n", room.id);
        }
    }

    private static String safeName(String s) {
        String n = (s == null || s.isBlank()) ? "Player" : s.trim();
        return n.length() > 24 ? n.substring(0, 24) : n;
    }

    private static final class Room {
        final String id; final String name; final int maxPlayers;
        final Map<Integer, Packets.PlayerInfo> players = new ConcurrentHashMap<>();
        final List<Integer> order = new CopyOnWriteArrayList<>();
        final Set<Integer> ready = Collections.synchronizedSet(new HashSet<>());
        Packets.RoomPhase phase = Packets.RoomPhase.WAITING;

        Room(String id, String name, int maxPlayers) {
            this.id = id; this.name = name; this.maxPlayers = maxPlayers;
        }

        synchronized void join(Connection c, Packets.PlayerInfo info) {
            players.put(c.getID(), info);
            if (!order.contains(c.getID())) order.add(c.getID());
        }

        synchronized void leave(int connectionId) {
            players.remove(connectionId);
            order.remove((Integer) connectionId);
            ready.remove(connectionId);
        }

        List<Integer> connectionIds() { return new ArrayList<>(players.keySet()); }

        Packets.RoomState toState() {
            Packets.RoomState rs = new Packets.RoomState();
            rs.roomId = id; rs.roomName = name; rs.maxPlayers = maxPlayers; rs.phase = phase;
            rs.players = new ArrayList<>();
            for (Integer id : order) {
                Packets.PlayerInfo p = players.get(id);
                if (p != null) {
                    Packets.PlayerInfo copy = new Packets.PlayerInfo();
                    copy.playerId = p.playerId;
                    copy.username = p.username;
                    copy.ready = ready.contains(id);
                    rs.players.add(copy);
                }
            }
            return rs;
        }

        synchronized void setReady(int connectionId, boolean value) {
            if (!players.containsKey(connectionId)) return;
            if (value) ready.add(connectionId); else ready.remove(connectionId);
        }

        synchronized boolean allReady() {
            return !players.isEmpty() && ready.containsAll(players.keySet());
        }
    }
}
