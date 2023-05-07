package com.example.students.exceptions;

import java.util.UUID;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(UUID id) {
        super("Could not find student with id: " + id.toString());
    }
}
