package com.example.libmaster.Controllers;

import com.example.libmaster.Models.Person.Member;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MemberInfoController {
    @FXML private TextField idField;
    @FXML private Label nameLabel;
    @FXML private TextField dobField;
    @FXML private Label genderLabel;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField quantityField;

    public void setMember(Member member) {
        idField.setText(String.valueOf(member.getId()));
        nameLabel.setText(member.getName());
        dobField.setText(member.getDateOfBirth());
        genderLabel.setText(member.getGender());
        phoneField.setText(member.getPhone());
        emailField.setText(member.getEmail());
        quantityField.setText(String.valueOf(member.getBookBorrowed()));
    }
}
