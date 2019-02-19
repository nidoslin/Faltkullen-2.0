/* This is the main class. It sets up core functionality such as the stage (window) and the root (all the stuff in it).
 * root is loaded from an external fxml project file which saves us from setting up all graphical interface stuff
 * programmatically, meaning we only need to worry about handling army calculations etc in java code.
 *
 * Written by Nils Odin 24 January 2019
 */

package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    // create global variables so other classes can access these objects
    private static final double WIDTH = 1920, HEIGHT = 1080;
    static Stage stage;
    static Scene scene;
    static Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        setUpWindow();
    }

    private void setUpWindow() throws Exception{
        root = FXMLLoader.load(getClass().getResource("fxml/fältkullen.fxml"));
        scene = new Scene(root, WIDTH, HEIGHT, Color.DARKGREY);

        stage.setTitle("Fältkullen 2.0");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.getIcons().add(new Image(getClass().getResource("res/military.png").toExternalForm()));
        scene.setCursor(Cursor.CROSSHAIR);

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
