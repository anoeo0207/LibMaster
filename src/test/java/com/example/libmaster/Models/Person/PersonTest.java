package com.example.libmaster.Models.Person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    private Person person;

    @BeforeEach
    public void setUp() {
        person = new Person(1, "Nguyễn Văn A", "1990-05-15", "Male", "0123456789", "nguyenvana@gmail.com");
    }

    @Test
    public void testPersonConstructor() {
        assertEquals(1, person.getId());
        assertEquals("Nguyễn Văn A", person.getName());
        assertEquals("1990-05-15", person.getDateOfBirth());
        assertEquals("Male", person.getGender());
        assertEquals("0123456789", person.getPhone());
        assertEquals("nguyenvana@gmail.com", person.getEmail());
    }

    @Test
    public void testGetIdentification() {
        person.setIdentification("ID12345");
        assertEquals("ID12345", person.getIdentification());
    }

    @Test
    public void testSetIdentification() {
        person.setIdentification("ID67890");
        assertEquals("ID67890", person.getIdentification());
    }

    @Test
    public void testSetPhone() {
        person.setPhone("0987654321");
        assertEquals("0987654321", person.getPhone());
    }

    @Test
    public void testSetEmail() {
        person.setEmail("nguyenvana_updated@gmail.com");
        assertEquals("nguyenvana_updated@gmail.com", person.getEmail());
    }
}
