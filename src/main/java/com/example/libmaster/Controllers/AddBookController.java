package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class AddBookController {

    @FXML
    private void handleReturnBtn(ActionEvent event) throws IOException {
        Main.changeScene("library.fxml");
    }
}
