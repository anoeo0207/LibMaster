package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Models.Form;
import com.example.libmaster.Models.Documents.Book;  // Assuming Book class exists
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddNewLoanController extends Form {

    @FXML
    private ComboBox<String> borrowerComboBox;

    @FXML
    private TextField isbnField;

    @FXML
    private DatePicker loanDatePicker;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private TextArea commentsArea;

    @FXML
    private Button btn_addLoan;

    public void initialize() {
        List<String> members = fetchMembersFromDatabase();
        ObservableList<String> observableMembers = FXCollections.observableArrayList(members);

        FilteredList<String> filteredMembers = new FilteredList<>(observableMembers);
        borrowerComboBox.setItems(filteredMembers);

        borrowerComboBox.setEditable(true);
        borrowerComboBox.setOnKeyTyped(event -> {
            String filter = borrowerComboBox.getEditor().getText();
            filteredMembers.setPredicate(item -> item.toLowerCase().contains(filter.toLowerCase()));
        });

        btn_addLoan.setOnAction(event -> handleAddLoan());
    }

    private List<String> fetchMembersFromDatabase() {
        List<String> memberNames = new ArrayList<>();
        String query = "SELECT name FROM members";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                memberNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberNames;
    }

    @FXML
    private void handleAddLoan() {
        String isbn = isbnField.getText().trim();
        String borrower = borrowerComboBox.getValue();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();
        String comments = commentsArea.getText().trim();

        if (isbn.isEmpty() || borrower == null || loanDate == null || returnDate == null) {
            showAlert("Please fill in all required fields.");
            return;
        }

        if (loanDate.isAfter(returnDate)) {
            showAlert("Loan date must be before or equal to return date.");
            return;
        }

        // Validate borrower
        if (!isBorrowerValid(borrower)) {
            showAlert("The selected borrower does not exist in the database.");
            return;
        }

        // Validate ISBN
        if (!isIsbnValid(isbn)) {
            showAlert("The entered ISBN does not exist in the books database.");
            return;
        }

        // Check if the book is available for loan (using Book.isAvailable method)
        if (!Book.quantityAvailable(isbn)) {
            showAlert("This book is currently not available for loan (out of stock).");
            return;
        }

        // Insert into books_loan
        String query = "INSERT INTO book_loans (isbn, borrower_name, loan_date, return_date, comments) VALUES (?, ?, ?, ?, ?)";
        String query_2 = "UPDATE books SET quantity = quantity - 1 WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query); PreparedStatement stmt = conn.prepareStatement(query_2)) {

            pstmt.setString(1, isbn);
            pstmt.setString(2, borrower);
            pstmt.setDate(3, Date.valueOf(loanDate));
            pstmt.setDate(4, Date.valueOf(returnDate));
            pstmt.setString(5, comments);

            stmt.setString(1, isbn);
            stmt.executeUpdate();

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                showAlert("The loan has been successfully recorded.");
                clearForm();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("An error occurred while saving the loan.");
        }
    }

    private boolean isBorrowerValid(String borrowerName) {
        String query = "SELECT COUNT(*) FROM members WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, borrowerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isIsbnValid(String isbn) {
        String query = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText("New Message");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void clearForm() {
        isbnField.clear();
        borrowerComboBox.getSelectionModel().clearSelection();
        borrowerComboBox.getEditor().clear();
        loanDatePicker.setValue(null);
        returnDatePicker.setValue(null);
        commentsArea.clear();
    }
}
