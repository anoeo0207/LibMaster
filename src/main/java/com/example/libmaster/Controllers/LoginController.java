package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("admin") && password.equals("12345")) {
            Main.changeScene("dashboard.fxml");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Welcome back");
                alert.setHeaderText("Welcome back, Admin!");
                alert.setContentText("We're glad to have you back. Here's your dashboard overview for today.");
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
                alert.showAndWait();
            });
        } else {
            // Hiển thị thông báo lỗi nếu thông tin sai
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Invalid Username or Password");
            alert.setContentText("Please try again.");
            alert.showAndWait();
        }
    }
}
