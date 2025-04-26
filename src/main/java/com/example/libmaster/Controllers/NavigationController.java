package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class NavigationController {
    @FXML
    private void handleDashboardButton(ActionEvent event) throws IOException {
        Main.changeScene("dashboard.fxml");
    }

    @FXML
    private void handleLibraryButton(ActionEvent event) throws IOException {
        Main.changeScene("library.fxml");
    }

    @FXML
    private void handleMemberListButton(ActionEvent event) throws IOException {
        Main.changeScene("member.fxml");
    }

    @FXML
    private void handleRequestButton(ActionEvent event) throws IOException {
        Main.changeScene("request.fxml");
    }

    @FXML
    private void handleLogOutButton(ActionEvent event) throws IOException {
        Main.changeScene("login.fxml");
    }

    @FXML
    private void handleBooksOnLoanButton(ActionEvent event) throws IOException {
        Main.changeScene("booksOnLoan.fxml");
    }
}
