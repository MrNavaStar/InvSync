package com.mrnavastar.invsync.sql;

import com.mrnavastar.invsync.setup.ConfigManager;
import org.apache.logging.log4j.Level;

import java.sql.*;

import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    private static final String databaseName = ConfigManager.Database_Name;
    private static final String databaseDirectory = ConfigManager.Database_Directory;
    public static Connection connection;

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
        return executeStatement("SELECT " + column + " FROM " + tableName);
    }

    public static void createTable(String tableName) {
        executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (id TEXT PRIMARY KEY)");
    }

    public static void dropTable(String tableName) {
        executeStatement("DROP TABLE IF EXISTS " + tableName);
    }

    public static void createRow(String tableName, String id, String value) {
        executeStatement("INSERT OR REPLACE INTO " + tableName + "(" + id + ") VALUES('" + value + "');");
    }

    public static void saveString(String tableName, String where, String name, String str) {
        executeStatement("UPDATE " + tableName + " SET " + name + " = '" + str + "' WHERE id = '" + where + "'");
    }

    public static void saveInt(String tableName, String where, String name, int amount) {
        executeStatement("UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE id = '" + where + "'");
    }

    public static void saveFloat(String tableName, String where, String name, float amount) {
        executeStatement("UPDATE " + tableName + " SET " + name + " = " + amount + " WHERE id = '" + where + "'");
    }

    public static String loadString(String tableName, String where, String name, String defaultValue) {
        ResultSet resultSet = executeStatementAndReturn("SELECT " + name + " FROM " + tableName + " WHERE id = '" + where + "'");
        String data = defaultValue;
        if (resultSet != null) {
            try {
                data = resultSet.getString(name);
            } catch (SQLException ignore) {}
        }
        return data;
    }

    public static int loadInt(String tableName, String where, String name, int defaultValue) {
        ResultSet resultSet = executeStatementAndReturn("SELECT " + name + " FROM " + tableName + " WHERE id = '" + where + "'");
        int data = defaultValue;
        if (resultSet != null) {
            try {
                data = Integer.parseInt(resultSet.getString(name));
            } catch (SQLException ignore) {}
        }
        return data;
    }

    public static float loadFloat(String tableName, String where, String name, float defaultValue) {
        ResultSet resultSet = executeStatementAndReturn("SELECT " + name + " FROM " + tableName + " WHERE id = '" + where + "'");
        float data = defaultValue;
        if (resultSet != null) {
            try {
                data = Float.parseFloat(resultSet.getString(name));
            } catch (SQLException ignore) {}
        }
        return data;
    }
}