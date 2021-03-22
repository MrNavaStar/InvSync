package com.mrnavastar.invsync.util;

import org.apache.logging.log4j.Level;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.mrnavastar.invsync.Invsync.log;

public class SQLHandler {

    private static String address, port, username, password, databaseName, table;
    public static Connection connection;

    public static void getConfigData() {
        databaseName = ConfigManager.SQL_Database_Name;
        table = ConfigManager.SQL_Database_Table_Name;
        username = ConfigManager.SQL_User;
        password = ConfigManager.SQL_Password;
        address = ConfigManager.SQL_Server_Address;
        port = ConfigManager.SQL_Server_Port;
    }

    public static void connectToSQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Properties SQLproperties = new Properties();
            SQLproperties.setProperty("user", username);
            SQLproperties.setProperty("password", password);
            SQLproperties.setProperty("autoReconnect", "true");
            SQLproperties.setProperty("verifyServerCertificate", "false");
            SQLproperties.setProperty("useSSL", "true");
            SQLproperties.setProperty("requireSSL", "true");

            connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + databaseName, SQLproperties);
            log(Level.INFO, "Successfully connected to database!");

        } catch (ClassNotFoundException e) {
            log(Level.ERROR, "Failed to load SQL drivers!");
        } catch (SQLException e) {
            log(Level.ERROR, "Failed to connect to SQL database!");
        }
    }

    public static void disconnectFromSQL() {
        try {
            log(Level.INFO, "Disconnecting from SQL database");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String table) {

    }

    public static void createRow(String rowID) {

    }

    public static void readTableRow(String row) {

    }

    public static void writeTableRow(String row) {

    }

    public static void start() {
        getConfigData();
        connectToSQL();
    }
}
