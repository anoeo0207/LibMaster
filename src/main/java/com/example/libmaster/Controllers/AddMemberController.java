package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import com.example.libmaster.Models.Form;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

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

        if (!fullName.matches("[a-zA-Z\\s]+")) {
            showAlert("Name must only contain characters.");
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert("Phone number must only contain digits.");
            return;
        }

        LocalDate dateOfBirthLocal = LocalDate.parse(dateOfBirth);
        if (dateOfBirthLocal.isAfter(LocalDate.now())) {
            showAlert("Please enter valid date of birth");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Email must be in the format 'example@gmail.com'.");
            return;
        }

        if (isIdentificationDuplicate(identification)) {
            showAlert("Identification number already exists. Please use a unique ID.");
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

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("[0-9]+");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    }

    private boolean isIdentificationDuplicate(String identification) {
        String sql = "SELECT COUNT(*) FROM members WHERE identification_num = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identification);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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

    @FXML
    public void handleReturnBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("member.fxml");
    }
}
