package org.example.fichajes;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.fichajes.dao.FichajeDAO;
import org.example.fichajes.dao.TrabajadorDAO;
import org.example.fichajes.model.Fichaje;
import org.example.fichajes.model.Trabajador;
import org.example.fichajes.util.WeatherService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class FicharController {

    @FXML
    private ComboBox<Trabajador> comboTrabajador;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private DatePicker dateFecha;

    @FXML
    private TextField txtHora;

    @FXML
    private TextField txtMinuto;

    @FXML
    private RadioButton radioEntrada;

    @FXML
    private RadioButton radioSalida;

    @FXML
    private ToggleGroup tipoFichaje;

    @FXML
    private TextField txtTemperatura;

    @FXML
    private TextField txtClima;

    @FXML
    private TextArea txtObservaciones;

    @FXML
    private Button btnObtenerClima;

    @FXML
    private Button btnFichar;

    @FXML
    private Label lblStatus;

    private TrabajadorDAO trabajadorDAO;
    private FichajeDAO fichajeDAO;

    @FXML
    private void initialize() {
        trabajadorDAO = new TrabajadorDAO();
        fichajeDAO = new FichajeDAO();

        cargarTrabajadores();

        dateFecha.setValue(LocalDate.now());
        LocalTime now = LocalTime.now();
        txtHora.setText(String.format("%02d", now.getHour()));
        txtMinuto.setText(String.format("%02d", now.getMinute()));

        radioEntrada.setSelected(true);

        obtenerClima();
    }

    private void cargarTrabajadores() {
        try {
            List<Trabajador> trabajadores = trabajadorDAO.getAllTrabajadores();
            comboTrabajador.setItems(FXCollections.observableArrayList(trabajadores));

            if (!trabajadores.isEmpty()) {
                comboTrabajador.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            showError("Error al cargar trabajadores: " + e.getMessage());
        }
    }

    @FXML
    private void onObtenerClimaClick() {
        obtenerClima();
    }

    private void obtenerClima() {
        try {
            WeatherService.WeatherData weather = WeatherService.getCurrentWeather();
            txtTemperatura.setText(String.format(Locale.US, "%.1f", weather.getTemperature()));
            txtClima.setText(weather.getDescription());
            lblStatus.setText("Clima actualizado");
            lblStatus.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            showError("Error al obtener el clima: " + e.getMessage());
        }
    }

    @FXML
    private void onFicharClick() {
        System.out.println("=== INTENTANDO FICHAR ===");
        if (!validarCampos()) {
            System.out.println("Validación fallida");
            return;
        }

        try {
            Trabajador trabajador = comboTrabajador.getValue();
            LocalDate fecha = dateFecha.getValue();
            System.out.println("Fecha seleccionada: " + fecha);
            System.out.println("Trabajador: " + trabajador.getNombreCompleto());

            int hora = Integer.parseInt(txtHora.getText());
            int minuto = Integer.parseInt(txtMinuto.getText());
            LocalTime time = LocalTime.of(hora, minuto);

            Fichaje.TipoFichaje tipo = radioEntrada.isSelected() ?
                    Fichaje.TipoFichaje.ENTRADA : Fichaje.TipoFichaje.SALIDA;

            Fichaje fichaje = new Fichaje(trabajador, fecha, time, tipo);

            if (!txtTemperatura.getText().isEmpty()) {
                String tempStr = txtTemperatura.getText().replace(",", ".");
                fichaje.setTemperatura(Double.parseDouble(tempStr));
            }

            fichaje.setDescripcionClima(txtClima.getText());
            fichaje.setObservaciones(txtObservaciones.getText());

            int id = fichajeDAO.insertFichaje(fichaje);

            if (id > 0) {
                showInfo("Fichaje registrado correctamente",
                        String.format("%s - %s\n%s a las %s",
                                trabajador.getNombreCompleto(),
                                tipo.name(),
                                fecha,
                                time));

                limpiarCampos();

                LocalTime nowTime = LocalTime.now();
                txtHora.setText(String.format("%02d", nowTime.getHour()));
                txtMinuto.setText(String.format("%02d", nowTime.getMinute()));

                lblStatus.setText("Fichaje registrado exitosamente");
                lblStatus.setStyle("-fx-text-fill: green;");
            } else {
                showError("No se pudo registrar el fichaje");
            }

        } catch (NumberFormatException e) {
            showError("Error en formato de numeros: " + e.getMessage());
        } catch (Exception e) {
            showError("Error al fichar: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (comboTrabajador.getValue() == null) {
            showError("Debe seleccionar un trabajador");
            return false;
        }

        Trabajador trabajador = comboTrabajador.getValue();
        String password = txtPassword.getText();
        
        if (password == null || password.isEmpty()) {
            showError("Debe introducir su contraseña");
            return false;
        }
        
        if (!trabajador.verificarPassword(password)) {
            showError("Contraseña incorrecta");
            return false;
        }

        if (dateFecha.getValue() == null) {
            showError("Debe seleccionar una fecha");
            return false;
        }

        try {
            int hora = Integer.parseInt(txtHora.getText());
            int minuto = Integer.parseInt(txtMinuto.getText());

            if (hora < 0 || hora > 23) {
                showError("La hora debe estar entre 0 y 23");
                return false;
            }

            if (minuto < 0 || minuto > 59) {
                showError("Los minutos deben estar entre 0 y 59");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Hora y minuto deben ser numeros validos");
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        txtPassword.clear();
        txtObservaciones.clear();
        obtenerClima();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        lblStatus.setText("Error: " + message);
        lblStatus.setStyle("-fx-text-fill: red;");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

