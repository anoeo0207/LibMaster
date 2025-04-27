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
    private void handleAddBookChoiceBtn(MouseEvent event) throws IOException {
        Main.changeScene("addBookChoice.fxml");
        System.out.println("Button Clicked!");
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

    // JavaFX call this method automatically
    @FXML
    public void initialize() {
        updateTotalBooksCard();
    }
}
