package org.example.ae4;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.awt.*;

public class MenuController {
    @FXML
    private Button ficharButton;

    @FXML
    private Button gestionButton;

    @FXML
    private Button estadisticasButton;

    @FXML
    private Button salirButton;

    @FXML
    private void initialize() {
        HibernateUtil.getSessionFactory();
    }

    @FXML
    private void setFicharButton() {
        HelloApplication.openWindow("Crearfichaje.fxml", "fichar");
    }

    @FXML
    private void setGestionButton() {
        HelloApplication.openWindow("listarFichajes.fxml", "gestion");
    }

    @FXML
    private void setEstadisticasButton() {
        HelloApplication.openWindow("listarFichajes.fxml", "PorPersona");
    }

    @FXML
    private void setSalirButton() {
        HibernateUtil.shutdownDatabase();
        Stage stage = (Stage) salirButton.getScene().getWindow();
        stage.close();
    }
}
