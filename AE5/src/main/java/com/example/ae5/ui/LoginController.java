package com.example.ae5.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {
    private Stage stage;

    public LoginController(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12;");

        Label titleLabel = new Label("AE5 - Sistema de Garantías");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label usernameLabel = new Label("Usuario:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Ingrese usuario");

        Label passwordLabel = new Label("Contraseña:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Ingrese contraseña");

        Button loginButton = new Button("Iniciar Sesión");
        loginButton.setStyle("-fx-font-size: 14; -fx-padding: 10;");
        loginButton.setPrefWidth(150);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Simple authentication (in production, use a proper auth service)
            if ("admin".equals(username) && "admin".equals(password)) {
                MainViewController mainView = new MainViewController(stage);
                Scene mainScene = mainView.createScene();
                stage.setScene(mainScene);
            } else {
                errorLabel.setText("Credenciales inválidas");
            }
        });

        root.getChildren().addAll(
                titleLabel,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                loginButton,
                errorLabel
        );

        return new Scene(root, 400, 500);
    }
}
