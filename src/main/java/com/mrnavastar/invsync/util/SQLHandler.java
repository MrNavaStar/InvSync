package com.mrnavastar.invsync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    public static Connection connection;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:C:/sqlite/JTP.db";

            connection = DriverManager.getConnection(url);
            log(Level.INFO,"Successfully connected to SQLite database!");

        } catch (SQLException | ClassNotFoundException e) {
            log(Level.ERROR, "Failed to connect to SQLite database!");
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
        connect();
    }
}