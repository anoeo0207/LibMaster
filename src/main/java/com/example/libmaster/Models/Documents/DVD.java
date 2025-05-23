package com.example.libmaster.Models.Documents;

public class DVD extends Document {
    private String director;
    private String duration;

    public DVD(String id, String title, String director, String duration, int quantity) {
        super(id, title, quantity);
        this.director = director;
        this.duration = duration;
        this.quantity = quantity;
    }

    public DVD(String id, String title, String director, String duration, int quantity, String description) {
        super(id, title, quantity, description);
        this.director = director;
        this.duration = duration;
        this.quantity = quantity;
    }

    public String getDirector() {
        return director;
    }

    public String getDuration() {
        return duration;
    }
}
