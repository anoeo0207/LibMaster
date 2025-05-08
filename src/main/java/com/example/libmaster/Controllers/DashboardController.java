package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import com.example.libmaster.Models.Documents.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import java.sql.*;

import java.io.IOException;

public class DashboardController {

    private static final String URL = "jdbc:mysql://localhost:3306/LibMasterServer";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private TableView<Book> newBookTable;
    @FXML
    private TableColumn<Book, String> LMCode;
    @FXML
    private TableColumn<Book, String> title;
    @FXML
    private TableColumn<Book, String> author;
    @FXML
    private TableColumn<Book, String> category;

    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label totalRequestsLabel;
    @FXML
    private Label totalMembersLabel;
    @FXML
    private Label totalLoanLabel;

    @FXML
    private void handleAddBookChoiceBtn(MouseEvent event) throws IOException {
        Main.changeScene("addBookChoice.fxml");
    }

    @FXML
    private void handleAddMemberBtn(MouseEvent event) throws IOException {
        Main.changeScene("addMember.fxml");
    }

    @FXML
    private void handleAddLoanBtn(MouseEvent event) throws IOException {
        Main.changeScene("addNewLoan.fxml");
    }

    @FXML
    private void handleUseChatBotBtn(MouseEvent event) throws IOException {
        Main.changeScene("libroBot.fxml");
    }

    private void updateTotalBooksCard() {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int totalBooks = rs.getInt(1);
                totalBooksLabel.setText(String.valueOf(totalBooks));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalRequestsCard() {
        String sql = "SELECT COUNT(*) FROM requests";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int totalRequests = rs.getInt(1);
                totalRequestsLabel.setText(String.valueOf(totalRequests));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalMembersCard() {
        String sql = "SELECT COUNT(*) FROM members";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int totalMembers = rs.getInt(1);
                totalMembersLabel.setText(String.valueOf(totalMembers));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalLoanCard() {
        String sql = "SELECT COUNT(*) FROM book_loans";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int totalLoan = rs.getInt(1);
                totalLoanLabel.setText(String.valueOf(totalLoan));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchBooksData() {
        ObservableList<Book> books = FXCollections.observableArrayList();

        String sql = "SELECT isbn, title, author, category FROM books ORDER BY id DESC LIMIT 5";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String lmCode = rs.getString("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String category = rs.getString("category");

                books.add(new Book(lmCode, title, author, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        newBookTable.setItems(books);
    }

    private void initializeTableColumns() {
        LMCode.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
    }

    @FXML
    public void initialize() {
        updateTotalBooksCard();
        updateTotalRequestsCard();
        updateTotalMembersCard();
        updateTotalLoanCard();
        initializeTableColumns();
        fetchBooksData();
    }
}
