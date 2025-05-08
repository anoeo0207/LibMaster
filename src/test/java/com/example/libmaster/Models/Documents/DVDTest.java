package com.example.libmaster.Models.Documents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class DVDTest {

    private DVD dvd;
    private final String testId = "DVD-001";
    private final String testTitle = "Inception";
    private final String testDirector = "Christopher Nolan";
    private final String testDuration = "148";
    private final int testQuantity = 5;
    private final String testDescription = "A mind-bending heist movie";

    @BeforeEach
    void setUp() {
        dvd = new DVD(testId, testTitle, testDirector, testDuration, testQuantity, testDescription);
    }

    @Test
    void testConstructorWithAllFields() {
        assertEquals(testId, dvd.getId());
        assertEquals(testTitle, dvd.getTitle());
        assertEquals(testDirector, dvd.getDirector());
        assertEquals(testDuration, dvd.getDuration());
        assertEquals(testQuantity, dvd.getQuantity());
        assertEquals(testDescription, dvd.getDescription());
    }

    @Test
    void testConstructorWithoutDescription() {
        DVD simpleDvd = new DVD(testId, testTitle, testDirector, testDuration, testQuantity);

        assertEquals(testId, simpleDvd.getId());
        assertEquals(testTitle, simpleDvd.getTitle());
        assertEquals(testDirector, simpleDvd.getDirector());
        assertEquals(testDuration, simpleDvd.getDuration());
        assertEquals(testQuantity, simpleDvd.getQuantity());
        assertNull(simpleDvd.getDescription());
    }

    @Test
    void testGetters() {
        assertEquals(testDirector, dvd.getDirector());
        assertEquals(testDuration, dvd.getDuration());
        assertEquals(testTitle, dvd.getTitle());
        assertEquals(testId, dvd.getId());
        assertEquals(testQuantity, dvd.getQuantity());
        assertEquals(testDescription, dvd.getDescription());
    }

    @Test
    void testShowInfo() {

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        dvd.showInfo();

        String expectedOutput = "DVD: " + testTitle + " | Director: " + testDirector +
                " | Duration: " + testDuration + " minutes";

        assertTrue(outContent.toString().contains(expectedOutput));

        System.setOut(System.out);
    }

    @Test
    void testInheritedMethods() {
        assertEquals(testId, dvd.getId());
        assertEquals(testTitle, dvd.getTitle());

        assertEquals(testQuantity - 1, dvd.getQuantity());

        assertEquals(testQuantity, dvd.getQuantity());

        assertEquals(0, dvd.getQuantity());

        assertEquals(3, dvd.getQuantity());

    }

}