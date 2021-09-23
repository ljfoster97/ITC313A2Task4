import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;


public class Task4 extends Application {
    static final String DB_NAME = "ITC313";
    static final String TABLE_NAME = "clusters";

    double x = 0;
    double y = 0;
    XYChart.Series cluster1 = new XYChart.Series();
    XYChart.Series cluster2 = new XYChart.Series();
    XYChart.Series cluster3 = new XYChart.Series();
    XYChart.Series cluster4 = new XYChart.Series();

    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("Cluster Display Samples");
        final NumberAxis xAxis = new NumberAxis(0,8,1);
        final NumberAxis yAxis = new NumberAxis(0,8,1);
        final ScatterChart<Number,Number> clusterDisplay = new
                ScatterChart<>(xAxis,yAxis);
        xAxis.setLabel("X Coordinate");
        yAxis.setLabel("Y Coordinate");
        clusterDisplay.setTitle("Cluster Display");

        readFile();

        clusterDisplay.getData().addAll(cluster1, cluster2, cluster3, cluster4);
        Scene scene = new Scene(clusterDisplay, 700, 600);
        stage.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void readFile() throws IOException {
        DBHandler dbHandler = new DBHandler("jdbc:mysql://localhost:3306?","root","Zi26303y");
        dbHandler.establishConnection();
        Connection connection = dbHandler.getConnection();
        dbHandler.createDatabase(DB_NAME, TABLE_NAME);

        try {
            FileReader file = new FileReader("Cluster.txt");
            BufferedReader bufferedReader = new BufferedReader(file);
            String read;
            while ((read = bufferedReader.readLine()) != null) {
                String[] row = read.split("\t");
                String valX = row[0];
                String valY = row[1];
                String cluster = row[2];
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + DB_NAME + "." + TABLE_NAME + " (X, Y, Cluster) VALUES (?, ?, ?)");
                    preparedStatement.setString(1, valX);
                    preparedStatement.setString(2, valY);
                    preparedStatement.setString(3, cluster);
                    preparedStatement.execute();
                    } catch(SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            }
            catch(Exception e) {
            e.printStackTrace();
        }
    }


//    public void readFile() throws FileNotFoundException {
//        File file = new File("Cluster.txt");
//        try {
//            Scanner sc = new Scanner(file);
//            cluster1.setName("Cluster 1");
//            cluster2.setName("Cluster 2");
//            cluster3.setName("Cluster 3");
//            cluster4.setName("Cluster 4");
//
//            sc.nextLine();
//            while (sc.hasNextLine()) {
//                String dataSeries = sc.nextLine();
//                if (dataSeries.contains("Cluster1")) {
//
//                    String[] data = dataSeries.split("\t");
//                    x = Double.parseDouble(data[0]);
//                    y = Double.parseDouble(data[1]);
//                    cluster1.getData().add(new XYChart.Data(x, y));
//                } else if (dataSeries.contains("Cluster2")) {
//                    String[] data = dataSeries.split("\t");
//                    x = Double.parseDouble(data[0]);
//                    y = Double.parseDouble(data[1]);
//                    cluster2.getData().add(new XYChart.Data(x, y));
//                } else if (dataSeries.contains("Cluster3")) {
//                    String[] data = dataSeries.split("\t");
//                    x = Double.parseDouble(data[0]);
//                    y = Double.parseDouble(data[1]);
//                    cluster3.getData().add(new XYChart.Data(x, y));
//                } else {
//                    if (dataSeries.contains("Cluster4")) {
//                        String[] data = dataSeries.split("\t");
//                        x = Double.parseDouble(data[0]);
//                        y = Double.parseDouble(data[1]);
//                        cluster4.getData().add(new XYChart.Data(x, y));
//                    }
//                }
//                System.out.println(dataSeries);
//            }
//        } catch (IOException e) {
//            System.err.println("IOException: " + e.getMessage());
//        }
//    }


    public static void main(String[] args) throws FileNotFoundException {

        launch(args);
    }
}