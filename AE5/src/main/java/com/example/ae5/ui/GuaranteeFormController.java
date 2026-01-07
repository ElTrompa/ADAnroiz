package com.example.ae5.ui;

import com.example.ae5.model.Guarantee;
import com.example.ae5.service.GuaranteeService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class GuaranteeFormController {
    private GuaranteeService guaranteeService;
    private Guarantee guarantee;
    private Stage dialogStage;

    public GuaranteeFormController(Guarantee guarantee) {
        this.guarantee = guarantee;
        this.guaranteeService = new GuaranteeService();
    }

    public void showDialog() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(guarantee == null ? "Nueva Garantía" : "Editar Garantía");
        dialogStage.setWidth(500);
        dialogStage.setHeight(600);

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        // Invoice ID
        Label invoiceLabel = new Label("ID Factura:");
        TextField invoiceField = new TextField();
        invoiceField.setPromptText("ID Factura de Odoo");

        // Client
        Label clientLabel = new Label("Cliente:");
        TextField clientField = new TextField();
        clientField.setPromptText("Nombre del cliente");

        // Purchase Date
        Label purchaseDateLabel = new Label("Fecha de Compra:");
        DatePicker purchaseDatePicker = new DatePicker();

        // Address
        Label addressLabel = new Label("Dirección:");
        TextField addressField = new TextField();
        addressField.setPromptText("Dirección");

        // Country
        Label countryLabel = new Label("País:");
        TextField countryField = new TextField();
        countryField.setPromptText("País");

        // Warranty Start
        Label warrantyStartLabel = new Label("Fecha Inicio Garantía:");
        DatePicker warrantyStartPicker = new DatePicker();

        // Warranty End
        Label warrantyEndLabel = new Label("Fecha Fin Garantía:");
        DatePicker warrantyEndPicker = new DatePicker();

        // Status
        Label statusLabel = new Label("Estado:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("ACTIVA", "EXPIRADA", "CANCELADA");
        statusCombo.setValue("ACTIVA");

        // Product
        Label productLabel = new Label("Producto:");
        ComboBox<String> productCombo = new ComboBox<>();
        productCombo.setItems(FXCollections.observableArrayList(
                "Bicicleta",
                "Maillot",
                "Culote",
                "Rodillo",
                "Casco",
                "Pedales"
        ));
        productCombo.setValue("Bicicleta");

        // Description
        Label descriptionLabel = new Label("Descripción:");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(5);

        // Buttons
        Button saveButton = new Button("Guardar");
        Button cancelButton = new Button("Cancelar");

        saveButton.setOnAction(e -> {
            try {
                if (invoiceField.getText() == null || invoiceField.getText().trim().isEmpty()) {
                    showErrorDialog("El campo 'ID Factura' es obligatorio y debe ser un número.");
                    return;
                }
                long invoiceId;
                try {
                    invoiceId = Long.parseLong(invoiceField.getText().trim());
                } catch (NumberFormatException nfe) {
                    showErrorDialog("El 'ID Factura' no es un número válido.");
                    return;
                }

                Guarantee g = new Guarantee();
                g.setInvoiceId(invoiceId);
                g.setClient(clientField.getText());
                g.setPurchaseDate(purchaseDatePicker.getValue());
                g.setAddress(addressField.getText());
                g.setCountry(countryField.getText());
                g.setWarrantyStart(warrantyStartPicker.getValue());
                g.setWarrantyEnd(warrantyEndPicker.getValue());
                g.setStatus(statusCombo.getValue());
                g.setProduct(productCombo.getValue());
                g.setDescription(descriptionArea.getText());

                if (guarantee == null) {
                    Guarantee created = guaranteeService.createGuarantee(g);
                    if (created == null) {
                        showErrorDialog("No se pudo crear la garantía. Revisa el log para más detalles.");
                        return;
                    }
                } else {
                    g.setId(guarantee.getId());
                    Guarantee updated = guaranteeService.updateGuarantee(guarantee.getId(), g);
                    if (updated == null) {
                        showErrorDialog("No se pudo actualizar la garantía. Revisa el log para más detalles.");
                        return;
                    }
                }

                dialogStage.close();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error al guardar la garantía: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        cancelButton.setOnAction(e -> dialogStage.close());

        // Pre-fill if editing
        if (guarantee != null) {
            invoiceField.setText(guarantee.getInvoiceId().toString());
            clientField.setText(guarantee.getClient());
            purchaseDatePicker.setValue(guarantee.getPurchaseDate());
            addressField.setText(guarantee.getAddress());
            countryField.setText(guarantee.getCountry());
            warrantyStartPicker.setValue(guarantee.getWarrantyStart());
            warrantyEndPicker.setValue(guarantee.getWarrantyEnd());
            statusCombo.setValue(guarantee.getStatus());
            productCombo.setValue(guarantee.getProduct() != null ? guarantee.getProduct() : "Bicicleta");
            descriptionArea.setText(guarantee.getDescription() != null ? guarantee.getDescription() : "");
        }

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        root.getChildren().addAll(
                invoiceLabel, invoiceField,
                clientLabel, clientField,
                purchaseDateLabel, purchaseDatePicker,
                addressLabel, addressField,
                countryLabel, countryField,
                warrantyStartLabel, warrantyStartPicker,
                warrantyEndLabel, warrantyEndPicker,
                statusLabel, statusCombo,
                descriptionLabel, descriptionArea,
                buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(root);
        Scene scene = new Scene(scrollPane);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
