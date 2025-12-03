package org.example.ae4;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class InicioController {
    @FXML
    private Button iniciarButton;

    @FXML
    private void initialize() {
        HibernateUtil.getSessionFactory();
    }

    @FXML
    private void iniciarButton() {
        InicioController.openWindow("menu.fxml", "menu");
    }

    public static void openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(InicioController.class.getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 720, 720);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
