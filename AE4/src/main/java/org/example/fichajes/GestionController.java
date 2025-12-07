package org.example.fichajes;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.fichajes.dao.FichajeDAO;
import org.example.fichajes.dao.TrabajadorDAO;
import org.example.fichajes.model.Fichaje;
import org.example.fichajes.model.FichajeDia;
import org.example.fichajes.model.Trabajador;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class GestionController {

    @FXML
    private DatePicker dateDesde;

    @FXML
    private DatePicker dateHasta;

    @FXML
    private ComboBox<Trabajador> comboTrabajador;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private TableView<FichajeDia> tableFichajes;

    @FXML
    private TableColumn<FichajeDia, String> colFecha;

    @FXML
    private TableColumn<FichajeDia, String> colTrabajador;

    @FXML
    private TableColumn<FichajeDia, String> colEntrada;

    @FXML
    private TableColumn<FichajeDia, String> colSalida;

    @FXML
    private TableColumn<FichajeDia, String> colTiempo;

    @FXML
    private TableColumn<FichajeDia, String> colClimaEntrada;

    @FXML
    private TableColumn<FichajeDia, String> colClimaSalida;

    @FXML
    private TableColumn<FichajeDia, String> colTempEntrada;

    @FXML
    private TableColumn<FichajeDia, String> colTempSalida;

    @FXML
    private Label lblTotalRegistros;

    @FXML
    private Label lblTotalHoras;

    private FichajeDAO fichajeDAO;
    private TrabajadorDAO trabajadorDAO;

    @FXML
    private void initialize() {
        fichajeDAO = new FichajeDAO();
        trabajadorDAO = new TrabajadorDAO();

        configurarTabla();
        cargarTrabajadores();

        dateHasta.setValue(LocalDate.now().plusDays(7));
        dateDesde.setValue(LocalDate.now().minusDays(7));

        cargarDatos();
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getFecha().toString()));

        colTrabajador.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTrabajadorNombre()));

        colEntrada.setCellValueFactory(cellData -> {
            var hora = cellData.getValue().getHoraEntrada();
            return new SimpleStringProperty(hora != null ? hora.toString() : "-");
        });

        colSalida.setCellValueFactory(cellData -> {
            var hora = cellData.getValue().getHoraSalida();
            return new SimpleStringProperty(hora != null ? hora.toString() : "-");
        });

        colTiempo.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTiempoTrabajado()));

        colClimaEntrada.setCellValueFactory(cellData -> {
            String clima = cellData.getValue().getClimaEntrada();
            return new SimpleStringProperty(clima != null ? clima : "-");
        });

        colClimaSalida.setCellValueFactory(cellData -> {
            String clima = cellData.getValue().getClimaSalida();
            return new SimpleStringProperty(clima != null ? clima : "-");
        });

        colTempEntrada.setCellValueFactory(cellData -> {
            Double temp = cellData.getValue().getTemperaturaEntrada();
            return new SimpleStringProperty(temp != null ? String.format("%.1f°C", temp) : "-");
        });

        colTempSalida.setCellValueFactory(cellData -> {
            Double temp = cellData.getValue().getTemperaturaSalida();
            return new SimpleStringProperty(temp != null ? String.format("%.1f°C", temp) : "-");
        });
    }

    private void cargarTrabajadores() {
        try {
            List<Trabajador> trabajadores = trabajadorDAO.getAllTrabajadores();

            Trabajador todos = new Trabajador();
            todos.setId(0);
            todos.setNombre("Todos");
            todos.setApellidos("");

            trabajadores.add(0, todos);

            comboTrabajador.setItems(FXCollections.observableArrayList(trabajadores));
            comboTrabajador.getSelectionModel().selectFirst();
        } catch (Exception e) {
            showError("Error al cargar trabajadores: " + e.getMessage());
        }
    }

    @FXML
    private void onBuscarClick() {
        cargarDatos();
    }

    @FXML
    private void onLimpiarClick() {
        dateDesde.setValue(LocalDate.now().minusDays(7));
        dateHasta.setValue(LocalDate.now().plusDays(7));
        comboTrabajador.getSelectionModel().selectFirst();
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            LocalDate fechaDesde = dateDesde.getValue();
            LocalDate fechaHasta = dateHasta.getValue();

            if (fechaDesde == null || fechaHasta == null) {
                showError("Debe seleccionar un rango de fechas");
                return;
            }

            if (fechaDesde.isAfter(fechaHasta)) {
                showError("La fecha inicial no puede ser posterior a la fecha final");
                return;
            }

            List<Fichaje> fichajes;
            Trabajador trabajadorSeleccionado = comboTrabajador.getValue();

            if (trabajadorSeleccionado != null && trabajadorSeleccionado.getId() > 0) {
                fichajes = fichajeDAO.getFichajesByTrabajador(trabajadorSeleccionado.getId());
                fichajes.removeIf(f -> f.getFecha().isBefore(fechaDesde) || f.getFecha().isAfter(fechaHasta));
            } else {
                fichajes = fichajeDAO.getFichajesByRangoFechas(fechaDesde, fechaHasta);
            }

            List<FichajeDia> fichajesDia = agruparFichajesPorDia(fichajes);

            tableFichajes.setItems(FXCollections.observableArrayList(fichajesDia));
            actualizarEstadisticas(fichajesDia);

        } catch (Exception e) {
            showError("Error al cargar datos: " + e.getMessage());
        }
    }

    private List<FichajeDia> agruparFichajesPorDia(List<Fichaje> fichajes) {
        Map<String, FichajeDia> mapa = new LinkedHashMap<>();

        for (Fichaje f : fichajes) {
            String clave = f.getFecha() + "|" + f.getTrabajador().getId();

            FichajeDia dia = mapa.computeIfAbsent(clave, k -> {
                FichajeDia nuevo = new FichajeDia();
                nuevo.setFecha(f.getFecha());
                nuevo.setTrabajadorNombre(f.getTrabajadorNombre());
                return nuevo;
            });

            if (f.getTipo() == Fichaje.TipoFichaje.ENTRADA) {
                dia.setHoraEntrada(f.getHora());
                dia.setClimaEntrada(f.getDescripcionClima());
                dia.setTemperaturaEntrada(f.getTemperatura());
                dia.setObservacionesEntrada(f.getObservaciones());
            } else {
                dia.setHoraSalida(f.getHora());
                dia.setClimaSalida(f.getDescripcionClima());
                dia.setTemperaturaSalida(f.getTemperatura());
                dia.setObservacionesSalida(f.getObservaciones());
            }
        }

        List<FichajeDia> resultado = new ArrayList<>(mapa.values());
        resultado.sort((a, b) -> {
            int cmp = a.getFecha().compareTo(b.getFecha());
            if (cmp != 0) return cmp;
            return a.getTrabajadorNombre().compareTo(b.getTrabajadorNombre());
        });

        return resultado;
    }

    private void actualizarEstadisticas(List<FichajeDia> fichajesDia) {
        lblTotalRegistros.setText("Total de días: " + fichajesDia.size());

        long totalMinutos = 0;
        int diasCompletos = 0;

        for (FichajeDia dia : fichajesDia) {
            if (dia.isCompleto()) {
                Duration duracion = Duration.between(dia.getHoraEntrada(), dia.getHoraSalida());
                totalMinutos += duracion.toMinutes();
                diasCompletos++;
            }
        }

        long horas = totalMinutos / 60;
        long minutos = totalMinutos % 60;
        lblTotalHoras.setText(String.format("Total horas: %02d:%02d (%d días completos)", horas, minutos, diasCompletos));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}