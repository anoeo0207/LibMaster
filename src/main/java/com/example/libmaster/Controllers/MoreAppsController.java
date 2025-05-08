package com.example.libmaster.Controllers;

import javafx.fxml.FXML;

import java.awt.*;
import java.net.URI;

public class MoreAppsController {
    @FXML
    private void visitAppOne() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/anoeo0207/LibMaster"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void visitAppTwo() {
        try {
            Desktop.getDesktop().browse(new URI("https://pay-pilot-official-git-testmaster-hai-ans-projects.vercel.app/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void visitAppThree() {
        try {
            Desktop.getDesktop().browse(new URI("https://coral-cinda-53.tiiny.site/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
