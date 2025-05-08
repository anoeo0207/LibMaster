package com.example.libmaster.Models.Person;

import java.util.List;

public class User extends Person {
    private List<String> requestHistory;

    public User(int id, String name, String dateOfBirth, String gender, String phone, String email, List<String> requestHistory) {
        super(id, name, dateOfBirth, gender, phone, email);
        this.requestHistory = requestHistory;
    }

    public User(String userName) {
        super(userName);
    }

    public List<String> getRequestHistory() {
        return requestHistory;
    }

    public void setRequestHistory(List<String> requestHistory) {
        this.requestHistory = requestHistory;
    }

    public void addRequest(String request) {
        requestHistory.add(request);
    }
}
