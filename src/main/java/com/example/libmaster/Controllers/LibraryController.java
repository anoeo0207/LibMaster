package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import com.example.libmaster.Models.Book;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LibraryController {

    @FXML
    private ImageView bookCoverImage;

    @FXML
    private Label labelTitle;

    @FXML
    private Label labelAuthor;

    @FXML
    private Label labelISBN;

    @FXML
    private Label labelCategory;

    @FXML
    private Label labelDescription;


    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, String > isbnColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> genreColumn;

    @FXML
    private TableColumn<Book, String>  quantityColumn;

    @FXML
    private TableColumn<Book, String> publishedDateColumn;

    @FXML
    private TableColumn<Book, String> imageColumn;

    @FXML
    private TableColumn<Book, String> categoryColumn;

    @FXML
    private TextField searchField;

    private static final String api = "https://www.googleapis.com/books/v1/volumes?q=";

    @FXML
    private void handleAddNewBookBtn(ActionEvent event) throws IOException {
        Main.changeScene("addBookChoice.fxml");
    }

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        isbnColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));

        searchField.setOnAction(event -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                fetchBooks(keyword);
            }
        });

        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showBookDetails(newSelection);
            }
        });

        fetchBooks("harry potter");
    }

    private void showBookDetails(Book book) {
        labelTitle.setText("Title: " + book.getTitle());
        labelAuthor.setText("Author: " + book.getAuthor());
        labelISBN.setText("ISBN: " + book.getIsbn());
        labelCategory.setText("Category: " + book.getCategory());

        // Nếu Book có thêm trường description và thumbnail
        labelDescription.setText("Description: " + (book.getDescription() != null ? book.getDescription() : "No description"));

        if (book.getThumbnailUrl() != null && !book.getThumbnailUrl().isEmpty()) {
            bookCoverImage.setImage(new Image(book.getThumbnailUrl(), true));
        } else {
            bookCoverImage.setImage(null); // Hoặc ảnh mặc định
        }
    }


    @FXML
    private void fetchBooks(String keyword) {
        String apiUrl = api + keyword.replace(" ", "+");

        Task<List<Book>> task = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                List<Book> bookList = new ArrayList<>();
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray items = json.optJSONArray("items");
                if (items != null) {
                    for (int i = 0; i < Math.min(10, items.length()); i++) {
                        JSONObject bookInfo = items.getJSONObject(i).getJSONObject("volumeInfo");
                        String title = bookInfo.optString("title", "No Title");
                        JSONArray authors = bookInfo.optJSONArray("authors");
                        String author = (authors != null) ? authors.join(", ").replace("\"", "") : "Unknown";
                        String isbn = "No information";
                        JSONArray identifiers = bookInfo.optJSONArray("industryIdentifiers");
                        if (identifiers != null && identifiers.length() > 0) {
                            isbn = identifiers.getJSONObject(0).optString("identifier", "N/A");
                        }
                        String category = "Uncategorized";
                        JSONArray categories = bookInfo.optJSONArray("categories");
                        if (categories != null) {
                            category = categories.join(", ").replace("\"", "");
                        }

                        String description = bookInfo.optString("description", "No description");
                        String[] words = description.split("\\s+");

                        if (words.length > 30) {
                            // Cắt chỉ lấy 30 từ đầu tiên
                            description = String.join(" ", java.util.Arrays.copyOfRange(words, 0, 30)) + "...";
                        }

                        String thumbnailUrl = "";
                        if (bookInfo.has("imageLinks")) {
                            thumbnailUrl = bookInfo.getJSONObject("imageLinks").optString("thumbnail", "");
                        }

                        bookList.add(new Book(title, author, isbn, category, description, thumbnailUrl ));
                    }
                }
                return bookList;
            }

            @Override
            protected void succeeded() {
                bookTable.getItems().setAll(getValue());
            }

            @Override
            protected void failed() {
                System.out.println("❌ Error: " + getException().getMessage());
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void onSearch() {
        String param = searchField.getText();
        if (!param.isEmpty()) {
            fetchBooks(param);
        }
    }
}
