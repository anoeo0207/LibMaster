package com.example.libmaster.Models.Documents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThesisTest {

    private Thesis thesis;

    @BeforeEach
    public void setUp() {
        thesis = new Thesis("T001", "Luận Văn Về Công Nghệ Mới", "Nguyễn Văn A", "Đại Học Công Nghệ", 10, "Luận văn nghiên cứu về công nghệ mới trong ngành CNTT.");
    }

    @Test
    public void testThesisConstructor() {
        assertEquals("T001", thesis.getId());
        assertEquals("Luận Văn Về Công Nghệ Mới", thesis.getTitle());
        assertEquals("Nguyễn Văn A", thesis.getAuthor());
        assertEquals("Đại Học Công Nghệ", thesis.getUniversity());
        assertEquals(10, thesis.getQuantity());
    }

    @Test
    public void testShowInfo() {
        thesis.showInfo();
    }

    @Test
    public void testGetAuthor() {
        assertEquals("Nguyễn Văn A", thesis.getAuthor());
    }

    @Test
    public void testGetUniversity() {
        assertEquals("Đại Học Công Nghệ", thesis.getUniversity());
    }
}
