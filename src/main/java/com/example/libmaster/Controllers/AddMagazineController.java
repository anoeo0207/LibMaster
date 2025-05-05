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

public class AddMagazineController extends Form {
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField publisherField;
    @FXML private TextField quantityField;
    @FXML private TextArea descriptionField;
    @FXML private Button btn_addMagazine;

    @FXML
    private void handleReturnBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("itemLoan.fxml");
    }

    @FXML
    private void initialize() {
        btn_addMagazine.setOnAction(event -> addMagazineToDatabase());
    }

    private void addMagazineToDatabase() {
        String title = titleField.getText();
        String author = authorField.getText();
        String publisher =  publisherField.getText();
        String quantity = quantityField.getText();
        String description = descriptionField.getText();

        if (title.isEmpty() || quantity.isEmpty()) {
            showAlert("Please fill all required fields.");
            return;
        }

        String sql = "INSERT INTO items (title, author, publisher, quantity, description, category) VALUES (?, ?, ?, ?, ?, 'Magazine')";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, publisher);
            stmt.setString(4, quantity);
            stmt.setString(5, description);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Magazine added successfully!");
                clearForm();
            } else {
                showAlert("Failed to add magazine.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error: " + e.getMessage());
        }
    }

    @Override
    public void clearForm() {
        titleField.clear();
        authorField.clear();
        publisherField.clear();
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
