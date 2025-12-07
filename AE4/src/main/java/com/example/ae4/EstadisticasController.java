package com.example.ae4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.beans.property.SimpleStringProperty;

import java.time.Duration;
import java.util.List;

public class EstadisticasController {

    @FXML
    private TableView<Fichaje> TableView;

    @FXML
    private TableColumn<Fichaje, String> trabajadorColumn;

    @FXML
    private TableColumn<Fichaje, String> fechaColumn;

    @FXML
    private TableColumn<Fichaje, String> horaColumn;

    @FXML
    private TableColumn<Fichaje, String> tipoColumn;

    @FXML
    private TableColumn<Fichaje, String> temperaturaColumn;

    @FXML
    private TableColumn<Fichaje, String> descripcionesColumn;

    @FXML
    private TableColumn<Fichaje, String> observacionesColumn;

    @FXML
    private Label totalRegistros;

    @FXML
    private Label totalHorasTrabajadas;

    @FXML
    private Button salirButton;

    private FichajeDAO fichajeDAO = new FichajeDAO();

    @FXML
    public void initialize() {
        loadFichajes();
    }

    private void loadFichajes() {
        List<Fichaje> fichajes = fichajeDAO.getAllFichajes();

        ObservableList<Fichaje> lista = FXCollections.observableArrayList(fichajes);

        trabajadorColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTrabajador().getNombre())
        );
        fechaColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFecha().toString())
        );
        horaColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHora().toString())
        );
        tipoColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTipo().name())
        );
        temperaturaColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getTemperatura() != null ? cellData.getValue().getTemperatura().toString() : ""
                )
        );
        descripcionesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getDescripcionClima() != null ? cellData.getValue().getDescripcionClima() : ""
                )
        );
        observacionesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getObservaciones() != null ? cellData.getValue().getObservaciones() : ""
                )
        );

        TableView.setItems(lista);

        totalRegistros.setText("Total de registros: " + lista.size());
        totalHorasTrabajadas.setText("Total horas trabajadas: " + calcularHorasTotales(lista));
    }

    private String calcularHorasTotales(List<Fichaje> fichajes) {
        Duration total = Duration.ZERO;

        // Suma horas de ENTRADA y SALIDA por trabajador
        for (Fichaje f : fichajes) {
            if (f.getTipo() == Fichaje.TipoFichaje.ENTRADA) {
                for (Fichaje f2 : fichajes) {
                    if (f2.getTrabajador().getId().equals(f.getTrabajador().getId())
                            && f2.getTipo() == Fichaje.TipoFichaje.SALIDA
                            && f2.getFecha().equals(f.getFecha())) {
                        Duration dur = Duration.between(f.getHora(), f2.getHora());
                        total = total.plus(dur);
                        break;
                    }
                }
            }
        }

        long hours = total.toHours();
        long minutes = total.toMinutes() % 60;
        return hours + "h " + minutes + "m";
    }

    @FXML
    private void onSalirButtonClick() {
        salirButton.getScene().getWindow().hide();
    }
}
