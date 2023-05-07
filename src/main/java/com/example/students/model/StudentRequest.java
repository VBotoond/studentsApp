package com.example.students.model;

public class StudentRequest {
    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public StudentRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public  String getEmail() {
        return email;
    }
}

