package com.mygame.f1.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class SqliteUserStore implements AuthStore {
    private final String dbUrl;

    public SqliteUserStore() {
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
        String ddl = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "salt TEXT NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        try (Connection c = DriverManager.getConnection(dbUrl);
             Statement s = c.createStatement()) {
            s.execute(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init database", e);
        }
    }

    @Override
    public boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.length() < 4) return false;
        String salt = genSalt();
        String hash = hash(password, salt);
        String sql = "INSERT INTO users(username, salt, password_hash) VALUES(?,?,?)";
        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, salt);
            ps.setString(3, hash);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // duplicate or DB error
        }
    }

    @Override
    public boolean verify(String username, String password) {
        String sql = "SELECT salt, password_hash FROM users WHERE username = ?";
        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String salt = rs.getString(1);
                String expected = rs.getString(2);
                String h = hash(password, salt);
                return expected.equals(h);
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private static String genSalt() {
        byte[] b = new byte[16];
        new SecureRandom().nextBytes(b);
        return Base64.getEncoder().encodeToString(b);
    }

    private static String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            byte[] out = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            return "";
        }
    }
}

