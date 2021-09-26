import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Author: Lyndon Foster.
 * Course: ITC313 - Programming in Java 2.
 * Assessment Title: Assessment Item 2, Task 4 - Build a Data Loader Application.
 * Date: September 26th, 2021.
 */

// Object for encapsulation of database functions.
public class DBHandler {

    // Fields.
    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    // Constructor.
    public DBHandler(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    // Calls getConnection method from DriverManager class and parses in this objects fields as parameters.
    public void establishConnection() {
        try {
            connection = DriverManager.getConnection(this.url, this.username, this.password);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // Provides a usable connection for performing higher level database functions.
    public Connection getConnection() {
        return this.connection;
    }

    // Closes the connection to the database.
    public void closeConnection() {
        try {
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // Ideally for this DBHandler Class there would be dedicated functions for creating
    // and updating schemas and tables on the MySQL server.
    // Given the limited scope of this task, it makes more sense to have a
    // single function that can create the initial schema and results table.
    // However this could easily be expanded to allow for multiple tables so
    // that data isn't being overwritten each time the raw data is read in.
    public void createDatabase(String databaseName, String tableName) throws Exception {
        String sqlCreateDatabase = "CREATE DATABASE IF NOT EXISTS "
                + databaseName;

        String sqlSelectDatabase = "USE " + databaseName;

        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS "
                + tableName
                //                + " (id INT AUTO_INCREMENT PRIMARY KEY,"
                + " (X DOUBLE,"
                + " Y DOUBLE,"
                + " Cluster TEXT)";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlCreateDatabase);
            System.out.println("Created database: " + databaseName);
            statement.executeUpdate(sqlSelectDatabase);
            System.out.println("Selected database: " + databaseName);
            statement.executeUpdate(sqlCreateTable);
            System.out.println("Created table: " + tableName);
        } catch(SQLException e) {
            // Raise any exception the class that called the method.
            throw new Exception();
        }
    }
}