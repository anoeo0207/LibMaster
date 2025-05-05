package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Documents.Thesis;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.sql.*;

public class ThesisTableController {

    @FXML
    private TableView<Thesis> ThesisTableView;
    @FXML
    private TableColumn<Thesis, String> idColumn;
    @FXML
    private TableColumn<Thesis, String> titleColumn;
    @FXML
    private TableColumn<Thesis, String> authorColumn;
    @FXML
    private TableColumn<Thesis, String> universityColumn;
    @FXML
    private TableColumn<Thesis, String> quantityColumn;
    @FXML
    private TableColumn<Thesis, String> descriptionColumn;
    @FXML
    private TextField searchField;

    public void handleAddThesisBtn(ActionEvent actionEvent) throws IOException {
        Main.changeScene("addThesis.fxml");
    }

    public void handleRemoveThesisBtn(ActionEvent actionEvent) {

    }

    private void deleteThesisFromDatabase(String id) {
        String sql = "DELETE FROM items WHERE item_id = ? AND category = 'Thesis'";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Thesis with ID " + id + " deleted.");
                fetchThesis("");
            } else {
                System.out.println("No thesis deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addContextMenuToTable() {
        ThesisTableView.setRowFactory(tv -> {
            TableRow<Thesis> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                Thesis selectedThesis = row.getItem();
                if (selectedThesis != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Confirmation");
                    alert.setHeaderText("Are you sure you want to delete this thesis?");
                    alert.setContentText("Thesis: " + selectedThesis.getTitle());
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteThesisFromDatabase(selectedThesis.getId());
                        }
                    });
                }
            });

            contextMenu.getItems().addAll(deleteItem);
            row.setContextMenu(contextMenu);

            return row;
        });
    }

    public void fetchThesis(String keyword) {
        ObservableList<Thesis> theses = FXCollections.observableArrayList();
        String sql = "SELECT item_id, title, author, university, quantity, description FROM items WHERE title LIKE ? AND category = 'Thesis'";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("item_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String university = rs.getString("university");
                int quantity = rs.getInt("quantity");
                String description = rs.getString("description");

                theses.add(new Thesis(id, title, author, university, quantity, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ThesisTableView.setItems(theses);
    }

    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        universityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUniversity()));
        quantityColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantity())));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        searchField.setOnAction(event -> {
            String keyword = searchField.getText().trim();
            fetchThesis(keyword.isEmpty() ? "" : keyword);
        });

        addContextMenuToTable();
        fetchThesis("");
    }

    @FXML
    private void handleRemoveDVDBtn(ActionEvent actionEvent) {

    }
}
