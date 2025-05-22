package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Models.Documents.Book;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class AddBookViaAPIController {
    private String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=";

    @FXML private TextField searchField;
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> categoryColumn;
    @FXML private Button btn_addBook;

    @FXML private Label labelTitle;
    @FXML private Label labelAuthor;
    @FXML private Label labelId;
    @FXML private Label labelCategory;
    @FXML private Label labelDescription;
    @FXML private ImageView bookCoverImage;

    private ObservableList<Book> books = FXCollections.observableArrayList();

    public void fetchBookData(String query) {
        try {
            String search = apiUrl + query;
            URL url = new URL(search);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray items = jsonResponse.getJSONArray("items");

            books.clear();

            for (int i = 0; i < items.length(); i++) {
                JSONObject book = items.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                String id = book.getString("id");
                String title = volumeInfo.getString("title");
                String description = volumeInfo.optString("description", "No description available");
                if (description.length() > 70) {
                    description = description.substring(0, 70) + "..."; // Truncate to 40 characters and add ellipsis
                }
                String authors = volumeInfo.optJSONArray("authors") != null ? volumeInfo.getJSONArray("authors").join(", ") : "Unknown";
                String category = volumeInfo.optJSONArray("categories") != null ? volumeInfo.getJSONArray("categories").join(", ") : "Uncategorized";
                String imageCover = volumeInfo.optJSONObject("imageLinks") != null ? volumeInfo.getJSONObject("imageLinks").getString("thumbnail") : "No image available";

                books.add(new Book(id, title, authors, category, description, imageCover));
            }

            bookTable.setItems(books);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddNewBookBtn() {

    }

    @FXML
    public void onSearch() {
        String query = searchField.getText();
        fetchBookData(query);
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        bookTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

                if (selectedBook != null) {
                    labelTitle.setText("Title: " + selectedBook.getTitle());
                    labelAuthor.setText("Author: " + selectedBook.getAuthor());
                    labelId.setText("ID: " + selectedBook.getId());
                    labelCategory.setText("Category: " + selectedBook.getCategory());
                    labelDescription.setText("Description: " + selectedBook.getDescription());

                    if (!selectedBook.getImage().equals("No image available")) {
                        bookCoverImage.setImage(new Image(selectedBook.getImage()));
                    } else {
                        bookCoverImage.setImage(new Image("file:default.png"));
                    }
                }
            }
        });


        btn_addBook.setOnMouseClicked(event -> {
                Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    openDialog(selectedBook);
            }
        });

        fetchBookData("1");

        searchField.setOnAction(event -> {
            String keyword = searchField.getText().replaceAll("\\s+", "");
            if (!keyword.isEmpty()) {
                fetchBookData(keyword);
            } else if (keyword.equals("")) {
                fetchBookData("");
            }
        });
    }

    private void openDialog(Book selectedBook) {
        TextInputDialog isbnDialog = new TextInputDialog();
        isbnDialog.setTitle("Enter LMCode");
        isbnDialog.setHeaderText("Enter LMCode for: " + selectedBook.getTitle());
        isbnDialog.setContentText("LMCode:");

        Optional<String> isbnResult = isbnDialog.showAndWait();
        isbnResult.ifPresent(isbn -> {
            TextInputDialog quantityDialog = new TextInputDialog();
            quantityDialog.setTitle("Enter Quantity");
            quantityDialog.setHeaderText("Enter quantity for: " + selectedBook.getTitle());
            quantityDialog.setContentText("Quantity:");

            Optional<String> quantityResult = quantityDialog.showAndWait();
            quantityResult.ifPresent(quantity -> {
                if (quantity.matches("\\d+")) {
                    int quantityInt = Integer.parseInt(quantity);
                    insertBookIntoDatabase(selectedBook, quantityInt, isbn);
                } else {
                    showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                }
            });
        });
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void insertBookIntoDatabase(Book selectedBook, int quantity, String ISBN) {
        String sql = "INSERT INTO books (isbn, title, author, category, quantity, image, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ISBN);
            stmt.setString(2, selectedBook.getTitle());
            stmt.setString(3, selectedBook.getAuthor());
            stmt.setString(4, selectedBook.getCategory());
            stmt.setInt(5, quantity);
            stmt.setString(6, selectedBook.getImage());
            stmt.setString(7, selectedBook.getDescription());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Book inserted successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Failure", "Failed to insert book.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate") || e.getMessage().contains("unique") || e.getErrorCode() == 1062) {
                showAlert("Error", "ISBN already exists in the database.", Alert.AlertType.ERROR);
            } else {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while inserting the book.", Alert.AlertType.ERROR);
            }
        }
    }
}
