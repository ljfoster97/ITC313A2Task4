import com.mysql.cj.MysqlConnection;

import java.sql.*;

public class DBEngine {
//    static final String DB_URL = "jdbc:mysql://localhost:3306?";
//    static final String USER = "root";
//    static final String PASSWORD = "Zi26303y";
//    static final String DATABASE_NAME = "CLUSTERS";

    private String url;
    private String username;
    private String password;
    private MysqlConnection mysqlConnection;

    public DBEngine(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void establishConnection(){
        try {
            mysqlConnection = (MysqlConnection) DriverManager.getConnection(this.url, this.username, this.password);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){

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
//                    + " (id INTEGER not NULL,"
//                    + " X INTEGER,"
//                    + " Y INTEGER,"
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