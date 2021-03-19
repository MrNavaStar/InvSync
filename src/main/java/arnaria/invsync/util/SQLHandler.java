package arnaria.invsync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHandler {

    private static String url, username, password, table;

    public static void connectToSQL() {
        System.out.println("Connecting database...");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
        } catch (SQLException e) {
            System.out.println("Cannot connect the database!");
        }
    }

    public static void getConfigData() {
        String databaseName = ConfigManager.SQL_Database_Name;
        table = ConfigManager.SQL_Database_Table_Name;
        String address = ConfigManager.SQL_Server_Address;
        String port = ConfigManager.SQL_Server_Port;
        username = ConfigManager.SQL_User;
        password = ConfigManager.SQL_Password;

        url = "jdbc:mysql://" + address + ":" + port + "/" + databaseName;
    }

    public static void start() {
        getConfigData();
        connectToSQL();
    }

    public static void createTable() {
        
    }

    public static void createRow() {

    }

    public static void readTableRow(String row) {

    }

    public static void writeTableRow(String row) {

    }
}
