package com.mygame.f1.server;

public class ServerLauncher {
    public static void main(String[] args) throws Exception {
        int tcp = 54555;
        int udp = 54777;
        if (args.length >= 2) {
            tcp = Integer.parseInt(args[0]);
            udp = Integer.parseInt(args[1]);
        }
        GameServer server = new GameServer(tcp, udp);
        server.start();
    }
}
