package com.example.garantias.controller;

import com.example.garantias.model.Factura;
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
    private TableColumn<com.example.garantias.model.Factura, String> colFacturaNumero;

    @FXML
    private TableColumn<com.example.garantias.model.Factura, String> colFacturaCliente;

    @FXML
    private TableColumn<com.example.garantias.model.Factura, String> colFacturaFecha;

    @FXML
    private TableColumn<com.example.garantias.model.Factura, String> colFacturaTotal;

    @FXML
    private TableColumn<com.example.garantias.model.Garantia, String> colGarantiaProducto;

    @FXML
    private TableColumn<com.example.garantias.model.Garantia, String> colGarantiaCliente;

    @FXML
    private TableColumn<com.example.garantias.model.Garantia, String> colGarantiaInicio;

    @FXML
    private TableColumn<com.example.garantias.model.Garantia, String> colGarantiaFin;

    @FXML
    private TableColumn<com.example.garantias.model.Garantia, String> colGarantiaEstado;

    @FXML
    private TableColumn<com.example.garantias.model.Garantia, String> colGarantiaDias;

    @FXML
    private TableColumn<com.example.garantias.model.Garantia, Void> colGarantiaVer;

    @FXML
    private TableColumn<com.example.garantias.model.Garantia, Void> colGarantiaEliminar;

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

        // Column 'Ver' con botón
        colGarantiaVer.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Ver");
            {
                btn.setOnAction(e -> {
                    com.example.garantias.model.Garantia g = getTableView().getItems().get(getIndex());
                    mostrarDetalleGarantia(g);
                });
                btn.getStyleClass().add("btn-info");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });

        // Column 'Eliminar' con botón
        colGarantiaEliminar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Eliminar");
            {
                btn.setOnAction(e -> {
                    com.example.garantias.model.Garantia g = getTableView().getItems().get(getIndex());
                    confirmarYEliminar(g);
                });
                btn.getStyleClass().add("btn-danger");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });
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
                System.out.println("DEBUG: SesionOdoo url=" + s.getUrl() + " db=" + s.getBaseDatos() + " cookie=" + s.getCookieSession());
                ServicioOdoo servicioOdoo = new ServicioOdoo(s.getUrl() == null ? "http://localhost:8069" : s.getUrl(), s.getBaseDatos() == null ? "odoo" : s.getBaseDatos());
                List<com.example.garantias.model.Factura> facturas = servicioOdoo.obtenerFacturasVenta();
                System.out.println("DEBUG: facturas obtenidas count=" + (facturas == null ? 0 : facturas.size()));
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

    private void mostrarDetalleGarantia(com.example.garantias.model.Garantia g) {
        if (g == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Factura: ").append(g.getFacturaId()).append("\n");
        sb.append("Linea: ").append(g.getLineaFacturaId()).append("\n");
        sb.append("Producto: ").append(g.getNombreProducto()).append("\n");
        sb.append("Cliente: ").append(g.getNombreCliente()).append("\n");
        sb.append("Inicio garantía: ").append(g.getFechaInicioGarantia()).append("\n");
        sb.append("Fin garantía: ").append(g.getFechaFinGarantia()).append("\n");
        sb.append("Estado: ").append(g.getEstado()).append("\n");
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Detalle de garantía");
        a.setHeaderText("Garantía detalle");
        a.setContentText(sb.toString());
        a.showAndWait();
    }

    private void confirmarYEliminar(com.example.garantias.model.Garantia g) {
        if (g == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Confirmas eliminar la garantía de la línea " + g.getLineaFacturaId() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        boolean ok = servicioMongo.eliminarGarantiaPorLinea(g.getLineaFacturaId());
                        Platform.runLater(() -> {
                            if (ok) {
                                garantiasObs.remove(g);
                                cargarEstadisticas();
                                Alert info = new Alert(Alert.AlertType.INFORMATION, "Garantía eliminada.");
                                info.showAndWait();
                            } else {
                                Alert err = new Alert(Alert.AlertType.ERROR, "No se pudo eliminar la garantía.");
                                err.showAndWait();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Error al eliminar: " + e.getMessage()).showAndWait());
                    }
                }).start();
            }
        });
    }

    private void cargarEstadisticas() {
        new Thread(() -> {
            try {
                long activas = servicioMongo.contarGarantiasPorEstado(com.example.garantias.model.Garantia.Estado.ACTIVA);
                long porExp = servicioMongo.contarGarantiasPorEstado(com.example.garantias.model.Garantia.Estado.POR_EXPIRAR);
                long expiradas = servicioMongo.contarGarantiasPorEstado(com.example.garantias.model.Garantia.Estado.EXPIRADA);
                List<com.example.garantias.model.Garantia> todas = servicioMongo.obtenerGarantias();
                double totalVentas = facturasObs.stream().mapToDouble(Factura::getMontoTotal).sum();
                // sumar totales de facturas que están en el sistema

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