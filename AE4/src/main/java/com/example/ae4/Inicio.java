package com.example.ae4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Inicio extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Inicio.class.getResource("inicio.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720, 720);
        stage.setTitle("INICIO");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(Inicio.class, args);
    }
}
