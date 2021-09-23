import com.mysql.cj.MysqlConnection;

import java.sql.*;

public class DBHandler {
//    static final String DB_URL = "jdbc:mysql://localhost:3306?";
//    static final String USER = "root";
//    static final String PASSWORD = "Zi26303y";
//    static final String DATABASE_NAME = "CLUSTERS";

    private String url;
    private String username;
    private String password;
    private Connection connection;
    private Statement statement;

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

    public void createDatabase(String databaseName, String tableName) {

        String sqlCreateDatabase = "CREATE DATABASE IF NOT EXISTS "
                + databaseName;

        String sqlSelectDatabase  = "USE " + databaseName;

        String sqlCreateTable = "CREATE TABLE "
                + tableName
//                + " (id INT AUTO_INCREMENT PRIMARY KEY,"
                + " (X DOUBLE,"
                + " Y DOUBLE,"
                + " Cluster TEXT)";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sqlCreateDatabase);
            statement.executeUpdate(sqlSelectDatabase);
            statement.executeUpdate(sqlCreateTable);
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }



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