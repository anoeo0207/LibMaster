package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class AddBookChoiceController {

    @FXML
    private void handleCustomAddBtn(ActionEvent event) throws IOException {
        Main.changeScene("addBook.fxml");
    }

    @FXML
    private void handleAddViaAPIBtn(ActionEvent event) throws IOException {
        Main.changeScene("addBookViaAPI.fxml");
    }
}
