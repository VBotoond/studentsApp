package com.example.students;
import com.example.students.exceptions.InvalidEmailException;
import com.example.students.exceptions.StudentNotFoundException;
import com.example.students.model.Student;
import com.example.students.model.StudentRequest;
import com.example.students.repository.StudentRepository;
import com.example.students.service.StudentService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.UUID;
import static java.util.UUID.randomUUID;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;



@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Repository
public class StudentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;


    @MockBean
    private StudentRepository studentRepository;
    @Autowired
    private StudentService studentService;



    @BeforeEach
    public void setUp() {
        studentService = new StudentService(studentRepository);
    }

    @Test
    public void testAddStudent() {

        StudentRequest request = new StudentRequest("John Doe", "john_doe@example.com");
        ResponseEntity<Void> response = restTemplate.postForEntity("/add", request, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Student> students = studentService.getAllStudents();
        assertFalse(((List<?>) students).isEmpty());
        assertEquals("John Doe", students.get(0).getName());
        assertEquals("john_doe@example.com", students.get(0).getEmail());
    }

    @Test
    public void testGetAllStudents() throws InvalidEmailException {
        ResponseEntity<List<Student>> response = restTemplate.exchange(restTemplate.getRootUri() + "/list", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Student>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        studentService.addStudent(new Student(randomUUID(), "Jane Doe", "jane_doe@example.com"));

        response = restTemplate.exchange("/students", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Student>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Jane Doe", response.getBody().get(0).getName());
        assertEquals("jane_doe@example.com", response.getBody().get(0).getEmail());
    }

    @Test
    public void testUpdateStudent() throws InvalidEmailException {
        Student student = new Student(UUID.randomUUID(), "John Doe", "john_doe@example.com");


        studentService.addStudent(student);
        student.setName("Jane Doe");
        student.setEmail("jane_doe@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Student> requestEntity = new HttpEntity<>(student, headers);

        ResponseEntity<Void> response = restTemplate.exchange("/update", HttpMethod.PUT, requestEntity, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());


        Student updatedStudent = studentService.getStudentById(student.getId());
        assertEquals("Jane Doe", updatedStudent.getName());
        assertEquals("jane_doe@example.com", updatedStudent.getEmail());
    }

    @Test
    public void testDeleteStudent() throws InvalidEmailException {
        Student student = new Student(UUID.randomUUID(), "John Doe", "john_doe@example.com");
        studentService.addStudent(student);

        ResponseEntity<Void> response = restTemplate.exchange("/delete/" + student.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(student.getId()));
    }


}