package com.example.ae5.ui;

import com.example.ae5.model.Invoice;
import com.example.ae5.model.Guarantee;
import com.example.ae5.service.OdooService;
import com.example.ae5.service.GuaranteeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class MainViewController {
    private Stage stage;
    private OdooService odooService;
    private GuaranteeService guaranteeService;

    public MainViewController(Stage stage) {
        this.stage = stage;
        this.odooService = new OdooService();
        this.guaranteeService = new GuaranteeService();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();

        // Top menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Tab pane for invoices and guarantees
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab invoiceTab = createInvoiceTab();
        Tab guaranteeTab = createGuaranteeTab();

        tabPane.getTabs().addAll(invoiceTab, guaranteeTab);
        root.setCenter(tabPane);

        return new Scene(root, 1024, 768);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        return menuBar;
    }

    private Tab createInvoiceTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label titleLabel = new Label("Facturas de Odoo");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Button refreshButton = new Button("Actualizar");
        HBox topBox = new HBox(10);
        topBox.getChildren().addAll(titleLabel, refreshButton);

        TableView<Invoice> table = new TableView<>();
        TableColumn<Invoice, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Invoice, String> numberCol = new TableColumn<>("Número Factura");
        numberCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getNumber()));

        TableColumn<Invoice, String> clientCol = new TableColumn<>("Cliente");
        clientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getClient()));

        TableColumn<Invoice, LocalDate> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDate()));

        TableColumn<Invoice, Double> amountCol = new TableColumn<>("Monto");
        amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));

        table.getColumns().addAll(idCol, numberCol, clientCol, dateCol, amountCol);

        refreshButton.setOnAction(e -> {
            try {
                List<Invoice> invoices = odooService.getInvoices();
                ObservableList<Invoice> observableInvoices = FXCollections.observableArrayList(invoices);
                table.setItems(observableInvoices);
                if (invoices.isEmpty()) {
                    showInfoDialog("No hay facturas disponibles en Odoo");
                }
            } catch (Exception ex) {
                System.err.println("No se pudieron cargar las facturas de Odoo: " + ex.getMessage());
                // Include the error message for easier debugging
                showInfoDialog("No se pudo conectar a Odoo: " + ex.getMessage() + "\nIntentando modo offline...\nLas garantías se guardarán localmente.");
                table.setItems(FXCollections.observableArrayList());
            }
        });

        vbox.getChildren().addAll(topBox, table);
        Tab tab = new Tab("Facturas", vbox);
        tab.setClosable(false);
        return tab;
    }

    private Tab createGuaranteeTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label titleLabel = new Label("Gestión de Garantías");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(5));
        TextField clientFilter = new TextField();
        clientFilter.setPromptText("Filtrar por cliente");
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.setItems(FXCollections.observableArrayList("", "ACTIVA", "EXPIRADA", "CANCELADA"));
        statusFilter.setPrefWidth(120);
        TextField countryFilter = new TextField();
        countryFilter.setPromptText("Filtrar por país");
        Button searchButton = new Button("Buscar");
        filterBox.getChildren().addAll(
                new Label("Cliente:"), clientFilter,
                new Label("Estado:"), statusFilter,
                new Label("País:"), countryFilter,
                searchButton
        );

        Button newGuaranteeButton = new Button("+ Nueva Garantía");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Eliminar");
        Button refreshButton = new Button("Actualizar");
        Button exitButton = new Button("Salir");

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(5));
        buttonBox.getChildren().addAll(titleLabel, newGuaranteeButton, editButton, deleteButton, refreshButton, exitButton);

        TableView<Guarantee> table = new TableView<>();
        TableColumn<Guarantee, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Guarantee, Long> invoiceCol = new TableColumn<>("ID Factura");
        invoiceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getInvoiceId()));

        TableColumn<Guarantee, String> clientCol = new TableColumn<>("Cliente");
        clientCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getClient()));

        TableColumn<Guarantee, String> productCol = new TableColumn<>("Producto");
        productCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getProduct()));

        TableColumn<Guarantee, String> statusCol = new TableColumn<>("Estado");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStatus()));

        TableColumn<Guarantee, String> countryCol = new TableColumn<>("País");
        countryCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getCountry()));

        TableColumn<Guarantee, LocalDate> startCol = new TableColumn<>("Fecha Inicio");
        startCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getWarrantyStart()));

        TableColumn<Guarantee, LocalDate> endCol = new TableColumn<>("Fecha Fin");
        endCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getWarrantyEnd()));

        table.getColumns().addAll(idCol, invoiceCol, clientCol, productCol, statusCol, countryCol, startCol, endCol);

        refreshButton.setOnAction(e -> loadGuarantees(table, null, null, null));

        exitButton.setOnAction(e -> stage.close());

        searchButton.setOnAction(e -> {
            String client = clientFilter.getText().isEmpty() ? null : clientFilter.getText();
            String status = statusFilter.getValue() == null || statusFilter.getValue().isEmpty() ? null : statusFilter.getValue();
            String country = countryFilter.getText().isEmpty() ? null : countryFilter.getText();
            loadGuarantees(table, client, status, country);
        });

        newGuaranteeButton.setOnAction(e -> {
            GuaranteeFormController formController = new GuaranteeFormController(null);
            formController.showDialog();
            loadGuarantees(table, null, null, null);
        });

        editButton.setOnAction(e -> {
            Guarantee selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                GuaranteeFormController formController = new GuaranteeFormController(selected);
                formController.showDialog();
                loadGuarantees(table, null, null, null);
            } else {
                showErrorDialog("Please select a guarantee to edit");
            }
        });

        deleteButton.setOnAction(e -> {
            Guarantee selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                guaranteeService.deleteGuarantee(selected.getId());
                loadGuarantees(table, null, null, null);
            } else {
                showErrorDialog("Please select a guarantee to delete");
            }
        });

        vbox.getChildren().addAll(buttonBox, filterBox, table);
        Tab tab = new Tab("Garantías", vbox);
        tab.setClosable(false);
        return tab;
    }

    private void loadGuarantees(TableView<Guarantee> table, String client, String status, String country) {
        List<Guarantee> guarantees;
        if (client != null || status != null || country != null) {
            guarantees = guaranteeService.searchGuarantees(client, status, country);
        } else {
            guarantees = guaranteeService.getAllGuarantees();
        }
        ObservableList<Guarantee> observableGuarantees = FXCollections.observableArrayList(guarantees);
        table.setItems(observableGuarantees);
    }

    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
