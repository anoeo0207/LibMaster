package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Documents.Book;
import com.example.libmaster.Models.Form;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry; // Thêm import này
import java.util.ArrayList;
import java.util.List;

public class AddNewLoanController extends Form {

    @FXML
    private ComboBox<SimpleEntry<Integer, String>> borrowerComboBox;

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
        List<SimpleEntry<Integer, String>> members = fetchMembersFromDatabase();
        ObservableList<SimpleEntry<Integer, String>> observableMembers = FXCollections.observableArrayList(members);
        FilteredList<SimpleEntry<Integer, String>> filteredMembers = new FilteredList<>(observableMembers);

        borrowerComboBox.setItems(filteredMembers);
        borrowerComboBox.setEditable(true);

        borrowerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SimpleEntry<Integer, String> entry) {
                return entry == null ? "" : entry.getValue();
            }

            @Override
            public SimpleEntry<Integer, String> fromString(String string) {
                return observableMembers.stream()
                        .filter(e -> e.getValue().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        borrowerComboBox.getEditor().setOnKeyTyped(event -> {
            String filter = borrowerComboBox.getEditor().getText().toLowerCase();
            filteredMembers.setPredicate(item -> item.getValue().toLowerCase().contains(filter));
        });

        btn_addLoan.setOnAction(event -> handleAddLoan());
    }

    private List<SimpleEntry<Integer, String>> fetchMembersFromDatabase() {
        List<SimpleEntry<Integer, String>> members = new ArrayList<>();
        String query = "SELECT id, name FROM members";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                members.add(new SimpleEntry<>(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    @FXML
    private void handleAddLoan() {
        String isbn = isbnField.getText().trim();
        SimpleEntry<Integer, String> selectedBorrower = borrowerComboBox.getValue();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();
        String comments = commentsArea.getText().trim();

        if (isbn.isEmpty() || selectedBorrower == null || loanDate == null || returnDate == null) {
            showAlert("Please fill in all required fields.");
            return;
        }

        if (loanDate.isAfter(returnDate)) {
            showAlert("Loan date must be before or equal to return date.");
            return;
        }

        if (!isBorrowerValid(selectedBorrower.getKey())) {
            showAlert("The selected borrower does not exist in the database.");
            return;
        }

        if (!isIsbnValid(isbn)) {
            showAlert("The entered ISBN does not exist in the books database.");
            return;
        }

        if (!Book.quantityAvailable(isbn)) {
            showAlert("This book is currently not available for loan (out of stock).");
            return;
        }

        String query = "INSERT INTO book_loans (isbn, member_id, loan_date, return_date, comments) VALUES (?, ?, ?, ?, ?)";
        String query_2 = "UPDATE books SET quantity = quantity - 1 WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             PreparedStatement stmt = conn.prepareStatement(query_2)) {

            pstmt.setString(1, isbn);
            pstmt.setInt(2, selectedBorrower.getKey()); // dùng member_id
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

    private boolean isBorrowerValid(int memberId) {
        String query = "SELECT COUNT(*) FROM members WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, memberId);
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

    @FXML
    private void handleReturnBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("booksOnLoan.fxml");
    }
}
