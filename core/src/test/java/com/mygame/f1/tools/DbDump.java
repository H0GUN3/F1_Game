package com.mygame.f1.tools;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbDump {
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

    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlite:" + resolveDbPath();
        try (Connection c = DriverManager.getConnection(url)) {
            try (Statement s = c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, salt TEXT NOT NULL, password_hash TEXT NOT NULL, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            }
            String sql = "SELECT username, length(salt) as salt_len, length(password_hash) as hash_len, created_at FROM users ORDER BY created_at DESC";
            try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
                int count = 0;
                while (rs.next()) {
                    String u = rs.getString("username");
                    int sl = rs.getInt("salt_len");
                    int hl = rs.getInt("hash_len");
                    String ca = rs.getString("created_at");
                    System.out.println(u + " | salt=" + sl + " | hash=" + hl + " | " + ca);
                    count++;
                }
                if (count == 0) {
                    System.out.println("(no users)");
                }
            }
        }
    }
}

