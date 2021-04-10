package com.mrnavastar.invsync.util;

import java.sql.*;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    private static String databaseName, tableName, databaseDirectory;
    public static Connection connection;

    private static void getConfigData() {
        databaseName = ConfigManager.Database_Name;
        tableName = ConfigManager.Database_Table_Name;
        databaseDirectory = ConfigManager.Database_Directory;
    }

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + databaseDirectory + "/" + databaseName;

            connection = DriverManager.getConnection(url);

        } catch (SQLException | ClassNotFoundException ignore) {
            log(Level.ERROR, "Failed to connect to database!");
            log(Level.ERROR, "This usually occurs if the given database directory doesn't exist or is invalid");
        }
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException ignore) {}
    }

    public static void enableWALMode() {
        String sql = "PRAGMA journal_mode=WAL;";
        executeStatement(sql);
    }

    private static boolean executeStatement(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            return true;

        } catch (SQLException ignore) {}
        return false;
    }

    public static String executeStatementAndReturn(String sql, String name) {
        String result = null;
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            result = resultSet.getString(name);

        } catch (SQLException ignore) {}
        return result;
    }

    public static boolean columnExists (String column) {
        String sql = "SELECT " + column + " FROM " + tableName;
        return executeStatement(sql);
    }

    private static void createColumns() {
        ArrayList<Column> columns = new ArrayList<>();

        if (ConfigManager.Sync_Inv && !columnExists("inv0")) {
            for (int i = 0; i < 36; i++) {
                columns.add(new Column("inv" + i, "TEXT"));
            }
            columns.add(new Column("offHand", "TEXT"));
            columns.add(new Column("selectedSlot", "INTEGER"));
        }

        if (ConfigManager.Sync_Armour && !columnExists("armour0")) {
            for (int i = 0; i < 4; i++) {
                columns.add(new Column("armour" + i, "TEXT"));
            }
        }

        if (ConfigManager.Sync_eChest && !columnExists("eChest0")) {
            for (int i = 0; i < 27; i++) {
               columns.add(new Column("eChest" + i, "TEXT"));
            }
        }

        if (ConfigManager.Sync_Xp && !columnExists("xp")) {
            columns.add(new Column("xp", "INTEGER"));
            columns.add(new Column("xpProgress", "REAL"));
        }

        if (ConfigManager.Sync_Score && !columnExists("score")) columns.add(new Column("score", "INTEGER"));
        if (ConfigManager.Sync_Health && !columnExists("health")) columns.add(new Column("health", "REAL"));
        if (ConfigManager.Sync_Food_Level && !columnExists("foodLevel")) columns.add(new Column("foodLevel", "INTEGER"));
        if (ConfigManager.Sync_Status_Effects && !columnExists("statusEffects")) columns.add(new Column("statusEffects", "TEXT"));

        for (Column c : columns) {
            String sql = "ALTER TABLE " + tableName + " ADD " + c.getName() + " " + c.getType();
            executeStatement(sql);
        }
    }

    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (uuid TEXT PRIMARY KEY)";
        executeStatement(sql);
        createColumns();
    }

    public static void createRow(String uuid) {
        String sql = "INSERT OR REPLACE INTO " + tableName + "(uuid) VALUES('" + uuid + "');";
        executeStatement(sql);
    }

    public static void saveString(String uuid, String name, String str) {
        String sql = "UPDATE " + tableName + " SET " + name + " = '" + str + "' WHERE uuid = '" + uuid + "'";
        executeStatement(sql);
    }

    public static void saveInt(String uuid, String name, int amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE uuid = '" + uuid + "'";
        executeStatement(sql);
    }

    public static void saveFloat(String uuid, String name, float amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE uuid = '" + uuid + "'";
        executeStatement(sql);
    }

    public static String loadString(String uuid, String name) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null) {
            return executeStatementAndReturn(sql, name);
        } else {
            return "{id:\"minecraft:air\",Count:1b}";
        }
    }

    public static int loadInt(String uuid, String name, int defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null) {
            return Integer.parseInt(executeStatementAndReturn(sql, name));
        } else {
            return defaultValue;
        }
    }

    public static float loadFloat(String uuid, String name, float defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null) {
            return Float.parseFloat(executeStatementAndReturn(sql, name));
        } else {
            return defaultValue;
        }
    }

    public static void start() {
        getConfigData();
        connect();
        if (ConfigManager.Enable_WAL_Mode) enableWALMode();
        createTable();
        disconnect();
    }
}