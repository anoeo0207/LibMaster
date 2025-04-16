package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
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

        // Kiểm tra thông tin đăng nhập
        if (username.equals("admin") && password.equals("12345")) {
            // Chuyển sang trang Dashboard
            Main.changeScene("dashboard.fxml");
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
