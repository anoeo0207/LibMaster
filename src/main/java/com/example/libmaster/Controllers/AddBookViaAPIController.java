package com.example.libmaster.Controllers;

import com.example.libmaster.Models.Documents.Book;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddBookViaAPIController {
    private String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=";

    @FXML private TextField searchField;
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> categoryColumn;
    @FXML private TableColumn<Book, Integer> quantityColumn;

    @FXML private Label labelTitle;
    @FXML private Label labelAuthor;
    @FXML private Label labelISBN;
    @FXML private Label labelCategory;
    @FXML private Label labelDescription;
    @FXML private ImageView bookCoverImage;

    private ObservableList<Book> books = FXCollections.observableArrayList();

    // Fetch book data from Google Books API
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

            books.clear(); // Clear existing data

            for (int i = 0; i < items.length(); i++) {
                JSONObject book = items.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                String id = book.getString("id");
                String title = volumeInfo.getString("title");
                String description = volumeInfo.optString("description", "No description available");
                String authors = volumeInfo.optJSONArray("authors") != null ? volumeInfo.getJSONArray("authors").join(", ") : "Unknown";
                String category = volumeInfo.optJSONArray("categories") != null ? volumeInfo.getJSONArray("categories").join(", ") : "Uncategorized";
                String imageCover = volumeInfo.optJSONObject("imageLinks") != null ? volumeInfo.getJSONObject("imageLinks").getString("thumbnail") : "No image available";

                books.add(new Book(id, title, authors, category, description, imageCover));
            }

            // Bind the data to the table
            bookTable.setItems(books);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddNewBookBtn() {
        // Add book functionality (e.g., save book to database)
    }

    @FXML
    public void onSearch() {
        String query = searchField.getText();
        fetchBookData(query);
    }

    @FXML
    public void initialize() {
        // Set up table columns
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        // Add row click listener to display book details
        bookTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    labelTitle.setText("Title: " + selectedBook.getTitle());
                    labelAuthor.setText("Author: " + selectedBook.getAuthor());
                    labelISBN.setText("ISBN: " + selectedBook.getId());
                    labelCategory.setText("Category: " + selectedBook.getCategory());
                    labelDescription.setText("Description: " + selectedBook.getDescription());

                    // Set book cover image
                    if (!selectedBook.getCoverImagePath().equals("No image available")) {
                        bookCoverImage.setImage(new Image(selectedBook.getCoverImagePath()));
                    } else {
                        bookCoverImage.setImage(new Image("file:default.png"));
                    }
                }
            }
        });
    }
}
