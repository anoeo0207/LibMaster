package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Form;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.sql.*;

public class AddItemLoanController extends Form {
    @FXML private TextField borrowerNameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<Pair<Integer, String>> itemNameComboBox;
    @FXML private DatePicker loanDatePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private TextArea notesField;

    @FXML
    public void initialize() {
        loanDatePicker.setValue(LocalDate.now());
        returnDatePicker.setValue(LocalDate.now().plusWeeks(2));
        categoryComboBox.setItems(FXCollections.observableArrayList("DVD", "Magazine", "Thesis"));
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateItemNamesFromDatabase(newVal);
        });
        categoryComboBox.getSelectionModel().selectFirst();

        itemNameComboBox.setCellFactory(param -> new ListCell<Pair<Integer, String>>() {
            @Override
            protected void updateItem(Pair<Integer, String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getValue());
                }
            }
        });

        itemNameComboBox.setButtonCell(new ListCell<Pair<Integer, String>>() {
            @Override
            protected void updateItem(Pair<Integer, String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getValue());
                }
            }
        });
    }

    private void updateItemNamesFromDatabase(String category) {
        itemNameComboBox.getItems().clear();

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            String query = "";

            switch (category) {
                case "DVD":
                    query = "SELECT item_id, title FROM items WHERE category = 'DVD' AND quantity > 0";
                    break;
                case "Magazine":
                    query = "SELECT item_id, title FROM items WHERE category = 'Magazine' AND quantity > 0";
                    break;
                case "Thesis":
                    query = "SELECT item_id, title FROM items WHERE category = 'Thesis' AND quantity > 0";
                    break;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                ObservableList<Pair<Integer, String>> items = FXCollections.observableArrayList();
                while (rs.next()) {
                    items.add(new Pair<>(rs.getInt("item_id"), rs.getString("title")));
                }

                itemNameComboBox.setItems(items);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to fetch items from database: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddLoan() {
        String borrowerName = borrowerNameField.getText();
        String category = categoryComboBox.getValue();
        Pair<Integer, String> selectedItem = itemNameComboBox.getValue();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (validateInput(borrowerName, selectedItem)) {
            try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
                String insertQuery = "INSERT INTO item_loans (item_id, member_name, category, loan_date, due_date) " + "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setInt(1, selectedItem.getKey());
                    stmt.setString(2, borrowerName);
                    stmt.setString(3, category);
                    stmt.setDate(4, Date.valueOf(loanDate));
                    stmt.setDate(5, Date.valueOf(returnDate));

                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        updateItemQuantity(selectedItem.getKey());
                        showAlert("The loan record has been saved.");
                        clearForm();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Failed to save loan record: " + e.getMessage());
            }
        }
    }

    private void updateItemQuantity(int itemId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            String updateQuery = "UPDATE items SET quantity = quantity - 1 WHERE item_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setInt(1, itemId);
                stmt.executeUpdate();
            }
        }
    }

    private boolean validateInput(String borrowerName, Pair<Integer, String> selectedItem) {
        if (borrowerName == null || borrowerName.trim().isEmpty()) {
            showAlert("Please enter borrower name");
            return false;
        }
        if (selectedItem == null) {
            showAlert("Please select an item");
            return false;
        }
        return true;
    }

    @Override
    public void clearForm() {
        borrowerNameField.clear();
        categoryComboBox.getSelectionModel().selectFirst();
        loanDatePicker.setValue(LocalDate.now());
        returnDatePicker.setValue(LocalDate.now().plusWeeks(2));
        notesField.clear();
        itemNameComboBox.getSelectionModel().clearSelection();
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
        Main.changeScene("itemLoan.fxml");
    }
}