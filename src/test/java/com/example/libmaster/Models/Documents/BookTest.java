package com.example.libmaster.Models.Documents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;
    private final String testId = "123";
    private final String testTitle = "Test Book";
    private final String testIsbn = "978-3-16-148410-0";
    private final String testAuthor = "Test Author";
    private final String testCategory = "Fiction";
    private final int testQuantity = 5;
    private final String testDescription = "Test Description";
    private final String testImage = "test.jpg";

    @BeforeEach
    void setUp() {
        book = new Book(testId, testTitle, testIsbn, testAuthor, testCategory, testQuantity, testDescription, testImage);
    }

    @Test
    void testConstructorWithAllFields() {
        assertEquals(testId, book.getId());
        assertEquals(testTitle, book.getTitle());
        assertEquals(testIsbn, book.getIsbn());
        assertEquals(testAuthor, book.getAuthor());
        assertEquals(testCategory, book.getCategory());
        assertEquals(testQuantity, book.getQuantity());
        assertEquals(testDescription, book.getDescription());
        assertEquals(testImage, book.getImage());
    }

    @Test
    void testConstructorWithoutQuantityAndImage() {
        Book simpleBook = new Book(testId, testTitle, testAuthor, testCategory, testDescription, testImage);

        assertEquals(testId, simpleBook.getId());
        assertEquals(testTitle, simpleBook.getTitle());
        assertEquals(testAuthor, simpleBook.getAuthor());
        assertEquals(testCategory, simpleBook.getCategory());
        assertEquals(testDescription, simpleBook.getDescription());
        assertEquals(testImage, simpleBook.getImage());
        // Default quantity should be 0 if not specified
        assertEquals(0, simpleBook.getQuantity());
    }

    @Test
    void testConstructorWithTitleAndQuantity() {
        Book quantityBook = new Book(testIsbn, testTitle, testAuthor, testCategory, testQuantity);

        assertEquals(testTitle, quantityBook.getTitle());
        assertEquals(testIsbn, quantityBook.getIsbn());
        assertEquals(testAuthor, quantityBook.getAuthor());
        assertEquals(testCategory, quantityBook.getCategory());
        assertEquals(testQuantity, quantityBook.getQuantity());
        assertNull(quantityBook.getDescription());
        assertNull(quantityBook.getImage());
    }

    @Test
    void testGetters() {
        assertEquals(testIsbn, book.getIsbn());
        assertEquals(testTitle, book.getTitle());
        assertEquals(testAuthor, book.getAuthor());
        assertEquals(testCategory, book.getCategory());
        assertEquals(testImage, book.getImage());
        assertEquals(testDescription, book.getDescription());
    }

    @Test
    void testShowInfo() {
        assertDoesNotThrow(() -> book.showInfo());
    }
}