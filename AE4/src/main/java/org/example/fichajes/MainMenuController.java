package org.example.fichajes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.fichajes.util.DatabaseConnection;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private Button btnFichar;

    @FXML
    private Button btnGestion;

    @FXML
    private Button btnEstadisticas;

    @FXML
    private Button btnSalir;

    @FXML
    private void initialize() {
        if (!DatabaseConnection.testConnection()) {
            showError("Error de Conexion",
                     "No se puede conectar a la base de datos.\n\n" +
                     "Asegurate de que Docker este ejecutandose con:\n" +
                     "docker-compose up -d");
        }
    }

    @FXML
    private void onFicharClick() {
        openWindow("fichar-view.fxml", "Fichar - Registro de Entrada/Salida", 500, 600);
    }

    @FXML
    private void onGestionClick() {
        openWindow("gestion-view.fxml", "Gestión de Fichajes", 1000, 700);
    }

    @FXML
    private void onEstadisticasClick() {
        openWindow("estadisticas-view.fxml", "Estadísticas de Fichajes", 900, 650);
    }

    @FXML
    private void onSalirClick() {
        DatabaseConnection.closeConnection();
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    private void openWindow(String fxmlFile, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), width, height);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error", "No se pudo abrir la ventana: " + e.getMessage());
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

