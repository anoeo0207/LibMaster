package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import java.sql.*;

import java.io.IOException;

public class DashboardController {

    private static final String URL = "jdbc:mysql://localhost:3306/LibMasterServer";
    private static final String USER = "root";
    private static final String PASSWORD = "";

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

    // JavaFX call this method automatically
    @FXML
    public void initialize() {
        updateTotalBooksCard();
        updateTotalRequestsCard();
        updateTotalMembersCard();
        updateTotalLoanCard();
    }
}
