package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Documents.Magazine;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.sql.*;

public class MagazineTableController {

    @FXML
    private TableView<Magazine> MagazineTableView;
    @FXML
    private TableColumn<Magazine, String> idColumn;
    @FXML
    private TableColumn<Magazine, String> titleColumn;
    @FXML
    private TableColumn<Magazine, String> authorColumn;
    @FXML
    private TableColumn<Magazine, String> publisherColumn;
    @FXML
    private TableColumn<Magazine, String> quantityColumn;
    @FXML
    private TableColumn<Magazine, String> descriptionColumn;
    @FXML
    private TextField searchField;

    public void handleAddMagazineBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("addMagazine.fxml");
    }

    public void handleRemoveMagazineBtn(ActionEvent actionEvent) {

    }

    private void deleteMagazineFromDatabase(String id) {
        String sql = "DELETE FROM items WHERE item_id = ? AND category = 'Magazine'";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Magazine with ID " + id + " deleted.");
                fetchMagazine("");
            } else {
                System.out.println("No magazine deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addContextMenuToTable() {
        MagazineTableView.setRowFactory(tv -> {
            TableRow<Magazine> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                Magazine selectedMagazine = row.getItem();
                if (selectedMagazine != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Confirmation");
                    alert.setHeaderText("Are you sure you want to delete this magazine?");
                    alert.setContentText("Magazine: " + selectedMagazine.getTitle());
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteMagazineFromDatabase(selectedMagazine.getId());
                        }
                    });
                }
            });

            contextMenu.getItems().addAll(deleteItem);
            row.setContextMenu(contextMenu);

            return row;
        });
    }

    public void fetchMagazine(String keyword) {
        ObservableList<Magazine> magazines = FXCollections.observableArrayList();
        String sql = "SELECT item_id, title, author, publisher, quantity, description FROM items WHERE title LIKE ? AND category = 'Magazine'";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("item_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                int quantity = rs.getInt("quantity");
                String description = rs.getString("description");

                magazines.add(new Magazine(id, title, author, publisher, quantity, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MagazineTableView.setItems(magazines);
    }

    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        publisherColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPublisher()));
        quantityColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantity())));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        searchField.setOnAction(event -> {
            String keyword = searchField.getText().trim();
            fetchMagazine(keyword.isEmpty() ? "" : keyword);
        });

        addContextMenuToTable();
        fetchMagazine("");
    }

    @FXML
    private void handleRemoveDVDBtn(ActionEvent actionEvent) {

    }
}
