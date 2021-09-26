import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Author: Lyndon Foster.
 * Course: ITC313 - Programming in Java 2.
 * Assessment Title: Assessment Item 2, Task 4 - Build a Data Loader Application,
 * Task 5 - Display the Records of Clusters.
 * <p>
 * Date: September 26th, 2021.
 */
public class Task4 extends Application {
    // Field for database and table creation.
    // Possible to have user input instead.
    static final String DB_NAME = "DataClustering";
    static final String TABLE_NAME = "Result";

    // Init XYChart.Series for each cluster in the dataset.
    XYChart.Series cluster1 = new XYChart.Series();
    XYChart.Series cluster2 = new XYChart.Series();
    XYChart.Series cluster3 = new XYChart.Series();
    XYChart.Series cluster4 = new XYChart.Series();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Get DBHandler.
        // This could be initialized as a field for global access but for encapsulation this seems like best practice.
        DBHandler dbHandler = Utilities.setupDatabaseConnection(DB_NAME, TABLE_NAME);
        // Set up FileChooser.
        final FileChooser fileChooser = new FileChooser();
        // Call function to set up the FileChooser.
        Utilities.configureFileChooser(fileChooser);
        // Create a new button for opening the clusters.txt in explorer.
        final Button openButton = new Button("Open file...");
        // Set up the button actions.
        openButton.setOnAction(
                e -> {
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        // Optionally open the raw data in Windows.
                        // May be useful for comparing the chart and raw data.
                        //                        openFile(file);
                        openFile(file, dbHandler);
                        // Call loadData to read the data from the DB and display it on the chart.
                        // This means that every time a file is read in, the display is immediately refreshed.
                        try {
                            loadData(dbHandler);
                        } catch(Exception e1) {
                            Utilities.showDialogWindow(Alert.AlertType.ERROR, "An unknown error occured while trying to display the data.",
                                    "Corrupt database.",
                                    "Unknown Error.");
                        }

                    }
                });
        // Call function to load data any previously existing data from the table.
        loadData(dbHandler);
        // Setting range based on the data set.
        // This should be done dynamically based on the values in the dataset.
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        // Set up ScatterChart.
        final ScatterChart<Number, Number> clusterDisplay = new ScatterChart<>(xAxis, yAxis);
        // add XYChart.Series to the ScatterChart.
        clusterDisplay.getData().addAll(cluster1, cluster2, cluster3, cluster4);
        clusterDisplay.setPrefSize(700, 600);

        // VBox to stack the chart and buttons.
        final VBox vBox = new VBox();
        // HBox to hold the btutons.
        final HBox hBox = new HBox();
        hBox.setSpacing(10);
        // Add button to horizontal box.
        hBox.getChildren().addAll(openButton);
        // Add the cluster chart and the HBox to the VBox.
        vBox.getChildren().addAll(clusterDisplay, hBox);
        // Padding to space the button and window border.
        hBox.setPadding(new Insets(10, 10, 10, 50));
        // Create a new group, add it to a new scene.
        Scene scene = new Scene(new Group());
        // Add the VBox to the scene.
        ((Group) scene.getRoot()).getChildren().add(vBox);
        // Set window title.
        stage.setTitle("Clusters");
        stage.setResizable(false);
        // Add scene to stage and display.
        stage.setScene(scene);
        stage.show();
    }

    // Function to read the clusters.txt line by line and add data into the MySQL Database.
    public void openFile(File file, DBHandler dbHandler) {
        boolean error = false;
        String valX = "";
        String valY = "";
        String cluster = "";
        // Get connection, alternatively could parse the connection as a parameter to this function,
        // but this seems like better practice/encapsulation.
        // I think that it's better to parse the DBHandler to functions that require it,
        // rather than having it globally accessible.
        Connection connection = dbHandler.getConnection();
        // Try/catch block for file reader.
        try {
            // Set up BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            // Skip the header line.
            bufferedReader.readLine();
            // Init empty string.
            String read = null;
            // Read the file line by line.
            while ((read = bufferedReader.readLine()) != null) {

                try {
                    // Split the string by tab/whitespace.
                    String[] row = read.split("\t");
                    // The first index is the X value.
                    valX = row[0];
                    // The second index is the Y value.
                    valY = row[1];
                    // The name of the cluster the data belongs to is the third index.
                    cluster = row[2];
                } catch(Exception e) {
                    Utilities.showDialogWindow(Alert.AlertType.ERROR,
                            "An unknown SQL error occurred while trying to load the data." +
                                    "\nPlease verify that the correct PIDC-0 Data was selected.",
                            "SQL Error.",
                            "Operation Failed.");
                    error = true;
                    e.printStackTrace();
                    break;
                }
                try {
                    // Create a new PreparedStatement to insert these values into the MySQL DB.
                    // '?' are placeholders that can then be set to variables.
                    // This could just be a function within the DBHandler class itself.
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO "
                                    + DB_NAME
                                    + "."
                                    + TABLE_NAME
                                    + " (X, Y, Cluster) VALUES (?, ?, ?)");
                    // Parse variables into the SQL statement.
                    preparedStatement.setString(1, valX);
                    preparedStatement.setString(2, valY);
                    preparedStatement.setString(3, cluster);
                    // Execute the SQL statement.
                    preparedStatement.execute();
                    // Catch statement for any SQL errors.
                } catch(SQLException e) {
                    // Catch displays alert window.
                    // From testing it's most likely for errors to occur when schema/table already
                    // exists or the data is corrupted/not formatted properly.
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("SQL Error.");
                    alert.setContentText("An unexpected SQL error occurred, but the operation was still completed successfully." +
                            "Some data may be duplicated or missing.");
                    alert.show();
                    e.printStackTrace();
                }
            }
            if (! error) {
                // Confirmation message if no errors occur.
                Utilities.showDialogWindow(Alert.AlertType.INFORMATION,
                        "The data has been successfully added to the database.",
                        "Data Processed.",
                        "Operation Successful.");
            }
        } catch(Exception e) {
            Utilities.showDialogWindow(Alert.AlertType.ERROR,
                    "An unknown SQL error occurred while trying to load the data." +
                            "Please verify that the correct PIDC-0 Data was selected.",
                    "SQL Error.",
                    "Operation Failed.");
            e.printStackTrace();
        }
    }

    // Function to read data in from the database and display it on the XYChart.
    public void loadData(DBHandler dbHandler) {
        // Get connection, alternatively could parse the connection as a parameter to this function,
        // but this seems like better practice/encapsulation
        // as it would allow the connection to be properly closed via DBHandler.closeConnection() method
        // if expanding the functionality of the program.
        Connection connection = dbHandler.getConnection();
        // Set labels for each cluster.
        cluster1.setName("Cluster 1");
        cluster2.setName("Cluster 2");
        cluster3.setName("Cluster 3");
        cluster4.setName("Cluster 4");
        // Try/catch block for PreparedStatement.
        // Ideally this could be generified into a function within the DBHandler.
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT x, y FROM "
                    + TABLE_NAME
                    + " WHERE "
                    + TABLE_NAME
                    + "."
                    + "cluster = ?");
            // Specify which cluster to get data for.
            preparedStatement.setString(1, "Cluster1");
            // Create a new ResultSet from the result of the SQL query.
            ResultSet resultSetCluster1 = preparedStatement.executeQuery();
            // Iterate through the result set.
            while (resultSetCluster1.next()) {
                // Add the X and Y values from the ResultSet to the XYChart.Series.
                cluster1.getData().add(new XYChart.Data(resultSetCluster1.getDouble(1), resultSetCluster1.getDouble(2)));
            }

            // Repeat the above steps for each cluster.
            // There is likely a more efficient way to do this instead of repeating similar code for each cluster.
            // The outline specified that these data sets only ever have 4 clusters though, so this is easier
            // than making the amount of clusters dynamic.
            preparedStatement.setString(1, "Cluster2");
            ResultSet resultSetCluster2 = preparedStatement.executeQuery();
            while (resultSetCluster2.next()) {
                cluster2.getData().add(new XYChart.Data(resultSetCluster2.getDouble(1), resultSetCluster2.getDouble(2)));
            }

            preparedStatement.setString(1, "Cluster3");
            ResultSet resultSetCluster3 = preparedStatement.executeQuery();
            while (resultSetCluster3.next()) {
                cluster3.getData().add(new XYChart.Data(resultSetCluster3.getDouble(1), resultSetCluster3.getDouble(2)));
            }

            preparedStatement.setString(1, "Cluster4");
            ResultSet resultSetCluster4 = preparedStatement.executeQuery();
            while (resultSetCluster4.next()) {
                cluster4.getData().add(new XYChart.Data(resultSetCluster4.getDouble(1), resultSetCluster4.getDouble(2)));
            }
            // Catch statement.
        } catch(SQLException e) {
            // Create alert window for SQL errors.
            Utilities.showDialogWindow(Alert.AlertType.ERROR,
                    "An unknown SQL error occurred while trying to load the data.",
                    "SQL Error.",
                    "Operation Failed.");
            e.printStackTrace();
        }
    }

}