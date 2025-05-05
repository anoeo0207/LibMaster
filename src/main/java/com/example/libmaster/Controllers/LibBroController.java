package com.example.libmaster.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LibBroController {
    @FXML
    private VBox chatContainer;
    @FXML
    private TextArea messageInput;

    @FXML
    private void sendMessage() throws IOException {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            addMessage(message, true);
            messageInput.clear();

            String botResponse = generateBotResponse(message);
            addMessage(botResponse, false);
        }
    }

    private void addMessage(String text, boolean isUser) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messageContainer.setPadding(new Insets(5));

        Label messageLabel = new Label(text);
        messageLabel.setPadding(new Insets(8));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(600);
        messageLabel.setFont(Font.font(16));

        if (isUser) {
            messageLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 15 15 0 15;");
        } else {
            messageLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-background-radius: 15 15 15 0;");
        }

        messageContainer.getChildren().add(messageLabel);
        chatContainer.getChildren().add(messageContainer);

        chatContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        chatContainer.layout();
        chatContainer.requestLayout();
    }

    private String generateBotResponse(String userMessage) throws IOException {
        return chatBotResponse(userMessage);
    }

    private String chatBotResponse(String promptText) throws IOException {
        String modelName = "gemma2:2b";

        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = String.format("{\"model\": \"%s\", \"prompt\":\"%s\", \"stream\": false}", modelName, promptText);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        System.out.println("Response Code: " + code);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder  response = new StringBuilder();
        String line;
        while((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        String responseText = jsonResponse.getString("response");
        System.out.println("Response: " + responseText);

        conn.disconnect();
        return responseText;
    }
}
