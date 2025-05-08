package com.example.libmaster.Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

public class Loan {
    private final SimpleStringProperty bookTitle;
    private final SimpleStringProperty borrower;
    private final SimpleStringProperty loanDate;
    private final SimpleStringProperty returnDate;
    private final SimpleStringProperty comment;
    private final SimpleIntegerProperty memberId;

    private final SimpleIntegerProperty loanId;
    private final SimpleStringProperty itemName;
    private final SimpleStringProperty borrowerName;
    private final SimpleObjectProperty<LocalDate> itemLoanDate;
    private final SimpleObjectProperty<LocalDate> itemReturnDate;
    private final SimpleStringProperty status;
    private final SimpleIntegerProperty itemId;

    public Loan(int loanId, String itemName, String borrowerName, LocalDate loanDate,
                LocalDate returnDate, String status, int itemId) {
        this.loanId = new SimpleIntegerProperty(loanId);
        this.itemName = new SimpleStringProperty(itemName);
        this.borrowerName = new SimpleStringProperty(borrowerName);
        this.itemLoanDate = new SimpleObjectProperty<>(loanDate);
        this.itemReturnDate = new SimpleObjectProperty<>(returnDate);
        this.status = new SimpleStringProperty(status);
        this.itemId = new SimpleIntegerProperty(itemId);

        this.bookTitle = new SimpleStringProperty("");
        this.borrower = new SimpleStringProperty("");
        this.loanDate = new SimpleStringProperty("");
        this.returnDate = new SimpleStringProperty("");
        this.comment = new SimpleStringProperty("");
        this.memberId = new SimpleIntegerProperty(0);
    }

    public Loan(String bookTitle, String borrower, String loanDate,
                String returnDate, String comment, int memberId) {
        this.bookTitle = new SimpleStringProperty(bookTitle);
        this.borrower = new SimpleStringProperty(borrower);
        this.loanDate = new SimpleStringProperty(loanDate);
        this.returnDate = new SimpleStringProperty(returnDate);
        this.comment = new SimpleStringProperty(comment);
        this.memberId = new SimpleIntegerProperty(memberId);

        this.loanId = new SimpleIntegerProperty(0);
        this.itemName = new SimpleStringProperty("");
        this.borrowerName = new SimpleStringProperty("");
        this.itemLoanDate = new SimpleObjectProperty<>(null);
        this.itemReturnDate = new SimpleObjectProperty<>(null);
        this.status = new SimpleStringProperty("");
        this.itemId = new SimpleIntegerProperty(0);
    }

    public Loan(int loanId, String itemName, String borrowerName,
                LocalDate loanDate, LocalDate returnDate, String status) {
        this(loanId, itemName, borrowerName, loanDate, returnDate, status, 0);
    }

    public String getBookTitle() { return bookTitle.get(); }
    public String getBorrower() { return borrower.get(); }
    public String getLoanDate() { return loanDate.get(); }
    public String getReturnDate() { return returnDate.get(); }
    public String getComment() { return comment.get(); }
    public int getMemberId() { return memberId.get(); }

    public int getLoanId() { return loanId.get(); }
    public String getItemName() { return itemName.get(); }
    public String getBorrowerName() { return borrowerName.get(); }
    public LocalDate getItemLoanDate() { return itemLoanDate.get(); }
    public LocalDate getItemReturnDate() { return itemReturnDate.get(); }
    public String getStatus() { return status.get(); }
    public int getItemId() { return itemId.get(); }

    public SimpleStringProperty bookTitleProperty() { return bookTitle; }
    public SimpleStringProperty borrowerProperty() { return borrower; }
    public SimpleStringProperty loanDateProperty() { return loanDate; }
    public SimpleStringProperty returnDateProperty() { return returnDate; }
    public SimpleStringProperty commentProperty() { return comment; }
    public SimpleIntegerProperty memberIdProperty() { return memberId; }
    public SimpleIntegerProperty loanIdProperty() { return loanId; }
    public SimpleStringProperty itemNameProperty() { return itemName; }
    public SimpleStringProperty borrowerNameProperty() { return borrowerName; }
    public SimpleObjectProperty<LocalDate> itemLoanDateProperty() { return itemLoanDate; }
    public SimpleObjectProperty<LocalDate> itemReturnDateProperty() { return itemReturnDate; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleIntegerProperty itemIdProperty() { return itemId; }
}