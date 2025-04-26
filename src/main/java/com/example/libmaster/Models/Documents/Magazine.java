package com.example.libmaster.Models.Documents;

public class Magazine extends Document {
    private String issueNumber;
    private String publisher;

    public Magazine(String id, String title, String issueNumber, String publisher, int quantity) {
        super(id, title);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        this.quantity = quantity;
        this.isAvailable = quantity > 0;
    }

    @Override
    public void showInfo() {
        System.out.println("Magazine: " + title + " | Issue: " + issueNumber + " | Publisher: " + publisher);
    }
}
