package com.mrnavastar.invsync.sql;

import java.io.*;
import java.sql.*;
import java.util.Arrays;

import com.mrnavastar.invsync.util.ConfigManager;
import org.apache.commons.io.FileUtils;
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
        executeStatement("PRAGMA journal_mode=WAL;");
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

    public static ResultSet executeStatementAndReturn(String sql) {
        ResultSet resultSet = null;
        try {
            Statement stmt = connection.createStatement();
            resultSet = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static boolean columnExists (String tableName, String column) {
        String sql = "SELECT " + column + " FROM " + tableName;
        return executeStatement(sql);
    }

    public static void createTable(String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id TEXT PRIMARY KEY)";
        executeStatement(sql);
    }

    public static void dropTable(String tableName) {
        String sql = "DROP TABLE " + tableName;
        executeStatement(sql);
    }

    public static void createRow(String tableName, String id, String value) {
        String sql = "INSERT OR REPLACE INTO " + tableName + "(" + id + ") VALUES('" + value + "');";
        executeStatement(sql);
    }

    public static void saveString(String tableName, String where, String name, String str) {
        String sql = "UPDATE " + tableName + " SET " + name + " = '" + str + "' WHERE id = '" + where + "'";
        executeStatement(sql);
    }

    public static void saveInt(String tableName, String where, String name, int amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE id = '" + where + "'";
        executeStatement(sql);
    }

    public static void saveFloat(String tableName, String where, String name, float amount) {
        String sql = "UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE id = '" + where + "'";
        executeStatement(sql);
    }

    public static void saveFile(String tableName, String where, String name, File file) {
        try {
            String sql = "UPDATE " + tableName + " SET " + name + " = '" + Arrays.toString(FileUtils.readFileToByteArray(file)) + "' WHERE id = '" + where + "'";
            executeStatement(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadString(String tableName, String where, String name, String defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE id = '" + where + "'";
        ResultSet resultSet = executeStatementAndReturn(sql);
        String data = defaultValue;
        if (resultSet != null) {
            try {
                data = resultSet.getString(name);
            } catch (SQLException ignore) {}
        }
        return data;
    }

    public static int loadInt(String tableName, String where, String name, int defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE id = '" + where + "'";
        ResultSet resultSet = executeStatementAndReturn(sql);
        int data = defaultValue;
        if (resultSet != null) {
            try {
                data = Integer.parseInt(resultSet.getString(name));
            } catch (SQLException ignore) {}
        }
        return data;
    }

    public static float loadFloat(String tableName, String where, String name, float defaultValue) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE id = '" + where + "'";
        ResultSet resultSet = executeStatementAndReturn(sql);
        float data = defaultValue;
        if (resultSet != null) {
            try {
                data = Float.parseFloat(executeStatementAndReturn(sql).getString(name));
            } catch (SQLException ignore) {}
        }
        return data;
    }

    public static byte[] loadFile(String tableName, String where, String name) {
        String sql = "SELECT " + name + " FROM " + tableName + " WHERE id = '" + where + "'";
        ResultSet resultSet = executeStatementAndReturn(sql);
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            if (resultSet != null && !resultSet.getString(name).equals("[]")) {

                byteArrayOutputStream = new ByteArrayOutputStream();
                String[] bytesAsStr = resultSet.getString(name).replace("[", "").replace("]", "").split(", ");


                for (String s : bytesAsStr) {
                    byteArrayOutputStream.write(new byte[]{Byte.parseByte(s)}, 0, 1);
                }
            }
        } catch (SQLException ignore) {}

        if (byteArrayOutputStream != null) {
            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }
    }

    public static void start() {
        getConfigData();
        connect();
        if (ConfigManager.Enable_WAL_Mode) enableWALMode();
        disconnect();
    }
}
