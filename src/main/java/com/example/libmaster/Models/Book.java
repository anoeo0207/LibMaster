package com.example.libmaster.Models;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private String quantity;
    private String published_date;
    private String image;

    private String thumbnaiUrl;
    private String description;

    public Book(String isbn, String title, String author, String genre, String quantity, String published_date, String image) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
        this.published_date = published_date;
        this.image = image;
    }

    public Book(String title, String author, String isbn, String category, String description, String thumbnaiUrl) {
        this.title = title;
        this.author = author;
        this.genre = category;
        this.isbn = isbn;
        this.thumbnaiUrl = thumbnaiUrl;
        this.description = description;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPublished_date() {
        return published_date;
    }

    public String getImage() {
        return image;
    }

    //Delete
    public String getCategory() {
        return genre;
    }

    public String getThumbnailUrl() {
        return thumbnaiUrl;
    }

    public String getDescription() {
        return description;
    }
}
