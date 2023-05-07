package com.example.students.controller;

import com.example.students.exceptions.InvalidEmailException;
import com.example.students.exceptions.StudentNotFoundException;
import com.example.students.model.Student;
import com.example.students.model.StudentRequest;
import com.example.students.service.StudentService;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.example.students.service.StudentService.*;

@RestController

public class StudentController {

    @Autowired
    private StudentService studentService;


    private final Logger log = LoggerFactory.getLogger(StudentController.class);

    /**
     * Retrieves the list of all students
     * @return a list of all students
     *
     */
    @GetMapping("/list")
    public List<Student> getAllStudents() {
        log.info("Getting all students");
        return studentService.getAllStudents();


    }

    /**
     * Adds a new Student to the system with a random generated ID and the given parameters
     * @param studentRequest The parameters given by the http request
     * @return A ResponseEntity with an empty body and HTTP 200 status code if the student was successfully added,
     *  a ResponseEntity with an error message and HTTP status code 400 if the email address is invalid,
     *  or a ResponseEntity with an error message and HTTP status code 409 if the email address already exists in the system
     */
    @PostMapping("/add")
    public ResponseEntity<?> addStudent(@RequestBody StudentRequest studentRequest) {

        Student student = new Student(UUID.randomUUID(), studentRequest.getName(), studentRequest.getEmail());


        try {
            studentService.addStudent(student);
            log.info("Added student with id {}", student.getId());
            return ResponseEntity.ok().build();
        } catch (InvalidEmailException e) {
            log.error("Invalid email exception occurred: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
    }

    /**
     * Updates an existing Student
     * @param student The ID of the student to be updated and the new name and e-mail
     * @return  the ResponseEntity with status 200 (OK) if the update was successful,
     * or ResponseEntity with status 404 (Not Found) if the student could not be found,
     * or  ResponseEntity with status 400 (Bad Request) if the email is invalid
     */
    @PutMapping("/update")
    public ResponseEntity<Void> updateStudent(@RequestBody Student student) {
        log.info("Updating student with id {}", student.getId());
        try {
            studentService.updateStudent(student);
            log.info("Student with id {} updated", student.getId());
            return ResponseEntity.ok().build();
        } catch (StudentNotFoundException e) {
            log.error("Student not found exception occurred: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (InvalidEmailException e) {
            log.error("Invalid email exception occurred: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes a student with the specified ID.
     * @param  id the ID of the student to be deleted
     * @return a ResponseEntity with a status code of 200 if the student was successfully deleted,
     * @throws StudentNotFoundException if the student with the specified ID was not found
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        log.info("Deleting student with id {}", id);

        try {
            studentService.deleteStudent(id);
            log.info("Student with id {} deleted", id);
            return ResponseEntity.ok().build();
        } catch (StudentNotFoundException e) {
            log.error("Student not found exception occurred: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
