package org.example.ae4;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ListarFichajesController {
    @FXML
    private Button salirButton;

    @FXML
    private DatePicker fechaDesdeDataPicker;

    @FXML
    private DatePicker fechaHastaDataPicker;

    @FXML
    private Button buscarButton;

    @FXML
    private Button limpiarButton;

    @FXML
    private TableView tableFichajes;

    @FXML
    private ComboBox trabajadorComboBox;

    @FXML
    private TableColumn fechaTableColum;

    @FXML
    private TableColumn nombreTableColum;

    @FXML
    private TableColumn entradaTableColum;

    @FXML
    private TableColumn salidaTableColum;

    @FXML
    private TableColumn climaTableColum;

    @FXML
    private TableColumn temperaturaTableColum;

    @FXML
    private TableColumn horasJornadaTableColum;

    @FXML
    private Label totalRegistrosLabel;

    @FXML
    private Label totalHorasLabel;

    @FXML
    private void initialize() {
        HibernateUtil.getSessionFactory();
    }

    @FXML
    private void setSalirButton() {
        HibernateUtil.shutdownDatabase();
        Stage stage = (Stage) salirButton.getScene().getWindow();
        stage.close();
    }


}
