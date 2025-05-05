package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class AddItemChoiceController {

    @FXML
    private void handleAddDVD(ActionEvent event) throws IOException {
        Main.changeScene("addDVD.fxml");
    }

    @FXML
    private void handleAddThesis(ActionEvent event) throws IOException {
        Main.changeScene("addThesis.fxml");
    }

    @FXML
    private void handleAddMagazine(ActionEvent event) throws IOException {
        Main.changeScene("addMagazine.fxml");
    }

}
