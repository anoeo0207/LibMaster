package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Documents.DVD;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DVDTableController {

    @FXML
    private TableView<DVD> DVDTableView;
    @FXML
    private TableColumn<DVD, String> idColumn;
    @FXML
    private TableColumn<DVD, String> titleColumn;
    @FXML
    private TableColumn<DVD, String> directorColumn;
    @FXML
    private TableColumn<DVD, String> durationColumn;
    @FXML
    private TableColumn<DVD, String> quantityColumn;
    @FXML
    private TableColumn<DVD, String> descriptionColumn;
    @FXML
    private TextField searchField;

    public void handleAddDVDBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("addDVD.fxml");
    }

    public void handleRemoveDVDBtn(ActionEvent actionEvent) {
        // Optional: implement if needed
    }

    private void deleteDVDFromDatabase(String id) {
        String sql = "DELETE FROM items WHERE item_id = ? AND category = 'DVD'";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("DVD with ID " + id + " deleted.");
                fetchDVDs("");
            } else {
                System.out.println("No DVD deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addContextMenuToTable() {
        DVDTableView.setRowFactory(tv -> {
            TableRow<DVD> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                DVD selectedDVD = row.getItem();
                if (selectedDVD != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Confirmation");
                    alert.setHeaderText("Are you sure you want to delete this DVD?");
                    alert.setContentText("DVD: " + selectedDVD.getTitle());
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteDVDFromDatabase(selectedDVD.getId());
                        }
                    });
                }
            });

            contextMenu.getItems().addAll(deleteItem);
            row.setContextMenu(contextMenu);

            return row;
        });
    }

    public void fetchDVDs(String keyword) {
        ObservableList<DVD> dvds = FXCollections.observableArrayList();
        String sql = "SELECT item_id, title, director, duration, quantity, description FROM items WHERE title LIKE ? AND category = 'DVD'";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("item_id");
                String title = rs.getString("title");
                String director = rs.getString("director");
                String duration = rs.getString("duration");
                int quantity = rs.getInt("quantity");
                String description = rs.getString("description");

                dvds.add(new DVD(id, title, director, duration, quantity, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DVDTableView.setItems(dvds);
    }

    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        directorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDirector()));
        durationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDuration()));
        quantityColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantity())));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        searchField.setOnAction(event -> {
            String keyword = searchField.getText().trim();
            fetchDVDs(keyword.isEmpty() ? "" : keyword);
        });

        addContextMenuToTable();
        fetchDVDs(""); // Load all DVDs at start
    }
}
