package org.example.fichajes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import org.example.fichajes.dao.FichajeDAO;
import org.example.fichajes.dao.TrabajadorDAO;
import org.example.fichajes.model.Fichaje;
import org.example.fichajes.model.Trabajador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class EstadisticasController {

    @FXML
    private DatePicker dateDesde;

    @FXML
    private DatePicker dateHasta;

    @FXML
    private ComboBox<Trabajador> comboTrabajador;

    @FXML
    private Button btnGenerar;

    @FXML
    private BarChart<String, Number> chartHorasPorDia;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private PieChart chartDistribucion;

    @FXML
    private Label lblTotalDias;

    @FXML
    private Label lblTotalFichajes;

    @FXML
    private Label lblTotalHoras;

    @FXML
    private Label lblPromedioDiario;

    @FXML
    private Label lblDiaMasHoras;

    @FXML
    private Label lblDiaMenosHoras;

    @FXML
    private TextArea txtResumen;

    private FichajeDAO fichajeDAO;
    private TrabajadorDAO trabajadorDAO;

    @FXML
    private void initialize() {
        fichajeDAO = new FichajeDAO();
        trabajadorDAO = new TrabajadorDAO();

        cargarTrabajadores();

        dateHasta.setValue(LocalDate.now());
        dateDesde.setValue(LocalDate.now().minusMonths(1));

        configurarGraficos();
        generarEstadisticas();
    }

    private void configurarGraficos() {
        xAxis.setLabel("Fecha");
        yAxis.setLabel("Horas Trabajadas");
        chartHorasPorDia.setTitle("Horas Trabajadas por Día");
        chartHorasPorDia.setLegendVisible(true);

        chartDistribucion.setTitle("Distribución de Horas por Trabajador");
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
    private void onGenerarClick() {
        generarEstadisticas();
    }

    private void generarEstadisticas() {
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

            procesarEstadisticas(fichajes, fechaDesde, fechaHasta);

        } catch (Exception e) {
            showError("Error al generar estadísticas: " + e.getMessage());
        }
    }

    private void procesarEstadisticas(List<Fichaje> fichajes, LocalDate fechaDesde, LocalDate fechaHasta) {
        Map<String, Map<Integer, List<Fichaje>>> fichajesPorFechaYTrabajador = new HashMap<>();

        for (Fichaje fichaje : fichajes) {
            String fechaStr = fichaje.getFecha().toString();
            fichajesPorFechaYTrabajador
                .computeIfAbsent(fechaStr, k -> new HashMap<>())
                .computeIfAbsent(fichaje.getTrabajadorId(), k -> new ArrayList<>())
                .add(fichaje);
        }

        Map<String, Double> horasPorDia = new TreeMap<>();
        Map<Integer, Double> horasPorTrabajador = new HashMap<>();

        for (Map.Entry<String, Map<Integer, List<Fichaje>>> entryFecha : fichajesPorFechaYTrabajador.entrySet()) {
            String fecha = entryFecha.getKey();
            double horasDia = 0;

            for (Map.Entry<Integer, List<Fichaje>> entryTrabajador : entryFecha.getValue().entrySet()) {
                int trabajadorId = entryTrabajador.getKey();
                List<Fichaje> fichajesTrabajador = entryTrabajador.getValue();

                List<Fichaje> entradas = fichajesTrabajador.stream()
                    .filter(f -> f.getTipo() == Fichaje.TipoFichaje.ENTRADA)
                    .sorted(Comparator.comparing(Fichaje::getHora))
                    .collect(Collectors.toList());

                List<Fichaje> salidas = fichajesTrabajador.stream()
                    .filter(f -> f.getTipo() == Fichaje.TipoFichaje.SALIDA)
                    .sorted(Comparator.comparing(Fichaje::getHora))
                    .collect(Collectors.toList());

                double horasTrabajador = 0;
                for (int i = 0; i < Math.min(entradas.size(), salidas.size()); i++) {
                    long minutos = ChronoUnit.MINUTES.between(entradas.get(i).getHora(), salidas.get(i).getHora());
                    horasTrabajador += minutos / 60.0;
                }

                horasDia += horasTrabajador;
                horasPorTrabajador.merge(trabajadorId, horasTrabajador, Double::sum);
            }

            horasPorDia.put(fecha, horasDia);
        }

        actualizarGraficos(horasPorDia, horasPorTrabajador);
        actualizarEstadisticasTextuales(fichajes, horasPorDia, fechaDesde, fechaHasta);
    }

    private void actualizarGraficos(Map<String, Double> horasPorDia, Map<Integer, Double> horasPorTrabajador) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Horas Trabajadas");

        for (Map.Entry<String, Double> entry : horasPorDia.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chartHorasPorDia.getData().clear();
        chartHorasPorDia.getData().add(series);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            for (Map.Entry<Integer, Double> entry : horasPorTrabajador.entrySet()) {
                Trabajador trabajador = trabajadorDAO.getTrabajadorById(entry.getKey());
                if (trabajador != null) {
                    String nombre = trabajador.getNombreCompleto();
                    pieChartData.add(new PieChart.Data(
                        nombre + " (" + String.format("%.1f", entry.getValue()) + "h)",
                        entry.getValue()
                    ));
                }
            }
        } catch (Exception e) {
            showError("Error al cargar datos de trabajadores: " + e.getMessage());
        }

        chartDistribucion.setData(pieChartData);
    }

    private void actualizarEstadisticasTextuales(List<Fichaje> fichajes, Map<String, Double> horasPorDia,
                                                  LocalDate fechaDesde, LocalDate fechaHasta) {

        long totalDias = ChronoUnit.DAYS.between(fechaDesde, fechaHasta) + 1;
        int totalFichajes = fichajes.size();
        double totalHoras = horasPorDia.values().stream().mapToDouble(Double::doubleValue).sum();
        double promedioDiario = horasPorDia.isEmpty() ? 0 : totalHoras / horasPorDia.size();

        lblTotalDias.setText(String.valueOf(totalDias));
        lblTotalFichajes.setText(String.valueOf(totalFichajes));
        lblTotalHoras.setText(String.format("%.2f horas", totalHoras));
        lblPromedioDiario.setText(String.format("%.2f horas", promedioDiario));

        // Día con más y menos horas
        if (!horasPorDia.isEmpty()) {
            Map.Entry<String, Double> diaMasHoras = horasPorDia.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

            Map.Entry<String, Double> diaMenosHoras = horasPorDia.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);

            if (diaMasHoras != null) {
                lblDiaMasHoras.setText(String.format("%s (%.2fh)", diaMasHoras.getKey(), diaMasHoras.getValue()));
            }

            if (diaMenosHoras != null) {
                lblDiaMenosHoras.setText(String.format("%s (%.2fh)", diaMenosHoras.getKey(), diaMenosHoras.getValue()));
            }
        } else {
            lblDiaMasHoras.setText("N/A");
            lblDiaMenosHoras.setText("N/A");
        }

        StringBuilder resumen = new StringBuilder();
        resumen.append("RESUMEN DE ESTADISTICAS\n");
        resumen.append("========================\n\n");
        resumen.append("Periodo analizado: ").append(fechaDesde).append(" a ").append(fechaHasta).append("\n");
        resumen.append("Total de dias en el periodo: ").append(totalDias).append("\n");
        resumen.append("Total de fichajes registrados: ").append(totalFichajes).append("\n");
        resumen.append("Total de horas trabajadas: ").append(String.format("%.2f", totalHoras)).append("\n");
        resumen.append("Promedio de horas diarias: ").append(String.format("%.2f", promedioDiario)).append("\n\n");

        if (!horasPorDia.isEmpty()) {
            resumen.append("Dia con mas horas trabajadas: ").append(lblDiaMasHoras.getText()).append("\n");
            resumen.append("Dia con menos horas trabajadas: ").append(lblDiaMenosHoras.getText()).append("\n");
        }

        txtResumen.setText(resumen.toString());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

