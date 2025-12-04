package com.example.ae4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;

public class EstadisticasController {

    @FXML
    private TableView<Fichaje> tableView;

    @FXML
    private TableColumn<Fichaje, String> trabajadorColumn;

    @FXML
    private TableColumn<Fichaje, LocalDate> fechaColumn;

    @FXML
    private TableColumn<Fichaje, LocalTime> horaColumn;

    @FXML
    private TableColumn<Fichaje, String> tipoColumn;

    @FXML
    private TableColumn<Fichaje, Double> temperaturaColumn;

    @FXML
    private TableColumn<Fichaje, String> descripcionesColumn;

    @FXML
    private TableColumn<Fichaje, String> observacionesColumn;

    @FXML
    private Button salirButton;

    @FXML
    private void initialize() {
        trabajadorColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTrabajador().getNombre() + " " + cellData.getValue().getTrabajador().getApellidos())
        );

        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        horaColumn.setCellValueFactory(new PropertyValueFactory<>("hora"));

        tipoColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo().name())
        );

        temperaturaColumn.setCellValueFactory(new PropertyValueFactory<>("temperatura"));
        descripcionesColumn.setCellValueFactory(new PropertyValueFactory<>("descripcionClima"));
        observacionesColumn.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        tableView.setItems(getFichajesEjemplo());
    }


    @FXML
    private void onSalirButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) salirButton.getScene().getWindow();
        stage.close();
    }

    private ObservableList<Fichaje> getFichajesEjemplo() {
        ObservableList<Fichaje> list = FXCollections.observableArrayList();

        Trabajador juan = new Trabajador("juan.garcia@empresa.com", "12345678A", "Garcia Lopez", "Juan", 1, "1234", java.time.LocalDate.now(), true);
        Fichaje f1 = new Fichaje(1L, juan, java.time.LocalDate.of(2025,12,1), java.time.LocalTime.of(8,0), Fichaje.TipoFichaje.ENTRADA, 15.0, "Nublado con intervalos de sol", null, java.time.LocalDateTime.now());
        Fichaje f2 = new Fichaje(2L, juan, java.time.LocalDate.of(2025,12,1), java.time.LocalTime.of(14,0), Fichaje.TipoFichaje.SALIDA, 19.0, "Nublado con intervalos de sol", null, java.time.LocalDateTime.now());

        list.addAll(f1, f2);

        return list;
    }
}
