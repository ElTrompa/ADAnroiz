package com.example.ae4;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class InicioController {
    @FXML
    private Button inicioButon;
    @FXML
    private Label welcomeText;

    @FXML
    private void onInicioButonClick() {
        HibernateUtil.getSessionFactory();
        cambioPagina("menu.fxml", "Menu Principal");
    }

    private void cambioPagina(String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene nuevaScene = new Scene(loader.load(), 720, 720);

            Stage stage = (Stage) inicioButon.getScene().getWindow();
            stage.setScene(nuevaScene);
            stage.setTitle(titulo);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "No se pudo cambiar de página: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
