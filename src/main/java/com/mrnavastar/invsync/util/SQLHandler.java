package com.mrnavastar.invsync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;
import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    public static String databaseName, tableName, databaseDirectory, url;
    public static Connection connection;

    public static void getConfigData() {
        databaseName = "database.db";
        tableName = "PlayerData";
        databaseDirectory = "C:/sqlite";
    }

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            url = "jdbc:sqlite:C:" + databaseDirectory + "/" + databaseName;

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

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	capacity real\n"
                + ");";
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void start() {
        getConfigData();
        connect();
        createTable();
    }
}