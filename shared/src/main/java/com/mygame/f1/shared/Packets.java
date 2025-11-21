package com.mygame.f1.shared;

import java.util.List;

/**
 * Network packet definitions for lobby and basic multiplayer sync.
 * Keep plain POJOs to stay engine-agnostic.
 */
public final class Packets {
    private Packets() {}

    // Lobby packets
    public static class CreateRoomRequest {
        public String roomName;
        public int maxPlayers = 4;
        public String username;
    }

    public static class CreateRoomResponse {
        public boolean ok;
        public String roomId;
        public String message; // error or info
        public PlayerInfo self;
    }

    public static class JoinRoomRequest {
        public String roomId;
        public String username;
    }

    public static class JoinRoomResponse {
        public boolean ok;
        public String message;
        public PlayerInfo self;
        public RoomState state;
    }

    public static class LeaveRoomRequest {
        public String roomId;
    }

    public static class ErrorResponse {
        public String message;
    }

    public static class RoomStatePacket {
        public RoomState state;
    }

    // Basic sync placeholders (extend later as per MULTIPLAYER-SYNC.md)
    public static class PlayerInputPacket {
        public int playerId;
        public long timestamp;
        public float acceleration; // -1..1
        public float steering;     // -1..1
        public boolean braking;
        public int sequenceNumber;
    }

    public static class GameStatePacket {
        public long serverTimestamp;
        public PlayerState[] playerStates;
    }

    // Value objects
    public static class PlayerInfo {
        public int playerId;
        public String username;
    }

    public static class PlayerState {
        public int playerId;
        public float x, y;
        public float rotation;
        public float velocityX, velocityY;
        public float angularVelocity;
    }

    public static class RoomState {
        public String roomId;
        public String roomName;
        public int maxPlayers;
        public List<PlayerInfo> players; // current occupants
        public RoomPhase phase = RoomPhase.WAITING;
    }

    public enum RoomPhase { WAITING, COUNTDOWN, RACING, FINISHED }

    // Race start (server broadcast)
    public static class RaceStartPacket {
        public long startTimeMillis;    // server time when race starts
        public int countdownSeconds;    // e.g., 5
    }

    // Start request (client -> server)
    public static class StartRaceRequest {
        public String roomId;
        public String initiator; // username
        public int countdownSeconds = 5;
    }
}
