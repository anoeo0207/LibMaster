package com.example.libmaster.Models.Documents;

public class Thesis extends Document {
    private String author;
    private String university;

    public Thesis(String id, String title, String author, String university, int quantity) {
        super(id, title);
        this.author = author;
        this.university = university;
        this.quantity = quantity;
        this.isAvailable = quantity > 0;
    }

    @Override
    public void showInfo() {
        System.out.println("Thesis: " + title + " | Author: " + author + " | University: " + university);
    }
}
