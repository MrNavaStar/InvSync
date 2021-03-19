package arnaria.invsync.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHandler {

    public static void connectToSQL() {
        String url = "jdbc:mysql://192.168.1.87:3306/invsync";
        String username = "root";
        String password = "cichlid1";

        System.out.println("Connecting database...");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
        } catch (SQLException e) {
            System.out.println("Cannot connect the database!");
        }
    }
}
