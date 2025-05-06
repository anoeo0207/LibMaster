package com.example.libmaster.Controllers;

import com.example.libmaster.Config.DatabaseConfig;
import com.example.libmaster.Main;
import com.example.libmaster.Models.Documents.Book;
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
                fetchBooks(keyword);
            } else if (keyword.equals("")) {
                fetchBooks("");
            }
        });

        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showBookDetails(newSelection);
            }
        });

        addContextMenuToTable();
        fetchBooks("");
    }

    private void addContextMenuToTable() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(event -> {
            Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                System.out.println("Edit " + selectedBook.getTitle());
            }
        });

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> {
            Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this book?");
                alert.setContentText("This action may delete all the requests and loan records associated with this book.");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        deleteBookFromDatabase(selectedBook.getIsbn());
                    }
                });
            }
        });

        contextMenu.getItems().addAll(editItem, deleteItem);

        bookTable.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private void deleteBookFromDatabase(String isbn) {
        String deleteLoansSql = "DELETE FROM book_loans WHERE isbn = ?";
        String deleteBookSql = "DELETE FROM books WHERE isbn = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteLoansStmt = conn.prepareStatement(deleteLoansSql);
                 PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookSql)) {
                deleteLoansStmt.setString(1, isbn);
                int loansAffected = deleteLoansStmt.executeUpdate();
                if (loansAffected > 0 || loansAffected == 0) {
                    deleteBookStmt.setString(1, isbn);
                    int bookRowsAffected = deleteBookStmt.executeUpdate();

                    if (bookRowsAffected > 0) {
                        conn.commit();
                        System.out.println("Book with ISBN " + isbn + " and related loans deleted successfully.");
                        fetchBooks("");
                    } else {
                        System.out.println("Failed to delete the book.");
                    }
                } else {
                    System.out.println("Failed to delete related book loans.");
                }

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddNewBookBtn(ActionEvent event) throws IOException {
        Main.changeScene("addBookChoice.fxml");
    }

    public void fetchBooks(String keyword) {
        ObservableList<Book> books = FXCollections.observableArrayList();

        String sql = "SELECT isbn, title, author, category, quantity, image, description FROM books WHERE " +
                "isbn LIKE ? OR title LIKE ? OR author LIKE ? OR category LIKE ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
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
