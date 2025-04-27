package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import com.example.libmaster.Models.Book;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.*;


public class LibraryController {

    private static final String URL = "jdbc:mysql://localhost:3306/LibMasterServer";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> categoryColumn;
    @FXML
    private TableColumn<Book, Integer> quantityColumn;
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
    private TextField searchField;

    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        isbnColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        quantityColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getQuantity()));

        searchField.setOnAction(event -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                fetchBooks(keyword); // Fetch books based on search
            }
        });

        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showBookDetails(newSelection);
            }
        });

        fetchBooks("");
    }


    private void loadBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT isbn, title, author, category, quantity FROM books";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String isbn = rs.getString("isbn");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String category = rs.getString("category");
                    int quantity = rs.getInt("quantity");
                    books.add(new Book(isbn, title, author, category, quantity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bookTable.setItems(books);
    }

    @FXML
    private void handleAddNewBookBtn(ActionEvent event) throws IOException {
        Main.changeScene("addBookChoice.fxml");
    }

    public void fetchBooks(String keyword) {
        ObservableList<Book> books = FXCollections.observableArrayList();

        String sql = "SELECT isbn, title, author, category, quantity, image, description FROM books WHERE " +
                "isbn LIKE ? OR title LIKE ? OR author LIKE ? OR category LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchQuery = "%" + keyword + "%";
                stmt.setString(1, searchQuery);
                stmt.setString(2, searchQuery);
                stmt.setString(3, searchQuery);
                stmt.setString(4, searchQuery);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String isbn = rs.getString("isbn");
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        String category = rs.getString("category");
                        int quantity = rs.getInt("quantity");
                        String image = rs.getString("image");
                        String description = rs.getString("description");
                        books.add(new Book(isbn, title, author, category, quantity, description, image));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bookTable.setItems(books);
    }

    private void showBookDetails(Book book) {
        labelTitle.setText("Title: " + book.getTitle());
        labelAuthor.setText("Author: " + book.getAuthor());
        labelISBN.setText("ISBN: " + book.getIsbn());
        labelCategory.setText("Category: " + book.getCategory());
        labelDescription.setText("Description: " + (book.getDescription() != null ? book.getDescription() : "No description"));

        String imageUrl = book.getImage();
        System.out.println(imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image image = new Image(imageUrl);
            bookCoverImage.setImage(image);
        } else {
            Image defaultImage = new Image("picture.png");
            bookCoverImage.setImage(defaultImage);
        }
    }

    @FXML
    private void onSearch() {
        String param = searchField.getText();
        if (!param.isEmpty()) {
            fetchBooks(param);
        }
    }
}
