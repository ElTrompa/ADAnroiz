package com.example.ae5;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.ae5.ui.LoginController;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        // Set up JavaFX main window
        stage.setTitle("AE5 - Sistema de Garantías");
        stage.setWidth(1024);
        stage.setHeight(768);

        // Load login scene
        LoginController loginController = new LoginController(stage);
        Scene loginScene = loginController.createScene();

        stage.setScene(loginScene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
