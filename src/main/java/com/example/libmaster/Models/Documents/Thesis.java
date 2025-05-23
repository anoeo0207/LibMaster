package com.example.libmaster.Models.Documents;

public class Thesis extends Document {
    private String author;
    private String university;

    public Thesis(String id, String title, String author, String university, int quantity, String description) {
        super(id, title, quantity, description);
        this.author = author;
        this.university = university;
        this.quantity = quantity;
    }

    public String getAuthor() {
        return author;
    }

    public String getUniversity() {
        return university;
    }
}
