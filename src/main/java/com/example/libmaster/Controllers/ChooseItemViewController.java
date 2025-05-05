package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class ChooseItemViewController {

    @FXML
    private void handleViewDVD(ActionEvent event) throws IOException {
        Main.changeScene("DVDTable.fxml");
    }

    @FXML
    private void handleViewThesis(ActionEvent event) throws IOException {
        Main.changeScene("thesisTable.fxml");
    }

    @FXML
    private void handleViewMagazine(ActionEvent event) throws IOException {
        Main.changeScene("magazineTable.fxml");
    }

}
