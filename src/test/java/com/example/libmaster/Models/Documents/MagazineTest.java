package com.example.libmaster.Models.Documents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MagazineTest {

    private Magazine magazine;

    @BeforeEach
    public void setUp() {
        magazine = new Magazine("M001", "Tạp Chí Công Nghệ", "Nhà Xuất Bản Công Nghệ", 50);
    }

    @Test
    public void testMagazineConstructorWithPublisher() {
        assertEquals("M001", magazine.getId());
        assertEquals("Tạp Chí Công Nghệ", magazine.getTitle());
        assertEquals("Nhà Xuất Bản Công Nghệ", magazine.getPublisher());
        assertEquals(50, magazine.getQuantity());
    }

    @Test
    public void testMagazineConstructorWithAuthor() {
        magazine = new Magazine("M002", "Tạp Chí Khoa Học", "Tác Giả Nguyễn Văn A", "Nhà Xuất Bản Khoa Học", 30, "Tạp chí về khoa học mới nhất");
        assertEquals("Tác Giả Nguyễn Văn A", magazine.getAuthor());
        assertEquals("Nhà Xuất Bản Khoa Học", magazine.getPublisher());
        assertEquals("Tạp Chí Khoa Học", magazine.getTitle());
        assertEquals(30, magazine.getQuantity());
    }

    @Test
    public void testShowInfo() {
        magazine.showInfo();
    }
}
