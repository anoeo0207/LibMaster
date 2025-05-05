package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import com.example.libmaster.Models.Form;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.libmaster.Config.DatabaseConfig.*;

public class AddDVDVController extends Form {
    @FXML private TextField titleField;
    @FXML private TextField directorField;
    @FXML private TextField durationField;
    @FXML private TextField quantityField;
    @FXML private TextArea descriptionField;
    @FXML private Button btn_addDVD;

    @FXML
    private void handleReturnBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("itemLoan.fxml");
    }

    @FXML
    private void initialize() {
        btn_addDVD.setOnAction(event -> addMemberToDatabase());
    }

    private void addMemberToDatabase() {
        String title = titleField.getText();
        String director = directorField.getText();
        String duration =  durationField.getText();
        String quantity = quantityField.getText();
        String description = descriptionField.getText();

        if (title.isEmpty() || duration.isEmpty() || quantity.isEmpty()) {
            showAlert("Please fill all required fields.");
            return;
        }

        String sql = "INSERT INTO items (title, director, duration, quantity, description, category) VALUES (?, ?, ?, ?, ?, 'DVD')";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, director);
            stmt.setString(3, duration);
            stmt.setString(4, quantity);
            stmt.setString(5, description);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("DVD added successfully!");
                clearForm();
            } else {
                showAlert("Failed to add DVD.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error: " + e.getMessage());
        }
    }

    @Override
    public void clearForm() {
        titleField.clear();
        directorField.clear();
        durationField.clear();
        quantityField.clear();
        descriptionField.clear();
    }

    @Override
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText("New Message");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
