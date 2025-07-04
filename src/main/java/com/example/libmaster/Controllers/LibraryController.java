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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

        searchField.setOnAction(event -> onSearch());

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
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/libmaster/editBookForm.fxml"));
                    Parent root = loader.load();
                    EditBookController controller = loader.getController();
                    controller.setBook(selectedBook);
                    Stage stage = new Stage();
                    stage.setTitle("Edit Book Form");
                    stage.setScene(new Scene(root));
                    stage.setWidth(980);
                    stage.setHeight(800);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    fetchBooks(searchField.getText().trim());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));
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
                deleteLoansStmt.executeUpdate();
                deleteBookStmt.setString(1, isbn);
                int bookRowsAffected = deleteBookStmt.executeUpdate();

                if (bookRowsAffected > 0) {
                    conn.commit();
                    fetchBooks(searchField.getText().trim());
                } else {
                    conn.rollback();
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

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image image;
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                image = new Image(imageUrl, true);
            } else {
                image = new Image(imageUrl, true);
            }
            bookCoverImage.setImage(image);
        } else {
            try {
                Image defaultImage = new Image(getClass().getResource("/com/example/libmaster/picture.png").toExternalForm());
                bookCoverImage.setImage(defaultImage);
            } catch (Exception e) {
                bookCoverImage.setImage(null);
            }
        }
    }

    @FXML
    private void onSearch() {
        String param = searchField.getText();
        fetchBooks(param);
    }
}

