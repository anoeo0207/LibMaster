package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Models.Documents.Book;
import com.example.libmaster.Models.Loan;
import com.example.libmaster.Models.Person.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditBookController {
    @FXML
    private TextField titleField, authorField, isbnField, categoryField, quantityField, imageUrlField;
    @FXML private TextArea descriptionField;
    @FXML private Button chooseImageBtn;
    @FXML private ImageView imageView;

    private Book book;

    public void setBook(Book book) {
        this.book = book;
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        categoryField.setText(book.getCategory());
        quantityField.setText(String.valueOf(book.getQuantity()));
        descriptionField.setText(book.getDescription());
        imageUrlField.setText(book.getImage());
    }

    @FXML
    private void handleSave() {
        book.setTitle(titleField.getText());
        book.setAuthor(authorField.getText());
        book.setCategory(categoryField.getText());
        book.setQuantity(Integer.parseInt(quantityField.getText()));
        book.setDescription(descriptionField.getText());
        book.setImage(imageUrlField.getText());

        String sql = "UPDATE books SET title=?, author=?, category=?, quantity=?, description=?, image=? WHERE isbn=?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCategory());
            stmt.setInt(4, book.getQuantity());
            stmt.setString(5, book.getDescription());
            stmt.setString(6, book.getImage());
            stmt.setString(7, book.getIsbn());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((Stage) titleField.getScene().getWindow()).close();
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        Stage stage = (Stage) chooseImageBtn.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            imageUrlField.setText(file.getAbsolutePath());
        }
    }

    public void handleReturnBtn(ActionEvent actionEvent) {
        ((Stage) titleField.getScene().getWindow()).close();
    }

    public void setMember(Member selectedMember) {

    }

    public void setLoan(Loan selectedLoan) {

    }
}
