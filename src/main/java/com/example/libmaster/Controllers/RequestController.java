package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Models.Person.User;
import com.example.libmaster.Models.Request;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RequestController {
    @FXML
    private TableView<Request> requestTable;
    @FXML
    private TableColumn<Request, String> idColumn;
    @FXML
    private TableColumn<Request, String> userColumn;
    @FXML
    private TableColumn<Request, String> typeColumn;
    @FXML
    private TableColumn<Request, String> dateColumn;
    @FXML
    private TableColumn<Request, String> statusColumn;
    @FXML
    private TableColumn<Request, String> isbnColumn;

    @FXML private Label labelUser;
    @FXML private Label labelType;
    @FXML private Label labelDate;
    @FXML private Label labelISBN;
    @FXML private Button btnApprove;
    @FXML private Button btnReject;

    @FXML
    private TextField searchField;

    public void fetchRequests(String keyword) {
        ObservableList<Request> requests = FXCollections.observableArrayList();

        String sql = "SELECT r.id, u.name, r.type, r.date_submitted, r.status, r.isbn " +
                "FROM requests r " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE (r.status != 'Rejected' OR r.rejected_time >= NOW() - INTERVAL 1 HOUR) " +
                "AND (u.name LIKE ? OR r.user_id LIKE ?)";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchQuery = "%" + keyword + "%";
                stmt.setString(1, searchQuery);
                stmt.setString(2, searchQuery);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String userName = rs.getString("name");
                        String type = rs.getString("type");
                        LocalDate dateSubmitted = rs.getDate("date_submitted").toLocalDate();
                        String status = rs.getString("status");
                        String isbn = rs.getString("isbn");

                        User user = new User(userName);
                        requests.add(new Request(id, user, type, dateSubmitted, status, isbn));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        requestTable.setItems(requests);
    }

    private void updateRequestStatus(int requestId, String newStatus) {
        String updateSql = "UPDATE requests SET status = ?, rejected_time = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setString(1, newStatus);
            stmt.setTimestamp(2, newStatus.equals("Rejected") ? Timestamp.valueOf(LocalDateTime.now()) : null);
            stmt.setInt(3, requestId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBookQuantity(String status, String type, String isbn) {
        if (!status.equals("Approved")) {
            return;
        }

        int currentQuantity = getBookQuantityByIsbn(isbn);
        int newQuantity = currentQuantity;

        if (type.equals("Return Books")) {
            newQuantity = currentQuantity + 1;
        } else if (type.equals("Borrow Books")) {
            if (currentQuantity <= 0) {
                return;
            }
            newQuantity = currentQuantity - 1;
        }

        String sql = "UPDATE books SET quantity = ? WHERE isbn = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, isbn);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBookQuantityByIsbn(String isbn) {
        String sql = "SELECT quantity FROM books WHERE isbn = ?";
        int quantity = -1;

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    quantity = rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quantity;
    }

    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()).asString());
        userColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUser().getName()));
        isbnColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateSubmitted().toString()));
        statusColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStatus()));

        searchField.setOnAction(event -> {
            String keyword = searchField.getText().trim();
            fetchRequests(keyword);
        });

        fetchRequests("");

        requestTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Request selectedRequest = requestTable.getSelectionModel().getSelectedItem();
                if (selectedRequest != null) {
                    labelUser.setText("User: " + selectedRequest.getUser().getName());
                    labelType.setText("Type: " + selectedRequest.getType());
                    labelDate.setText("Date: " + selectedRequest.getDateSubmitted());
                    labelISBN.setText("ISBN: " + selectedRequest.getIsbn());
                }
            }
        });

        btnApprove.setOnAction(event -> {
            Request selectedRequest = requestTable.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                if (selectedRequest.getStatus().equals("Approved")) {
                    showAlert("This request has been handled before.");
                    return;
                }
                int requestId = Integer.parseInt(selectedRequest.getId());
                updateRequestStatus(requestId, "Approved");
                updateBookQuantity("Approved", selectedRequest.getType(), selectedRequest.getIsbn());
                fetchRequests(searchField.getText().trim());
                clear();
            }
        });


        btnReject.setOnAction(event -> {
            Request selectedRequest = requestTable.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                if (selectedRequest.getStatus().equals("Rejected")) {
                    showAlert("This request has been handled before.");
                    return;
                }
                int requestId = Integer.parseInt(selectedRequest.getId());
                updateRequestStatus(requestId, "Rejected");
                fetchRequests(searchField.getText().trim());
                clear();
            }
        });

    }

    private void showAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning");
        alert.setHeaderText("Duplicate action");
        alert.setContentText(s);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
        alert.showAndWait();
    }

    private void clear() {
        labelUser.setText("User:");
        labelType.setText("Type:");
        labelDate.setText("Date:");
        labelISBN.setText("ISBN:");
    }
}
