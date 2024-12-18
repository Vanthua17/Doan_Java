package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	 private static final String URL = "jdbc:mysql://localhost:3306/qldk_ktx";
	    private static final String USER = "root";
	    private static final String PASSWORD = "#Cccc0903";

	    public static Connection getConnection() {
	        Connection connection = null;
	        try {
	            // Load MySQL JDBC driver
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            // Get connection
	            connection = DriverManager.getConnection(URL, USER, PASSWORD);
	            System.out.println("Connection successful!");
	        } catch (ClassNotFoundException e) {
	            System.out.println("JDBC Driver not found!");
	            e.printStackTrace();
	        } catch (SQLException e) {
	            System.out.println("Connection failed!");
	            e.printStackTrace();
	        }
	        return connection;
	    }
	    public static void main(String[] args) {
	        System.out.println("Hello World!");
	        getConnection();
	    }
}
