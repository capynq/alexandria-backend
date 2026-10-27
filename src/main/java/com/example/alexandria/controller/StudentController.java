package com.example.alexandria.controller;

import com.example.alexandria.service.dto.StudentDTO;
import com.example.alexandria.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public List<StudentDTO> getStudents(@RequestParam(required = false) String password) {
        if (password != null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Password query parameter is not supported");
        return studentService.findStudents();
    }

    @GetMapping("/{id}")
    public StudentDTO findStudentById(@PathVariable long id) {
        return studentService.findStudent(id);
    }

    @PostMapping("/login")
    public StudentDTO login(@RequestBody StudentDTO studentDTO) {
        return studentService.logIn(studentDTO);
    }

    @PostMapping("/create")
    public StudentDTO createStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.create(studentDTO);
    }

    @PostMapping("/createMany")
    public List<StudentDTO> createStudents(@RequestBody List<StudentDTO> studentDTOs) {
        return studentService.createStudents(studentDTOs);
    }

    @PutMapping("/update/{login}")
    public void updateStudent(@PathVariable String login, @RequestBody StudentDTO studentDTO) {
        studentService.update(login, studentDTO);

    }

    @DeleteMapping("/{login}")
    public void deleteStudent(@PathVariable String login) {
        studentService.delete(login);
    }
}

