package com.example.libmaster.Models.Person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MemberTest {

    private Member member;

    @BeforeEach
    public void setUp() {
        member = new Member(1, "Nguyễn Văn A", "1990-05-15", "Male", "0123456789", "nguyenvana@gmail.com", 5);
    }

    @Test
    public void testConstructor() {
        assertEquals(1, member.getId());
        assertEquals("Nguyễn Văn A", member.getName());
        assertEquals("1990-05-15", member.getDateOfBirth());
        assertEquals("Male", member.getGender());
        assertEquals("0123456789", member.getPhone());
        assertEquals("nguyenvana@gmail.com", member.getEmail());
        assertEquals(5, member.getBookBorrowed());
    }

    @Test
    public void testSettersAndGetters() {
        member.setBookBorrowed(10);
        assertEquals(10, member.getBookBorrowed());
    }

    @Test
    public void testSetBookBorrowed() {
        member.setBookBorrowed(3);
        assertEquals(3, member.getBookBorrowed());
    }
}
