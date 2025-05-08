package com.example.libmaster.Models.Documents;

import com.example.libmaster.Config.DatabaseConfig;

import java.sql.*;

public class Book extends Document {
    private String isbn;
    private String author;
    private String category;
    private String image;

    public Book(String id, String title, String isbn, String author, String category, int quantity, String description, String coverImagePath) {
        super(id, title);
        this.isbn = isbn;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.description = description;
        this.image = coverImagePath;
    }

    public Book(String id, String title, String author, String isbn, String category, String description, String image) {
        super(id, title);
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.image = image;
        this.description = description;
    }

    public Book(String id, String title, String isbn, String author, String category, int quantity) {
        super(id, title);
        this.isbn = isbn;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
    }

    public Book(String id, String title, String author, String category, String description, String imageCover) {
        super(id, title);
        this.author = author;
        this.category = category;
        this.description = description;
        this.image = imageCover;
    }

    public Book(String isbn, String title, String author, String category, int quantity) {
        super(title, quantity);
        this.isbn = isbn;
        this.author = author;
        this.category = category;
    }

    public Book(String isbn, String title, String author, String category, int quantity, String description, String image) {
        super(title, quantity, description);
        this.isbn = isbn;
        this.author = author;
        this.category = category;
        this.image = image;
    }

    public Book(String isbn, String title, String author, String category) {
        super(title);
        this.isbn = isbn;
        this.author = author;
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    @Override
    public void showInfo() {

    }

    public static boolean quantityAvailable(String isbn) {
        String query = "SELECT quantity FROM books WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                return quantity > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
        }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}

