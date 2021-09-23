import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;


public class Task4 extends Application {

    double x = 0;
    double y = 0;
    XYChart.Series cluster1 = new XYChart.Series();
    XYChart.Series cluster2 = new XYChart.Series();
    XYChart.Series cluster3 = new XYChart.Series();
    XYChart.Series cluster4 = new XYChart.Series();

    @Override
    public void start(Stage stage) throws FileNotFoundException {

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

    public void readFile() throws FileNotFoundException {
        File file = new File("Cluster.txt");
        try {
            Scanner sc = new Scanner(file);
            cluster1.setName("Cluster 1");
            cluster2.setName("Cluster 2");
            cluster3.setName("Cluster 3");
            cluster4.setName("Cluster 4");

            sc.nextLine();
            while (sc.hasNextLine()) {
                String dataSeries = sc.nextLine();
                if (dataSeries.contains("Cluster1")) {

                    String[] data = dataSeries.split("\t");
                    x = Double.parseDouble(data[0]);
                    y = Double.parseDouble(data[1]);
                    cluster1.getData().add(new XYChart.Data(x, y));
                } else if (dataSeries.contains("Cluster2")) {
                    String[] data = dataSeries.split("\t");
                    x = Double.parseDouble(data[0]);
                    y = Double.parseDouble(data[1]);
                    cluster2.getData().add(new XYChart.Data(x, y));
                } else if (dataSeries.contains("Cluster3")) {
                    String[] data = dataSeries.split("\t");
                    x = Double.parseDouble(data[0]);
                    y = Double.parseDouble(data[1]);
                    cluster3.getData().add(new XYChart.Data(x, y));
                } else {
                    if (dataSeries.contains("Cluster4")) {
                        String[] data = dataSeries.split("\t");
                        x = Double.parseDouble(data[0]);
                        y = Double.parseDouble(data[1]);
                        cluster4.getData().add(new XYChart.Data(x, y));
                    }
                }
                System.out.println(dataSeries);
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }


    public static void main(String[] args) throws FileNotFoundException {

        launch(args);
    }
}