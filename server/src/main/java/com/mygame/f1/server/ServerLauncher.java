package com.mygame.f1.server;

public class ServerLauncher {
    public static void main(String[] args) throws Exception {
        int tcp = 54555;
        int udp = 54777;
        if (args.length >= 1) try { tcp = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        if (args.length >= 2) try { udp = Integer.parseInt(args[1]); } catch (Exception ignored) {}

        GameServer server = new GameServer(tcp, udp);
        server.start();
    }
}

