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
    @FXML private ComboBox<String> borrowerNameComboBox;
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

        loadBorrowerNames();
    }

    private void loadBorrowerNames() {
        ObservableList<String> names = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            String query = "SELECT name FROM members";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
            borrowerNameComboBox.setItems(names);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to load borrower names: " + e.getMessage());
        }
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
        String borrowerName = borrowerNameComboBox.getValue();
        String category = categoryComboBox.getValue();
        Pair<Integer, String> selectedItem = itemNameComboBox.getValue();
        Date loanDate = Date.valueOf(loanDatePicker.getValue());
        Date returnDate = Date.valueOf(returnDatePicker.getValue());
        String status = LocalDate.now().isAfter(returnDate.toLocalDate()) ? "Overdue" : "Valid";

        if (validateInput(borrowerName, selectedItem)) {
            try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
                String insertQuery = "INSERT INTO item_loans (item_id, member_name, category, loan_date, due_date, status) " + "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setInt(1, selectedItem.getKey());
                    stmt.setString(2, borrowerName);
                    stmt.setString(3, category);
                    stmt.setDate(4, loanDate);
                    stmt.setDate(5, returnDate);
                    stmt.setString(6, status);

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

    private boolean updateItemQuantity(int itemId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            String selectQuery = "SELECT quantity FROM items WHERE item_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setInt(1, itemId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int currentQuantity = rs.getInt("quantity");

                    if (currentQuantity <= 0) {
                        showAlert("Item quantity is already 0 or less. Cannot update.");
                        return false;
                    }
                    String updateQuery = "UPDATE items SET quantity = quantity - 1 WHERE item_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, itemId);
                        updateStmt.executeUpdate();
                    }

                    showAlert("Item quantity updated successfully.");
                } else {
                    showAlert("Item not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database error: " + e.getMessage());
        }
        return true;
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
        borrowerNameComboBox.getSelectionModel().clearSelection();
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