package com.mrnavastar.invsync.util;

import java.sql.*;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    private static String databaseName, tableName, databaseDirectory;
    private static final ArrayList<String> columnsTotal = new ArrayList<>();
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
        boolean executed = false;
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            executed = true;

        } catch (SQLException ignore) {}
        return executed;
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

    private static void manageColumns() {
        ArrayList<Column> columnsAdd = new ArrayList<>();

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                if (!columnExists("inv0")) columnsAdd.add(new Column("inv" + i, "TEXT"));
                columnsTotal.add("inv" + i);
            }
            if (!columnExists("offHand")) columnsAdd.add(new Column("offHand", "TEXT"));
            if (!columnExists("selectedSlot")) columnsAdd.add(new Column("selectedSlot", "INTEGER"));
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                if (!columnExists("armour0")) columnsAdd.add(new Column("armour" + i, "TEXT"));
                columnsTotal.add("armour" + i);
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                if (!columnExists("eChest0")) columnsAdd.add(new Column("eChest" + i, "TEXT"));
                columnsTotal.add("eChest" + i);
            }
        }

        if (ConfigManager.Sync_Xp) {
            if (!columnExists("xp")) {
                columnsAdd.add(new Column("xp", "INTEGER"));
                columnsAdd.add(new Column("xpProgress", "REAL"));
            }
            columnsTotal.add("xp");
            columnsTotal.add("xpProgress");
        }

        if (ConfigManager.Sync_Score) {
            if (!columnExists("score")) columnsAdd.add(new Column("score", "INTEGER"));
            columnsTotal.add("score");
        }

        if (ConfigManager.Sync_Health) {
            if (!columnExists("health")) columnsAdd.add(new Column("health", "REAL"));
            columnsTotal.add("health");
        }

        if (ConfigManager.Sync_Food_Level) {
            if (!columnExists("foodLevel")) columnsAdd.add(new Column("foodLevel", "TEXT"));
            columnsTotal.add("foodLevel");
        }

        if (ConfigManager.Sync_Status_Effects) {
            if (!columnExists("statusEffects")) columnsAdd.add(new Column("statusEffects", "TEXT"));
            columnsTotal.add("statusEffects");
        }

        for (Column c : columnsAdd) {
            String sql = "ALTER TABLE " + tableName + " ADD " + c.getName() + " " + c.getType();
            executeStatement(sql);
        }
    }

    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (uuid TEXT PRIMARY KEY)";
        executeStatement(sql);
        manageColumns();
    }

    public static void createRow(String uuid) {
        String sql = "INSERT OR REPLACE INTO " + tableName + "(uuid) VALUES('" + uuid + "');";
        executeStatement(sql);
    }

    public static void saveString(String uuid, String name, String str) {
        String sql = "UPDATE " + tableName + " SET " + name + " = '" + str + "' WHERE uuid = '" + uuid + "'";
        if (columnsTotal.contains(name)) executeStatement(sql);
    }

    public static void saveInt(String uuid, String name, int amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE uuid = '" + uuid + "'";
        if (columnsTotal.contains(name)) executeStatement(sql);
    }

    public static void saveFloat(String uuid, String name, float amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE uuid = '" + uuid + "'";
        if (columnsTotal.contains(name)) executeStatement(sql);
    }

    public static String loadString(String uuid, String name, String defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null && columnsTotal.contains(name)) {
            return executeStatementAndReturn(sql, name);
        } else {
            return defaultValue;
        }
    }

    public static int loadInt(String uuid, String name, int defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null && columnsTotal.contains(name)) {
            return Integer.parseInt(executeStatementAndReturn(sql, name));
        } else {
            return defaultValue;
        }
    }

    public static float loadFloat(String uuid, String name, float defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null && columnsTotal.contains(name)) {
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