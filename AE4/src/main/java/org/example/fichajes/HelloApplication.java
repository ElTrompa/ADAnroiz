package org.example.fichajes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.fichajes.util.DatabaseConnection;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        if (!DatabaseConnection.testConnection()) {
            System.err.println("No se pudo conectar a la base de datos. Asegurate de que Docker este ejecutandose.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Sistema de Control de Fichajes - Menu Principal");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            DatabaseConnection.closeConnection();
        });
        stage.show();
    }
}
