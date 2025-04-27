package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddBookController {

    private static final String URL = "jdbc:mysql://localhost:3306/LibMasterServer";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML private TextField isbnField;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField quantityField;
    @FXML private TextArea descriptionArea;
    @FXML private Button btn_addBook;
    @FXML private Button btn_selectImage;
    @FXML private ImageView coverPreview;

    private String selectedImagePath = null;

    @FXML
    private void initialize() {
        categoryBox.getItems().addAll("Fiction", "Non-Fiction", "Science", "History", "Fantasy", "Other");
        btn_selectImage.setOnAction(event -> chooseImage());
        btn_addBook.setOnAction(event -> addBookToDatabase());
    }

    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(btn_selectImage.getScene().getWindow());
        if (selectedFile != null) {
            selectedImagePath = selectedFile.toURI().toString(); // Save the URL path
            coverPreview.setImage(new javafx.scene.image.Image(selectedImagePath));
        }
    }

    private void addBookToDatabase() {
        String isbn = isbnField.getText();
        String title = titleField.getText();
        String author = authorField.getText();
        String category = categoryBox.getValue();
        String quantityStr = quantityField.getText();
        String description = descriptionArea.getText();
        String imageUrl = selectedImagePath != null ? selectedImagePath : "";

        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || category == null || quantityStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all required fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantity must be a number.");
            return;
        }

        String sql = "INSERT INTO books (isbn, title, author, category, quantity, published_date, image, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setString(4, category);
            stmt.setInt(5, quantity);
            stmt.setString(6, java.time.LocalDate.now().toString()); // published_date as today
            stmt.setString(7, imageUrl);
            stmt.setString(8, description);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Book added successfully!");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to add book.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }

    private void clearForm() {
        isbnField.clear();
        titleField.clear();
        authorField.clear();
        categoryBox.getSelectionModel().clearSelection();
        quantityField.clear();
        coverPreview.setImage(null);
        selectedImagePath = null;
        descriptionArea.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleReturnBtn(ActionEvent event) throws IOException {
        Main.changeScene("library.fxml");
    }
}
