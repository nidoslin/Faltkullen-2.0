/*
 * This is just an attempt at making a basic program that could be used to download maps from lantmäteriet directly in
 * the program, which would be neat. Unfortunatly it seems unlikely this could be implemented smoothly.
 */

package project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.*;
import java.net.URL;

public class MapDownloaderAttempt extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Webwiew map downloader");

        WebView webView = new WebView();
        System.out.println(webView.getEngine().isJavaScriptEnabled());

        webView.getEngine().load("https://kartutskrift.lantmateriet.se/");

        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
