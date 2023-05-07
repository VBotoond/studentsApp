package com.example.students.service;

import com.example.students.exceptions.InvalidEmailException;
import com.example.students.exceptions.StudentNotFoundException;
import com.example.students.model.Student;
import com.example.students.model.StudentRequest;
import com.example.students.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public void addStudent(Student student) throws InvalidEmailException {

        validateEmail(student.getEmail(), student.getId());

        studentRepository.save(student);
    }
    /**
     * Validates an email address for a student using a regex.
     *
     * @param email the email address to be validated
     * @param id the unique identifier of the student to be updated
     * @throws InvalidEmailException if the email address is not valid or is already in use by another student
     */
    public void validateEmail(String email, UUID id) throws InvalidEmailException {

        if (email == null || !email.matches("^[\\w]*[\\w]+@[\\w]*[\\w]+\\.+[\\w]{2,4}$")) {
            throw new InvalidEmailException("Invalid email address");
        } else if (studentRepository.findByEmail(email).isPresent() && id != studentRepository.findByEmail(email).get().getId()) {
            throw new InvalidEmailException("The given email is already used");
        }
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     *  Updates a student's name and/or email address in the database.
     * @param newStudent The updated student object.
     * @throws InvalidEmailException If the provided email address is invalid or already exists for another student.
     * @throws StudentNotFoundException If the provided student ID does not exist in the database.
     */
    public void updateStudent(Student newStudent) throws InvalidEmailException, StudentNotFoundException {
        UUID id = newStudent.getId();
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        String name = newStudent.getName();
        String email = newStudent.getEmail();

        if (!name.isBlank() && !name.equals(student.getName())) {
            student.setName(name);
        }
        if (!email.isBlank() && !email.equals(student.getEmail())) {
            validateEmail(email, id);
            student.setEmail(email);
        }
        studentRepository.save(student);
    }

    public void deleteStudent(UUID id) throws StudentNotFoundException {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        studentRepository.deleteById(id);

    }

    public Student getStudentById(UUID id) throws StudentNotFoundException {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
}


