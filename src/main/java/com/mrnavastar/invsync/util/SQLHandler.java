package com.mrnavastar.invsync.util;

import java.sql.*;

import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    private static String databaseName, tableName, databaseDirectory;
    private static StringBuilder columns;
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

    private static void executeStatement(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);

        } catch (SQLException ignore) {}
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

    private static void createColumns() {
        columns = new StringBuilder();

        for (int i = 0; i < 36; i++) {
            columns.append("inv").append(i).append(" TEXT,");
        }
        columns.append("offHand").append(" TEXT,");

        for (int i = 0; i < 4; i++) {
            columns.append("armour").append(i).append(" TEXT,");
        }

        for (int i = 0; i < 27; i++) {
            columns.append("eChest").append(i).append(" TEXT,");
        }

        columns.append("xp").append(" INTEGER,");
        columns.append("xpProgress").append(" REAL,");
        columns.append("score").append(" INTEGER,");
        columns.append("health").append(" REAL,");
        columns.append("foodLevel").append(" INTEGER");
        //columns.append("saturation").append(" REAL");
    }

    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (uuid TEXT PRIMARY KEY, " + columns + ");";
        executeStatement(sql);
    }

    public static void createRow(String uuid) {
        String sql = "INSERT OR REPLACE INTO " + tableName + "(uuid) VALUES('" + uuid + "');";
        executeStatement(sql);
    }

    public static void saveItem(String uuid, String name, ItemStack itemStack) {
        String sql = "UPDATE " + tableName + " SET " + name + " = '" + ConversionHelpers.itemStackToString(itemStack) + "' WHERE uuid = '" + uuid + "'";
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

    public static ItemStack loadItem(String uuid, String name) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null) {
            return ConversionHelpers.stringToItemStack(executeStatementAndReturn(sql, name));
        } else {
            return ConversionHelpers.stringToItemStack("{id:\"minecraft:air\",Count:1b}");
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
        createColumns();
        createTable();
        SQLHandler.disconnect();
    }
}