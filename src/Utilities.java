import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

// For generic methods that are suitable to be called from static context.
public class Utilities {

    // Function to set initialDirectory and allowed file types for FileChooser.
    public static void configureFileChooser(FileChooser fileChooser) {
        // Set the window title.
        fileChooser.setTitle("Select PIDC-O Data.");
        // Set the directory to user folder.
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        // Specify allowed file types. For the PIDC-O Data it's going to be either a .txt or possibly a CSV.
        // CSV delim not supported in openFile() yet.
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*"),
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
    }

    // Sets up JDBC and returns object so that other functions can safely perform database operations.
    public static DBHandler setupDatabaseConnection(String databaseName, String tableName) {
        // This database setup can likely be its own function.
        // Create new DBHandler Object and specify the required parameters.
        DBHandler dbHandler = new DBHandler("jdbc:mysql://localhost:3306?", "root", "Zi26303y");
        // Call function to establish the connection.
        dbHandler.establishConnection();
        // Create local connection object from the DBHandler.
        Connection connection = dbHandler.getConnection();
        // Call function to create the database and table using the fields specified in this class.
        try {
            dbHandler.createDatabase(databaseName, tableName);
        } catch(Exception e) {
            // Ideally this would throw the exception to the class that called it,
            // instead of creating an error dialog in this catch statement.
            showDialogWindow(Alert.AlertType.ERROR,
                    "A fatal error occured while trying to create the required " +
                            "schema and tables within the database.",
                    "I have no idea what you did to break everything this badly.",
                    "Fatal Error.");
            e.printStackTrace();
        }
        // Return the Connection object.
        return dbHandler;
    }

    // Function to simplify making alerts.
    public static void showDialogWindow(Alert.AlertType alertType, String contentText, String headerText, String titleText) {
        Alert alert = new Alert(alertType);
        alert.setContentText(contentText);
        alert.setHeaderText(headerText);
        alert.setTitle(titleText);
        alert.show();
    }

    // Function to open the rawdata in the OS.
    private void openFileInOS(File file) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch(IOException ex) {
            Logger.getLogger(
                    Task4.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }
}
