package com.mygame.f1.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class TimeAttackRepository {
    private final String dbUrl;

    public TimeAttackRepository() {
        this.dbUrl = "jdbc:sqlite:" + resolveDbPath();
        init();
    }

    private static String resolveDbPath() {
        try {
            String base = System.getenv("APPDATA");
            if (base == null || base.isEmpty()) {
                base = System.getProperty("user.home") + "/.f1game";
            } else {
                base = base + "/F1Game";
            }
            Path dir = Paths.get(base, "data");
            Files.createDirectories(dir);
            return dir.resolve("game.db").toAbsolutePath().toString();
        } catch (Exception e) {
            return Paths.get("data", "game.db").toAbsolutePath().toString();
        }
    }

    private void init() {
        String ddl = "CREATE TABLE IF NOT EXISTS laps (" +
                "track_id TEXT NOT NULL, " +
                "username TEXT NOT NULL, " +
                "best_time_ms INTEGER NOT NULL, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY(track_id, username)" +
                ")";
        try (Connection c = DriverManager.getConnection(dbUrl);
             Statement s = c.createStatement()) {
            s.execute(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init laps table", e);
        }
    }

    public static class Entry {
        public final String username;
        public final long bestTimeMs;
        public Entry(String username, long bestTimeMs) { this.username = username; this.bestTimeMs = bestTimeMs; }
    }

    public long getBest(String username, String trackId) {
        String sql = "SELECT best_time_ms FROM laps WHERE track_id=? AND username=?";
        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trackId);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return -1L;
        } catch (SQLException e) {
            return -1L;
        }
    }

    public void updateBestIfImproved(String username, String trackId, long lapMs) {
        long best = getBest(username, trackId);
        if (best < 0) {
            insert(username, trackId, lapMs);
        } else if (lapMs < best) {
            update(username, trackId, lapMs);
        }
    }

    public java.util.List<Entry> getTopN(String trackId, int limit) {
        java.util.List<Entry> result = new java.util.ArrayList<>();
        String sql = "SELECT username, best_time_ms FROM laps WHERE track_id=? ORDER BY best_time_ms ASC LIMIT ?";
        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trackId);
            ps.setInt(2, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Entry(rs.getString(1), rs.getLong(2)));
                }
            }
        } catch (SQLException ignored) {}
        return result;
    }

    private void insert(String username, String trackId, long lapMs) {
        String sql = "INSERT OR REPLACE INTO laps(track_id, username, best_time_ms) VALUES(?,?,?)";
        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trackId);
            ps.setString(2, username);
            ps.setLong(3, lapMs);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private void update(String username, String trackId, long lapMs) {
        String sql = "UPDATE laps SET best_time_ms=? WHERE track_id=? AND username=?";
        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, lapMs);
            ps.setString(2, trackId);
            ps.setString(3, username);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }
}
