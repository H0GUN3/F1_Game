package com.mygame.f1.network;

import com.mygame.f1.shared.Packets;

public class LobbyClientSmoke {
    public static void main(String[] args) throws Exception {
        String host = args.length>0? args[0] : "localhost";
        int tcp = args.length>1? Integer.parseInt(args[1]) : 54555;
        int udp = args.length>2? Integer.parseInt(args[2]) : 54777;
        String user = args.length>3? args[3] : "Tester";
        String room = args.length>4? args[4] : "TestRoom";

        LobbyClient cli = new LobbyClient();
        cli.start();
        cli.connect(host, tcp, udp, 3000);
        System.out.println("[SMOKE] Connected to "+host+":"+tcp+"/"+udp);

        cli.onRoomState(state -> System.out.println("[SMOKE] RoomState: "+state.roomId+" players="+state.players.size()+" phase="+state.phase));

        Packets.CreateRoomResponse res = cli.createRoom(room, user, 4).get();
        System.out.println("[SMOKE] CreateRoom ok="+res.ok+" id="+res.roomId+" msg="+res.message);

        Thread.sleep(500); // allow broadcast
        cli.leaveRoom(res.roomId);
        System.out.println("[SMOKE] LeaveRoom sent");
        Thread.sleep(500);
        cli.close();
        System.out.println("[SMOKE] Done");
    }
}
