package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Documents.Book;
import com.example.libmaster.Models.Loan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.xml.crypto.Data;
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

    private final ObservableList<Loan> loans = FXCollections.observableArrayList();

    public void initialize() {
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrower"));
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        loadLoanData();
    }

    private void loadLoanData() {
        String sql = "SELECT isbn, borrower_name, loan_date, return_date, comments FROM book_loans";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String borrower = rs.getString("borrower_name");
                String loanDate = rs.getString("loan_date");
                String returnDate = rs.getString("return_date");
                String comment = rs.getString("comments");

                loans.add(new Loan(isbn, borrower, loanDate, returnDate, comment));
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

        String sql = "SELECT isbn, borrower_name, loan_date, return_date, comments FROM book_loans WHERE " +
                "isbn LIKE ? OR borrower_name LIKE ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                        filteredLoans.add(new Loan(isbn, borrower, loanDate, returnDate, comment));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loanTableView.setItems(filteredLoans);
    }

    @FXML
    private void onSearch() {
        String param = searchField.getText();
        if (!param.isEmpty()) {
            fetchLoans(param);
        }
    }

}
