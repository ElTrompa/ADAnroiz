package com.example.garantias.controller;

import com.example.garantias.model.SesionOdoo;
import com.example.garantias.service.ServicioMongoDB;
import com.example.garantias.service.ServicioOdoo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControladorPrincipal {
    @FXML
    private TableView<com.example.garantias.model.Factura> tablaFacturas;

    @FXML
    private TableView<com.example.garantias.model.Garantia> tablaGarantias;

    @FXML
    private javafx.scene.control.Button btnActualizarFacturas;

    @FXML
    private TableColumn<?, ?> colFacturaNumero;

    @FXML
    private TableColumn<?, ?> colFacturaCliente;

    @FXML
    private TableColumn<?, ?> colFacturaFecha;

    @FXML
    private TableColumn<?, ?> colFacturaTotal;

    @FXML
    private TableColumn<?, ?> colGarantiaProducto;

    @FXML
    private TableColumn<?, ?> colGarantiaCliente;

    @FXML
    private TableColumn<?, ?> colGarantiaInicio;

    @FXML
    private TableColumn<?, ?> colGarantiaFin;

    @FXML
    private TableColumn<?, ?> colGarantiaEstado;

    @FXML
    private TableColumn<?, ?> colGarantiaDias;

    @FXML
    private TextField tfBuscarGarantia;

    @FXML
    private ChoiceBox<String> cbEstado;

    @FXML
    private Label lblTotalVentas;

    @FXML
    private Label lblNumFacturas;

    @FXML
    private Label lblActivas;

    @FXML
    private Label lblPorExpirar;

    @FXML
    private Label lblExpiradas;

    @FXML
    private PieChart pieEstados;

    @FXML
    private BarChart<String, Number> barVentas;

    private final ServicioMongoDB servicioMongo = new ServicioMongoDB("mongodb://admin:admin_password@localhost:27017", "garantias_db");

    private final ObservableList<com.example.garantias.model.Factura> facturasObs = FXCollections.observableArrayList();
    private final ObservableList<com.example.garantias.model.Garantia> garantiasObs = FXCollections.observableArrayList();

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Inicializar columnas facturas
        colFacturaNumero.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colFacturaCliente.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreCliente()));
        colFacturaFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaFactura() == null ? "" : c.getValue().getFechaFactura().format(df)));
        colFacturaTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f€", c.getValue().getMontoTotal())));
        tablaFacturas.setItems(facturasObs);

        // Inicializar columnas garantias
        colGarantiaProducto.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreProducto()));
        colGarantiaCliente.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreCliente()));
        colGarantiaInicio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaInicioGarantia() == null ? "" : c.getValue().getFechaInicioGarantia().format(df)));
        colGarantiaFin.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaFinGarantia() == null ? "" : c.getValue().getFechaFinGarantia().format(df)));
        colGarantiaEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado() == null ? "" : c.getValue().getEstado().name()));
        colGarantiaDias.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getFechaFinGarantia() == null ? 0 : java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), c.getValue().getFechaFinGarantia()))));
        tablaGarantias.setItems(garantiasObs);

        cbEstado.setItems(FXCollections.observableArrayList("Todas", "ACTIVA", "POR_EXPIRAR", "EXPIRADA"));
        cbEstado.setValue("Todas");

        // Cargar datos iniciales
        cargarGarantias();
        cargarEstadisticas();
    }

    @FXML
    private void onActualizarFacturas() {
        // Obtener facturas desde Odoo y crear garantías si no existen (en background)
        new Thread(() -> {
            try {
                SesionOdoo s = SesionOdoo.getInstancia();
                ServicioOdoo servicioOdoo = new ServicioOdoo(s.getUrl() == null ? "http://localhost:8069" : s.getUrl(), s.getBaseDatos() == null ? "odoo" : s.getBaseDatos());
                List<com.example.garantias.model.Factura> facturas = servicioOdoo.obtenerFacturasVenta();
                int mesesGarantia = 12;
                for (com.example.garantias.model.Factura f : facturas) {
                    if (f.getLineas() != null) {
                        for (com.example.garantias.model.LineaFactura linea : f.getLineas()) {
                            if (!servicioMongo.existeGarantiaPorLinea(linea.getId())) {
                                servicioMongo.crearGarantiaDesdeLinea(f.getId(), f.getNombre(), linea, f.getNombreCliente(), mesesGarantia);
                            }
                        }
                    }
                }
                Platform.runLater(() -> {
                    facturasObs.setAll(facturas);
                    cargarGarantias();
                    cargarEstadisticas();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Error al actualizar facturas: " + e.getMessage());
                    a.showAndWait();
                });
            }
        }).start();
    }

    @FXML
    private void onActualizarGarantias() {
        cargarGarantias();
        cargarEstadisticas();
    }

    private void cargarGarantias() {
        new Thread(() -> {
            try {
                List<com.example.garantias.model.Garantia> g = servicioMongo.obtenerGarantias();
                String filtro = tfBuscarGarantia.getText();
                String estado = cbEstado.getValue();
                java.util.stream.Stream<com.example.garantias.model.Garantia> stream = g.stream();
                if (filtro != null && !filtro.isBlank()) {
                    String f = filtro.toLowerCase();
                    stream = stream.filter(x -> (x.getNombreProducto() != null && x.getNombreProducto().toLowerCase().contains(f)) || (x.getNombreCliente() != null && x.getNombreCliente().toLowerCase().contains(f)));
                }
                if (estado != null && !estado.equals("Todas")) {
                    try {
                        com.example.garantias.model.Garantia.Estado es = com.example.garantias.model.Garantia.Estado.valueOf(estado);
                        stream = stream.filter(x -> x.getEstado() == es);
                    } catch (Exception ignored) {}
                }
                List<com.example.garantias.model.Garantia> filtered = stream.toList();
                Platform.runLater(() -> {
                    garantiasObs.setAll(filtered);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cargarEstadisticas() {
        new Thread(() -> {
            try {
                long activas = servicioMongo.contarGarantiasPorEstado(com.example.garantias.model.Garantia.Estado.ACTIVA);
                long porExp = servicioMongo.contarGarantiasPorEstado(com.example.garantias.model.Garantia.Estado.POR_EXPIRAR);
                long expiradas = servicioMongo.contarGarantiasPorEstado(com.example.garantias.model.Garantia.Estado.EXPIRADA);
                List<com.example.garantias.model.Garantia> todas = servicioMongo.obtenerGarantias();
                double totalVentas = 0;
                // sumar totales de facturas que están en el sistema
                for (com.example.garantias.model.Factura f : facturasObs) totalVentas += f.getMontoTotal();

                Platform.runLater(() -> {
                    lblActivas.setText(String.valueOf(activas));
                    lblPorExpirar.setText(String.valueOf(porExp));
                    lblExpiradas.setText(String.valueOf(expiradas));
                    lblNumFacturas.setText(String.valueOf(facturasObs.size()));
                    lblTotalVentas.setText(String.format("%.2f €", totalVentas));

                    pieEstados.getData().clear();
                    pieEstados.getData().add(new PieChart.Data("Activas", activas));
                    pieEstados.getData().add(new PieChart.Data("Por Expirar", porExp));
                    pieEstados.getData().add(new PieChart.Data("Expiradas", expiradas));

                    // Bar chart: ventas por cliente (simple)
                    barVentas.getData().clear();
                    java.util.Map<String, Double> ventasPorCliente = new java.util.HashMap<>();
                    for (com.example.garantias.model.Factura f : facturasObs) {
                        ventasPorCliente.merge(f.getNombreCliente() == null ? "--" : f.getNombreCliente(), f.getMontoTotal(), Double::sum);
                    }
                    javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
                    ventasPorCliente.forEach((k, v) -> series.getData().add(new javafx.scene.chart.XYChart.Data<>(k, v)));
                    barVentas.getData().add(series);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}