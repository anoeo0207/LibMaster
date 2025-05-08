package com.example.libmaster.Models.Person;

public class Member extends Person {
    private int bookBorrowed;

    public Member(int id, String name, String dateOfBirth, String gender, String phone, String email, int bookBorrowed) {
        super(id, name, dateOfBirth, gender, phone, email);
        this.bookBorrowed = bookBorrowed;
    }

    public Member(String identification,int id, String name, String dateOfBirth, String gender, String phone, String email, int bookBorrowed) {
        super(identification, id, name, dateOfBirth, gender, phone, email);
        this.bookBorrowed = bookBorrowed;
    }

    public int getBookBorrowed() {
        return bookBorrowed;
    }

    public void setBookBorrowed(int bookBorrowed) {
        this.bookBorrowed = bookBorrowed;
    }

}
