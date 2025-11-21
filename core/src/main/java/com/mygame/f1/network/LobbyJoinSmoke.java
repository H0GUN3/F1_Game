package com.mygame.f1.network;

import com.mygame.f1.shared.Packets;

public class LobbyJoinSmoke {
    public static void main(String[] args) throws Exception {
        String host = args.length>0? args[0] : "localhost";
        int tcp = args.length>1? Integer.parseInt(args[1]) : 54555;
        int udp = args.length>2? Integer.parseInt(args[2]) : 54777;
        String user = args.length>3? args[3] : "Joiner";
        String roomId = args.length>4? args[4] : System.getenv().getOrDefault("MP_ROOM_ID", "");
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId required (arg5 or MP_ROOM_ID)");

        try (LobbyClient cli = new LobbyClient()) {
            cli.start();
            cli.connect(host, tcp, udp, 3000);
            System.out.println("[JOIN] Connected to "+host+":"+tcp+"/"+udp);
            cli.onRoomState(state -> System.out.println("[JOIN] RoomState: "+state.roomId+" players="+state.players.size()+" phase="+state.phase));
            Packets.JoinRoomResponse res = cli.joinRoom(roomId, user).get();
            if (!res.ok) throw new RuntimeException("join failed: "+res.message);
            System.out.println("[JOIN] Joined room="+roomId+" as "+res.self.username);
            Thread.sleep(2000);
            cli.leaveRoom(roomId);
            System.out.println("[JOIN] Leave sent");
        }
    }
}
