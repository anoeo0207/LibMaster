package com.example.libmaster.Models;

import com.example.libmaster.Models.Person.User;

import java.time.LocalDate;

public class Request {
    private String id;
    private User user;
    private String type;
    private LocalDate dateSubmitted;
    private String status;
    private String isbn;

    public Request(int id, User user, String type, LocalDate dateSubmitted, String status, String isbn) {
        this.id = String.valueOf(id);
        this.user = user;
        this.type = type;
        this.dateSubmitted = dateSubmitted;
        this.status = status;
        this.isbn = isbn;
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public String getType() { return type; }
    public LocalDate getDateSubmitted() { return dateSubmitted; }
    public String getStatus() { return status; }
    public String getIsbn() { return isbn;}

    public void setId(String id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setType(String type) { this.type = type; }
    public void setDateSubmitted(LocalDate dateSubmitted) { this.dateSubmitted = dateSubmitted; }
    public void setStatus(String status) { this.status = status; }
}
