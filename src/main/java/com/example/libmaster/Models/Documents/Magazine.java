package com.example.libmaster.Models.Documents;

public class Magazine extends Document {
    private String publisher;
    private String author;

    public Magazine(String id, String title, String publisher, int quantity) {
        super(id, title, quantity);
        this.publisher = publisher;
        this.quantity = quantity;
    }

    public Magazine(String id, String title, String author, String publisher, int quantity, String description) {
        super(id, title, quantity, description);
        this.author = author;
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getAuthor() {
        return author;
    }
}
