package com.example.ae4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Button ficharButton;

    @FXML
    private Button buscarEmpleadoButton;

    @FXML
    private Button estadisticasButton;

    @FXML
    private Button salirButton;

    @FXML
    private void onFicharButtonClick(ActionEvent actionEvent) {
        cambioPagina("fichar.fxml", "Fichar Empleado");
    }

    @FXML
    private void onBuscarEmpleadoButtonClick(ActionEvent actionEvent) {
        cambioPagina("buscarEmpleado.fxml", "Buscar Empleado");
    }

    @FXML
    private void onEstadisticasButtonClick(ActionEvent actionEvent) {
        cambioPagina("estadisticas.fxml", "Estadísticas");
    }

    @FXML
    private void onSalirButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) salirButton.getScene().getWindow();
        stage.close();
    }

    private void cambioPagina(String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene nuevaScene = new Scene(loader.load(), 720, 720);

            Stage stage = (Stage) ficharButton.getScene().getWindow();
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
