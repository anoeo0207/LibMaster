package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Loan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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

        itemLoanTableView.setRowFactory(tv -> {
            TableRow<Loan> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Delete this loan");

            deleteItem.setOnAction(event -> {
                Loan selectedLoan = row.getItem();
                if (selectedLoan != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this loan?", ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText("Confirm Deletion");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            deleteLoanFromDatabase(selectedLoan.getLoanId(), selectedLoan.getItemName());
                            itemLoanTableView.getItems().remove(selectedLoan);
                        }
                    });
                }
            });

            contextMenu.getItems().add(deleteItem);
            row.setContextMenu(contextMenu);

            return row;
        });
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

    private void deleteLoanFromDatabase(int loanId, String itemName) {
        String sqlDeleteLoan = "DELETE FROM item_loans WHERE loan_id = ?";
        String sqlUpdateItemQuantity = "UPDATE items SET quantity = quantity + 1 WHERE title = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmtDeleteLoan = conn.prepareStatement(sqlDeleteLoan);
             PreparedStatement stmtUpdateItem = conn.prepareStatement(sqlUpdateItemQuantity)) {

            stmtDeleteLoan.setInt(1, loanId);
            stmtDeleteLoan.executeUpdate();

            stmtUpdateItem.setString(1, itemName);
            stmtUpdateItem.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
