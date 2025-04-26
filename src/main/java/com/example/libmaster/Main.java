package com.example.libmaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("addBookViaAPI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        stage.setResizable(false);
        stage.setWidth(1200);
        stage.setHeight(800);

        stage.setTitle("LibMaster - Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxml)));
        primaryStage.getScene().setRoot(pane);
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch();
    }
}