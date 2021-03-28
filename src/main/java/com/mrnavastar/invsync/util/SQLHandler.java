package com.mrnavastar.invsync.util;

import java.sql.*;

import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    private static String databaseName, tableName, databaseDirectory;
    private static StringBuilder columns;
    public static Connection connection;

    //Get config data from ConfigHandler
    private static void getConfigData() {
        databaseName = "InvSync.db";
        tableName = "PlayerData";
        databaseDirectory = "/var/lib/pufferpanel";
    }

    //Open connection to database
    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + databaseDirectory + "/" + databaseName;

            connection = DriverManager.getConnection(url);
            log(Level.INFO,"Successfully connected to database!");

        } catch (SQLException | ClassNotFoundException ignore) {
            log(Level.ERROR, "Failed to connect to database!");
        }
    }

    //Close connection to database
    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void enableWALMode() {
        String sql = "PRAGMA journal_mode=WAL;";
        executeStatement(sql);
    }

    //Run sql statement
    private static void executeStatement(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);

        } catch (SQLException ignore) {}
    }

    //Run sql statement and return a query as a string
    public static String executeStatementAndReturn(String sql, String name) {
        String result = null;
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            result = resultSet.getString(name);

        } catch (SQLException ignore) {}
        return result;
    }

    //Create columns for table
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
        columns.append("foodLevel").append(" INTEGER,");
        columns.append("saturation").append(" REAL");
    }

    //Create table if one does not exist
    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (uuid TEXT PRIMARY KEY, " + columns + ");";
        executeStatement(sql);
    }

    //Create row
    public static void createRow(String uuid) {
        String sql = "INSERT OR REPLACE INTO " + tableName + "(uuid) VALUES('" + uuid + "');";
        executeStatement(sql);
    }

    //Save item to row at name
    public static void saveItem(String uuid, String name, ItemStack itemStack) {
        String sql = "UPDATE " + tableName + " SET " + name + " = '" + ConversionHelpers.itemStackToString(itemStack) + "' WHERE uuid = '" + uuid + "'";
        executeStatement(sql);
    }

    //Save int to row at name
    public static void saveInt(String uuid, String name, int amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE uuid = '" + uuid + "'";
        executeStatement(sql);
    }

    //Save float to row at name
    public static void saveFloat(String uuid, String name, float amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE uuid = '" + uuid + "'";
        executeStatement(sql);
    }
    
    //Read item from row at name
    public static ItemStack loadItem(String uuid, String name) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null) {
            return ConversionHelpers.stringToItemStack(executeStatementAndReturn(sql, name));
        } else {
            return ConversionHelpers.stringToItemStack("{id:\"minecraft:air\",Count:1b}");
        }
    }

    //Read int from row at name
    public static int loadInt(String uuid, String name, int defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        if (executeStatementAndReturn(sql, name) != null) {
            return Integer.parseInt(executeStatementAndReturn(sql, name));
        } else {
            return defaultValue;
        }
    }

    //Read float from row at name
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
        enableWALMode();
        createColumns();
        createTable();
    }
}
