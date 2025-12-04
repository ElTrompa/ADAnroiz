package com.example.ae4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EstadisticasController {
    @FXML
    private TableView TableView;

    @FXML
    private TableColumn trabajadorColumn;

    @FXML
    private TableColumn fechaColumn;

    @FXML
    private TableColumn horaColumn;

    @FXML
    private TableColumn tipoColumn;

    @FXML
    private TableColumn temperaturaColumn;

    @FXML
    private TableColumn descripcionesColumn;

    @FXML
    private TableColumn observacionesColumn;

    @FXML
    private Label totalRegistros;

    @FXML
    private Label totalHorasTrabajadas;

    @FXML
    private Button salirButton;

    @FXML
    private void onSalirButtonClick(ActionEvent actionEvent) {
    }
}
