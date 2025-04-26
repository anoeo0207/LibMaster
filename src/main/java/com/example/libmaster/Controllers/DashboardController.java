package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class DashboardController {
    @FXML
    private void handleAddBookChoiceBtn(MouseEvent event) throws IOException {
        Main.changeScene("addBookChoice.fxml");
        System.out.println("Button Clicked!");
    }
}
