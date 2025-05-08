package com.example.libmaster.Models.Documents;

public abstract class Document {
    protected String id;
    protected String title;
    protected int quantity;
    protected String description;

    public Document(String id, String title) {
        this.id = id;
        this.title = title;
        this.quantity = 1;
    }

    public Document(String title, int quantity) {
        this.title = title;
        this.quantity = quantity;
    }

    public Document(String id, String title, int quantity) {
        this.id = id;
        this.title = title;
        this.quantity = quantity;
    }

    public Document(String title, int quantity, String description) {
        this.title = title;
        this.quantity = quantity;
        this.description = description;
    }

    public Document(String id, String title, int quantity, String description) {
        this.id = id;
        this.title = title;
        this.quantity = quantity;
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getQuantity() {
        return quantity;
    }

    public abstract void showInfo();

    public String getDescription() {
        return description;
    }
}

