package com.mrnavastar.invsync.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;

import com.mrnavastar.invsync.column.PlayerDataColumns;
import org.apache.logging.log4j.Level;

import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    private static String databaseName, databaseDirectory;
    public static Connection connection;

    private static void getConfigData() {
        databaseName = ConfigManager.Database_Name;
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

    public static boolean executeStatement(String sql) {
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

    public static boolean columnExists (String tableName, String column) {
        String sql = "SELECT " + column + " FROM " + tableName;
        return executeStatement(sql);
    }

    public static void createTable(String tableName, String primaryKey, String type) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + primaryKey + " " + type + " PRIMARY KEY)";
        executeStatement(sql);
    }

    public static void createRow(String name, String value, String tableName) {
        String sql = "INSERT OR REPLACE INTO " + tableName + "(" + name + ") VALUES('" + value + "');";
        executeStatement(sql);
    }

    public static void saveString(String tableName, ArrayList<String> columnsTotal, String where, String name, String str) {
        String sql = "UPDATE " + tableName + " SET " + name + " = '" + str + "' WHERE uuid = '" + where + "'";
        if (columnsTotal.contains(name)) executeStatement(sql);
    }

    public static void saveInt(String tableName, ArrayList<String> columnsTotal, String where, String name, int amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE uuid = '" + where + "'";
        if (columnsTotal.contains(name)) executeStatement(sql);
    }

    public static void saveFloat(String tableName, ArrayList<String> columnsTotal, String where, String name, float amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE uuid = '" + where + "'";
        if (columnsTotal.contains(name)) executeStatement(sql);
    }

    public static void saveFile(String tableName, String where, String name, File file) {
        String sql = "UPDATE " + tableName + " SET " + name + " = ? " + "WHERE type = ?";
        System.out.println(sql);
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setBlob(1, new FileInputStream(file));
            pstmt.setString(2, where);
            pstmt.executeUpdate();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String loadString(String tableName, ArrayList<String> columnsTotal, String where, String name, String defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + where + "'";
        if (executeStatementAndReturn(sql, name) != null && columnsTotal.contains(name)) {
            return executeStatementAndReturn(sql, name);
        } else {
            return defaultValue;
        }
    }

    public static int loadInt(String tableName, ArrayList<String> columnsTotal, String where, String name, int defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + where + "'";
        if (executeStatementAndReturn(sql, name) != null && columnsTotal.contains(name)) {
            return Integer.parseInt(executeStatementAndReturn(sql, name));
        } else {
            return defaultValue;
        }
    }

    public static float loadFloat(String tableName, ArrayList<String> columnsTotal, String where, String name, float defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE uuid = '" + where + "'";
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
        createTable(ConfigManager.Database_Table_Name, "uuid", "TEXT");
        PlayerDataColumns.manageColumns();
        disconnect();
    }
}