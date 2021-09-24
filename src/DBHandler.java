import com.mysql.cj.MysqlConnection;
import javafx.scene.control.Alert;

import java.sql.*;

/**
 * Author: Lyndon Foster.
 * Course: ITC313 - Programming in Java 2.
 * Assessment Title: Assessment Item 2, Task 4 - Build a Data Loader Application.
 * Date: September 26th, 2021.
 */
public class DBHandler {
//    static final String DB_URL = "jdbc:mysql://localhost:3306?";
//    static final String USER = "root";
//    static final String PASSWORD = "Zi26303y";
//    static final String DATABASE_NAME = "CLUSTERS";

    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    public DBHandler(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void establishConnection(){
        try {
            connection = DriverManager.getConnection(this.url, this.username, this.password);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return this.connection;
    }

    // Ideally for this DBHandler Class there would be dedicated functions for creating
    // and updating schemas and tables on the MySQL server.
    // Given the limited scope of this task, it makes more sense to have a
    // single function that can create the initial schema and results table.
    public void createDatabase(String databaseName, String tableName) {

        String sqlCreateDatabase = "CREATE DATABASE IF NOT EXISTS "
                + databaseName;

        String sqlSelectDatabase  = "USE " + databaseName;

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
        }
        catch(SQLException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("SQL Error.");
            alert.setContentText("An unexpected SQL error occurred, but the operation was still completed successfully." +
                    "Some data may be duplicated or missing.");
            alert.show();
            e.printStackTrace();
        }
    }

//    public void createTable(String tableName) {
//        String sqlCreateTable = "CREATE TABLE "
//                + tableName
//                //                + " (id INT AUTO_INCREMENT PRIMARY KEY,"
//                + " (X DOUBLE,"
//                + " Y DOUBLE,"
//                + " Cluster TEXT)";
//    }



//    public static void main(String[] args) {
//
//        // Open a connection
//        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
//             Statement statement = connection.createStatement()) {
//
//            String sqlCreateDatabase = "CREATE DATABASE IF NOT EXISTS "
//                    + DATABASE_NAME;
//
//            String sqlSelectDatabase  = "USE " + DATABASE_NAME;
//
//            String sqlCreateTable = "CREATE TABLE DataClustering"
//                    + " (id INT AUTO_INCREMENT PRIMARY KEY,"
//                    + " X DOUBLE,"
//                    + " Y DOUBLE,"
//                    + " Cluster TEXT,"
//                    + " PRIMARY KEY ( id ))";
//
//
//            statement.executeUpdate(sqlCreateDatabase);
//            statement.executeUpdate(sqlSelectDatabase);
//            statement.executeUpdate(sqlCreateTable);
//            System.out.println("Created table in given database...");
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }
//    }
}