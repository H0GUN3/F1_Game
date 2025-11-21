package com.mygame.f1.network;

import com.mygame.f1.shared.Packets;

public class LobbyHoldSmoke {
    public static void main(String[] args) throws Exception {
        String host = args.length>0? args[0] : "localhost";
        int tcp = args.length>1? Integer.parseInt(args[1]) : 54555;
        int udp = args.length>2? Integer.parseInt(args[2]) : 54777;
        String user = args.length>3? args[3] : "Holder";
        String room = args.length>4? args[4] : "HoldRoom";
        int holdSec = args.length>5? Integer.parseInt(args[5]) : 60;

        try (LobbyClient cli = new LobbyClient()) {
            cli.start();
            cli.connect(host, tcp, udp, 3000);
            System.out.println("[HOLD] Connected to "+host+":"+tcp+"/"+udp);
            cli.onRoomState(state -> System.out.println("[HOLD] RoomState: "+state.roomId+" players="+state.players.size()+" phase="+state.phase));
            Packets.CreateRoomResponse res = cli.createRoom(room, user, 4).get();
            if (!res.ok) throw new RuntimeException("create failed: "+res.message);
            System.out.println("[HOLD] RoomId="+res.roomId+" (holding "+holdSec+"s)");
            Thread.sleep(holdSec * 1000L);
            System.out.println("[HOLD] Done");
        }
    }
}
