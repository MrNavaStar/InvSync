package com.mrnavastar.invsync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    public static String databaseName, tableName, databaseDirectory;
    public static Connection connection;

    public static void getConfigData() {
        databaseName = ConfigManager.SQL_Database_Name;
        tableName = ConfigManager.SQL_Database_Table_Name;
        databaseDirectory = ConfigManager.SQL_Database_Directory;
    }

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:C:/sqlite/JTP.db";

            connection = DriverManager.getConnection(url);
            log(Level.INFO,"Successfully connected to database!");

        } catch (SQLException | ClassNotFoundException e) {
            log(Level.ERROR, "Failed to connect to database!");
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void start() {
        getConfigData();
        connect();
    }
}