package com.mygame.f1.network;

import com.mygame.f1.shared.Packets;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Smoke test that spins up two lobby clients (host + joiner) and walks through
 * create room -> join room -> start race flow against a running server.
 */
public final class LobbyRoundtripSmoke {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int tcp = args.length > 1 ? Integer.parseInt(args[1]) : 54555;
        int udp = args.length > 2 ? Integer.parseInt(args[2]) : 54777;
        String hostUser = args.length > 3 ? args[3] : "Host";
        String joinUser = args.length > 4 ? args[4] : "Joiner";

        try (LobbyClient hostClient = new LobbyClient();
             LobbyClient joinClient = new LobbyClient()) {
            hostClient.start();
            joinClient.start();
            hostClient.connect(host, tcp, udp, 3000);
            joinClient.connect(host, tcp, udp, 3000);
            System.out.printf("[ROUNDTRIP] Connected host+joiner to %s:%d/%d%n", host, tcp, udp);

            CountDownLatch countdownLatch = new CountDownLatch(2);
            hostClient.onRaceStart(pkt -> {
                System.out.println("[ROUNDTRIP] Host got RaceStart countdown=" + pkt.countdownSeconds);
                countdownLatch.countDown();
            });
            joinClient.onRaceStart(pkt -> {
                System.out.println("[ROUNDTRIP] Joiner got RaceStart countdown=" + pkt.countdownSeconds);
                countdownLatch.countDown();
            });

            Packets.CreateRoomResponse createRes = hostClient.createRoom("RoundtripRoom", hostUser, 4).get();
            if (!createRes.ok) throw new IllegalStateException("Create failed: " + createRes.message);
            String roomId = createRes.roomId;
            System.out.println("[ROUNDTRIP] Room created id=" + roomId);

            Packets.JoinRoomResponse joinRes = joinClient.joinRoom(roomId, joinUser).get();
            if (!joinRes.ok) throw new IllegalStateException("Join failed: " + joinRes.message);
            System.out.println("[ROUNDTRIP] Joiner entered room id=" + roomId);

            hostClient.startRace(roomId, hostUser, 3);
            System.out.println("[ROUNDTRIP] Start requested (countdown=3)");

            if (!countdownLatch.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("RaceStart countdown not received by both clients");
            }
            System.out.println("[ROUNDTRIP] Both clients received RaceStart packet");
        }
    }
}
