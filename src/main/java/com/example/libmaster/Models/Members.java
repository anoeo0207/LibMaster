package com.example.libmaster.Models;

public class Members {
    private int id;
    private String name;
    private String phone;
    private int numBookIssue;

    public Members(int id, String name, String phone, int numBookIssue) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.numBookIssue = numBookIssue;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getNumBookIssue() {
        return numBookIssue;
    }
}
