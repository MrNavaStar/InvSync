package mrnavastar.invsync;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * One row per player in a SQLite database shared by server processes on the same host.
 * WAL allows readers to proceed alongside a writer.
 */
class Db {
    /** Null fields leave the corresponding stored category unchanged when passed to put. */
    record Row(byte[] playerData, String advancements, String stats) {}

    // Access to this connection is serialized between the server thread and join workers.
    private final Connection conn;

    Db(String file) throws SQLException {
        SQLiteConfig cfg = new SQLiteConfig();
        cfg.setJournalMode(SQLiteConfig.JournalMode.WAL);
        cfg.setBusyTimeout(5000);
        SQLiteDataSource ds = new SQLiteDataSource(cfg);
        ds.setUrl("jdbc:sqlite:" + file);
        conn = ds.getConnection();
        try (Statement s = conn.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, player_data BLOB, advancements TEXT, stats TEXT, locked INTEGER NOT NULL DEFAULT 0)");
        }
    }

    synchronized Row get(UUID uuid) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT player_data, advancements, stats FROM players WHERE uuid = ?")) {
            p.setString(1, uuid.toString());
            ResultSet r = p.executeQuery();
            return r.next() ? new Row(r.getBytes(1), r.getString(2), r.getString(3)) : null;
        }
    }

    synchronized void put(UUID uuid, Row row) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO players (uuid, player_data, advancements, stats) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT(uuid) DO UPDATE SET player_data = COALESCE(excluded.player_data, players.player_data), "
                + "advancements = COALESCE(excluded.advancements, players.advancements), stats = COALESCE(excluded.stats, players.stats)")) {
            p.setString(1, uuid.toString());
            p.setBytes(2, row.playerData());
            p.setString(3, row.advancements());
            p.setString(4, row.stats());
            p.executeUpdate();
        }
    }

    synchronized boolean isLocked(UUID uuid) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT locked FROM players WHERE uuid = ?")) {
            p.setString(1, uuid.toString());
            ResultSet r = p.executeQuery();
            return r.next() && r.getBoolean(1);
        }
    }

    synchronized void setLocked(UUID uuid, boolean locked) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO players (uuid, locked) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET locked = excluded.locked")) {
            p.setString(1, uuid.toString());
            p.setBoolean(2, locked);
            p.executeUpdate();
        }
    }

    synchronized void close() throws SQLException {
        conn.close();
    }
}
