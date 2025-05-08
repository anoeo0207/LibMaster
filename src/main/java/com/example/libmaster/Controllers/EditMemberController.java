package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Models.Person.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditMemberController {

    @FXML private TextField identificationField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Button saveButton;
    @FXML private Button returnButton;

    private Member member;

    public void initialize() {
        genderComboBox.getItems().addAll("Male", "Female", "Custom");
    }

    public void setMember(Member member) {
        this.member = member;
        identificationField.setText(member.getIdentification());
        nameField.setText(member.getName());
        genderComboBox.setValue(member.getGender());
        dateOfBirthPicker.setValue(LocalDate.parse(member.getDateOfBirth()));
        emailField.setText(member.getEmail());
        phoneField.setText(member.getPhone());
    }


    @FXML
    private void handleSaveBtn() {
        member.setIdentification(identificationField.getText());
        member.setName(nameField.getText());
        member.setGender(genderComboBox.getValue());
        member.setDateOfBirth(String.valueOf(dateOfBirthPicker.getValue()));
        member.setEmail(emailField.getText());
        member.setPhone(phoneField.getText());

        String sql = "UPDATE members SET name = ?, gender = ?, date_of_birth = ?, email = ?, phone = ?, identification_num = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getName());
            stmt.setString(2, member.getGender());
            stmt.setDate(3, java.sql.Date.valueOf(member.getDateOfBirth()));
            stmt.setString(4, member.getEmail());
            stmt.setString(5, member.getPhone());
            stmt.setString(6, member.getIdentification());
            stmt.setInt(7, member.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleReturnBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }
}
