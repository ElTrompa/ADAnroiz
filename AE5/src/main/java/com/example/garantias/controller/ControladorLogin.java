package com.example.garantias.controller;

import com.example.garantias.model.SesionOdoo;
import com.example.garantias.service.ServicioOdoo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ControladorLogin {
    @FXML
    private TextField tfUsuario;

    @FXML
    private PasswordField pfContrasena;

    @FXML
    private TextField tfDb;

    @FXML
    private Button btnIniciar;

    @FXML
    public void initialize() {
        // Pre-cargar DB desde properties
        try {
            java.util.Properties props = new java.util.Properties();
            try (var is = getClass().getResourceAsStream("/application.properties")) {
                props.load(is);
                String db = props.getProperty("odoo.db", "odoo");
                tfDb.setText(db);
            }
        } catch (Exception ignored) {}
    }

    @FXML
    private void onIniciarSesion() {
        String usuario = tfUsuario.getText();
        String password = pfContrasena.getText();
        String db = tfDb.getText();
        if (db == null || db.isBlank()) db = "odoo";
        try {
            ServicioOdoo servicio = new ServicioOdoo("http://localhost:8069", db);
            boolean ok = servicio.autenticar(usuario, password);
            if (ok) {
                SesionOdoo.getInstancia().setBaseDatos(db);
                // Abrir vista principal
                javafx.stage.Stage stage = (javafx.stage.Stage) btnIniciar.getScene().getWindow();
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/garantias/view/vista-principal.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                stage.setScene(scene);
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR, "Autenticación fallida. Revisa usuario/contraseña y el nombre de la base de datos.");
                a.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "Error al conectar con Odoo: " + e.getMessage());
            a.showAndWait();
        }
    }
}