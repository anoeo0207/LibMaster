package com.example.libmaster.Models.Documents;

public class Book extends Document {
    private String isbn;
    private String author;
    private String category;
    private String description;
    private String coverImagePath;

    public Book(String id, String title, String isbn, String author, String category, int quantity, String description, String coverImagePath) {
        super(id, title);
        this.isbn = isbn;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.description = description;
        this.coverImagePath = coverImagePath;
    }

    @Override
    public void showInfo() {
        System.out.println("Book: " + title + " | ISBN: " + isbn + " | Author: " + author);
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

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
