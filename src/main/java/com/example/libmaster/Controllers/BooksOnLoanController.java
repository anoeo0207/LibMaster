package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Loan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.*;

public class BooksOnLoanController {

    @FXML private TableView<Loan> loanTableView;
    @FXML private TableColumn<Loan, String> isbnColumn;
    @FXML private TableColumn<Loan, String> borrowerColumn;
    @FXML private TableColumn<Loan, String> loanDateColumn;
    @FXML private TableColumn<Loan, String> returnDateColumn;
    @FXML private TableColumn<Loan, String> commentColumn;
    @FXML private TextField searchField;
    @FXML private Button handleRemoveLoanBtn;

    private final ObservableList<Loan> loans = FXCollections.observableArrayList();

    public void initialize() {
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrower"));
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        loadLoanData();
        onSearch();
    }

    private void loadLoanData() {
        String sql = "SELECT bl.isbn, m.name AS borrower_name, bl.loan_date, bl.return_date, bl.comments, m.id AS member_id " +
                "FROM book_loans bl " +
                "JOIN members m ON bl.member_id = m.id";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            loans.clear();

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String borrower = rs.getString("borrower_name");
                String loanDate = rs.getString("loan_date");
                String returnDate = rs.getString("return_date");
                String comment = rs.getString("comments");
                int memberId = rs.getInt("member_id");

                loans.add(new Loan(isbn, borrower, loanDate, returnDate, comment, memberId));
            }

            loanTableView.setItems(loans);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddNewLoanBtn() throws IOException {
        Main.changeScene("addNewLoan.fxml");
    }

    public void fetchLoans(String keyword) {
        ObservableList<Loan> filteredLoans = FXCollections.observableArrayList();

        String sql = "SELECT bl.isbn, m.name AS borrower_name, bl.loan_date, bl.return_date, bl.comments, m.id AS member_id " +
                "FROM book_loans bl " +
                "JOIN members m ON bl.member_id = m.id " +
                "WHERE bl.isbn LIKE ? OR m.name LIKE ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchQuery = "%" + keyword + "%";
            stmt.setString(1, searchQuery);
            stmt.setString(2, searchQuery);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("isbn");
                    String borrower = rs.getString("borrower_name");
                    String loanDate = rs.getString("loan_date");
                    String returnDate = rs.getString("return_date");
                    String comment = rs.getString("comments");
                    int memberId = rs.getInt("member_id");

                    filteredLoans.add(new Loan(isbn, borrower, loanDate, returnDate, comment, memberId));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        loanTableView.setItems(filteredLoans);
    }

    @FXML
    private void onSearch() {
        String param = searchField.getText().trim();
        if (!param.isEmpty()) {
            fetchLoans(param);
        } else {
            loadLoanData();
        }
    }

    @FXML
    private void handleRemoveLoanBtn(ActionEvent event) {
        Loan selectedLoan = loanTableView.getSelectionModel().getSelectedItem();

        if (selectedLoan == null) {
            showAlert("No selection", "Please select a loan to delete.", Alert.AlertType.WARNING);
            return;
        }

        deleteLoanFromDatabase(selectedLoan);
        loadLoanData();
    }

    private void deleteLoanFromDatabase(Loan loan) {
        String deleteSql = "DELETE FROM book_loans WHERE isbn = ? AND member_id = ?";
        String updateSql = "UPDATE books SET quantity = quantity + 1 WHERE isbn = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            deleteStmt.setString(1, loan.getBookTitle());
            deleteStmt.setInt(2, loan.getMemberId());

            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                updateStmt.setString(1, loan.getBookTitle());
                updateStmt.executeUpdate();

                showAlert("Success", "Loan record deleted and book quantity updated.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "No loan record was deleted.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete loan record.", Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
