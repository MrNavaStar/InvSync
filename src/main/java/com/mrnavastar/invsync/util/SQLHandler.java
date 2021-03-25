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
        databaseName = "database.db";
        tableName = "PlayerData";
        databaseDirectory = "C:/sqlite";
    }

    //Open connection to database
    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + databaseDirectory + "/" + databaseName;

            connection = DriverManager.getConnection(url);
            log(Level.INFO,"Successfully connected to database!");

        } catch (SQLException | ClassNotFoundException e) {
            log(Level.ERROR, "Failed to connect to database!");
            e.printStackTrace();
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

    //Run sql statement
    private static void executeStatement(String sql) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        for (int i = 0; i < 28; i++) {
            columns.append("eChest").append(i).append(" TEXT,");
        }

        columns.append("xp").append(" INTEGER,");
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

    //Save item to row at name + index
    public static void saveItem(String uuid, String name, int index, ItemStack itemStack) {
        String sql = "UPDATE " + tableName + " SET " + name + index + " = '" + ConversionHelpers.itemstackToString(itemStack) + "' WHERE uuid = '" + uuid + "'";
        executeStatement(sql);
        System.out.println(sql);
    }

    //Save item to row at name
    public static void saveItem(String uuid, String name, ItemStack itemStack) {
        String sql = "UPDATE " + tableName + " SET " + name + " = '" + ConversionHelpers.itemstackToString(itemStack) + "' WHERE uuid = '" + uuid + "'";
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

    public static void start() {
        getConfigData();
        connect();
        createColumns();
        createTable();
    }
}