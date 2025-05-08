package com.example.libmaster.Controllers;

import com.example.libmaster.Models.Form;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.libmaster.Config.DatabaseConfig.*;

public class AddMemberController extends Form {
    @FXML private TextField identificationField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderBox;

    @FXML private Button btn_addMember;

    @FXML
    private void initialize() {
        genderBox.getItems().addAll("Male", "Female", "Custom");
        btn_addMember.setOnAction(event -> addMemberToDatabase());
    }

    private void addMemberToDatabase() {
        String identification = identificationField.getText();
        String fullName = nameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneField.getText();
        String dateOfBirth = String.valueOf(dobPicker.getValue());
        String gender = genderBox.getValue();

        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || dateOfBirth.isEmpty() || gender.isEmpty()) {
            showAlert("Please fill all required fields.");
            return;
        }

        String sql = "INSERT INTO members (name, email, phone, date_of_birth, gender, identification_num) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, dateOfBirth);
            stmt.setString(5, gender);
            stmt.setString(6, identification);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Member added successfully!");
                clearForm();
            } else {
                showAlert("Failed to add member.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error: " + e.getMessage());
        }
    }

    @Override
    public void clearForm() {
        identificationField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        genderBox.getSelectionModel().clearSelection();
        dobPicker.setValue(null);
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
