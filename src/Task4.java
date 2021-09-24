import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;

/**
 * Author: Lyndon Foster.
 * Course: ITC313 - Programming in Java 2.
 * Assessment Title: Assessment Item 2, Task 4 - Build a Data Loader Application.
 * Date: September 26th, 2021.
 */
public class Task4 extends Application {
    static final String DB_NAME = "DataClustering";
    static final String TABLE_NAME = "Result";

    double x = 0;
    double y = 0;
    XYChart.Series cluster1 = new XYChart.Series();
    XYChart.Series cluster2 = new XYChart.Series();
    XYChart.Series cluster3 = new XYChart.Series();
    XYChart.Series cluster4 = new XYChart.Series();

    private Desktop desktop = Desktop.getDesktop();

    @Override
    public void start(Stage stage) throws IOException {
        Connection connection = getConnection();

        final FileChooser fileChooser = new FileChooser();
        final Button openButton = new Button("Open file...");
        openButton.setOnAction(
                e -> {
                    configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        // Optionally open the raw data in Windows.
                        // May be useful for comparing the chart and raw data.
//                        openFile(file);
                        try {
                            openFile(file, connection);

                        } catch(IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                });

        readData(connection);


        final NumberAxis xAxis = new NumberAxis(0,8,1);
        final NumberAxis yAxis = new NumberAxis(0,8,1);
        final ScatterChart<Number,Number> clusterDisplay = new ScatterChart<>(xAxis,yAxis);
        clusterDisplay.getData().addAll(cluster1, cluster2, cluster3, cluster4);
        clusterDisplay.setPrefSize(700,600);

        final VBox vBox = new VBox();
        final HBox  hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(openButton);
        vBox.getChildren().addAll(clusterDisplay, hBox);
        hBox.setPadding(new Insets(10,10,10,50));

        Scene scene = new Scene(new Group());
        ((Group)scene.getRoot()).getChildren().add(vBox);

        stage.setTitle("Cluster Display Samples");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void openFile(File file, Connection connection) throws IOException {

        // Try/catch block for file reader.
        try {
            // This can be simplified.
            // New FileReader, parse in the file object from the FileChooser.
            FileReader fileReader = new FileReader(file);
            // Parse the FileReader into a new BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            // Init empty string.
            String read;
            // Read the file line by line.
            while ((read = bufferedReader.readLine()) != null) {
                // Split the string by tab/whitespace.
                String[] row = read.split("\t");
                // The first index is the X value.
                String valX = row[0];
                // The second index is the Y value.
                String valY = row[1];
                // The name of the cluster the data belongs to is the third index.
                String cluster = row[2];
                try {
                    // Create a new PreparedStatment to insert these values into the MySQL DB.
                    // '?' are placeholders that can then be set to variables.
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO "
                            + DB_NAME
                            + "."
                            + TABLE_NAME
                            + " (X, Y, Cluster) VALUES (?, ?, ?)");
                    //
                    preparedStatement.setString(1, valX);
                    preparedStatement.setString(2, valY);
                    preparedStatement.setString(3, cluster);
                    // Execute the SQL statement.
                    preparedStatement.execute();
                    // Catch statement for any SQL errors.
                    }
                catch(SQLException e) {
                    e.printStackTrace();
                }
            }
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Operation Successful");
            success.setContentText("The data has been successfully added to the table.");
            success.show();
        }

        catch(Exception e) {
        e.printStackTrace();
        }
    }

    public void readData(Connection connection){
        cluster1.setName("Cluster 1");
        cluster2.setName("Cluster 2");
        cluster3.setName("Cluster 3");
        cluster4.setName("Cluster 4");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT x, y FROM "
                    + TABLE_NAME
                    + " WHERE "
                    + TABLE_NAME
                    + "."
                    + "cluster = ?");

            preparedStatement.setString(1, "Cluster1");
            ResultSet resultSetCluster1 = preparedStatement.executeQuery();
            while (resultSetCluster1.next()){
                cluster1.getData().add(new XYChart.Data(resultSetCluster1.getDouble(1), resultSetCluster1.getDouble(2)));
            }

            preparedStatement.setString(1, "Cluster2");
            ResultSet resultSetCluster2 = preparedStatement.executeQuery();
            while (resultSetCluster2.next()){
                cluster2.getData().add(new XYChart.Data(resultSetCluster2.getDouble(1), resultSetCluster2.getDouble(2)));
            }

            preparedStatement.setString(1, "Cluster3");
            ResultSet resultSetCluster3 = preparedStatement.executeQuery();
            while (resultSetCluster3.next()){
                cluster3.getData().add(new XYChart.Data(resultSetCluster3.getDouble(1), resultSetCluster3.getDouble(2)));
            }

            preparedStatement.setString(1, "Cluster4");
            ResultSet resultSetCluster4 = preparedStatement.executeQuery();
            while (resultSetCluster4.next()){
                cluster4.getData().add(new XYChart.Data(resultSetCluster4.getDouble(1), resultSetCluster4.getDouble(2)));
            }
        } catch(SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("An unknown SQL error occurred while trying to load the data.");
            alert.setTitle("SQL Error.");
            alert.show();
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        // This database setup can likely be its own function.
        // Create new DBHandler Object and specify the required parameters.
        DBHandler dbHandler = new DBHandler("jdbc:mysql://localhost:3306?","root","Zi26303y");
        // Call function to establish the connection.
        dbHandler.establishConnection();
        // Create local connection object from the DBHandler.
        Connection connection = dbHandler.getConnection();
        // Call function to create the database and table using the fields specified in this class.
        dbHandler.createDatabase(DB_NAME, TABLE_NAME);
        return connection;
    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("Select PIDC-O Data.");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*"),
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
    }

//    private void openFile(File file) {
//        try {
//            desktop.open(file);
//        } catch (IOException ex) {
//            Logger.getLogger(
//                    Task4.class.getName()).log(
//                    Level.SEVERE, null, ex
//            );
//        }
//    }


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