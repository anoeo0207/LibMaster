package com.example.libmaster.Models.Documents;

public abstract class Document {
    protected String id;
    protected String title;
    protected boolean isAvailable;
    protected int quantity;

    public Document(String id, String title) {
        this.id = id;
        this.title = title;
        this.isAvailable = true;
        this.quantity = 1;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAvailable() {
        return isAvailable && quantity > 0;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.isAvailable = quantity > 0;
    }

    public void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
            if (quantity == 0) {
                isAvailable = false;
            }
        }
    }

    public void increaseQuantity() {
        quantity++;
        isAvailable = true;
    }

    public abstract void showInfo();
}

