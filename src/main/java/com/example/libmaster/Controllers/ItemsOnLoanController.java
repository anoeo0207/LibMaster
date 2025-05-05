package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class ItemsOnLoanController {
    @FXML
    private void handleAddNewItemLoanBtn() throws IOException {
        Main.changeScene("addItemChoice.fxml");
    }

    public void handleChooseItemViewBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("chooseItemView.fxml");
    }
}
