package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Loan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class ItemsOnLoanController {
    @FXML
    private void handleAddNewItemLoanBtn() throws IOException {
        Main.changeScene("addNewItemLoan.fxml");
    }

    @FXML
    private void handleChooseItemViewBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("chooseItemView.fxml");
    }

    @FXML
    private TableView<Loan> itemLoanTableView;

    @FXML
    private TableColumn<Loan, Integer> loanIdColumn;

    @FXML
    private TableColumn<Loan, String> itemNameColumn;

    @FXML
    private TableColumn<Loan, String> borrowerNameColumn;

    @FXML
    private TableColumn<Loan, LocalDate> loanDateColumn;

    @FXML
    private TableColumn<Loan, LocalDate> returnDateColumn;

    @FXML
    private TableColumn<Loan, String> statusColumn;

    @FXML
    public void initialize() {
        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        borrowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("itemLoanDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("itemReturnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadItemLoanData();
    }

    private void loadItemLoanData() {
        ObservableList<Loan> loans = FXCollections.observableArrayList();

        String sql = "SELECT il.loan_id, i.title, il.member_name, il.loan_date, il.due_date, il.status " +
                "FROM item_loans il " +
                "JOIN items i ON il.item_id = i.item_id";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int loanId = rs.getInt("loan_id");
                String itemName = rs.getString("title");
                String borrowerName = rs.getString("member_name");
                LocalDate loanDate = rs.getDate("loan_date").toLocalDate();
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                String status = rs.getString("status");

                loans.add(new Loan(loanId, itemName, borrowerName, loanDate, dueDate, status));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        itemLoanTableView.setItems(loans);
    }
}