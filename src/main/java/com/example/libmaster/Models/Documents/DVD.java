package com.example.libmaster.Models.Documents;

public class DVD extends Document {
    private String director;
    private int duration;

    public DVD(String id, String title, String director, int duration, int quantity) {
        super(id, title);
        this.director = director;
        this.duration = duration;
        this.quantity = quantity;
        this.isAvailable = quantity > 0;
    }

    @Override
    public void showInfo() {
        System.out.println("DVD: " + title + " | Director: " + director + " | Duration: " + duration + " minutes");
    }
}
