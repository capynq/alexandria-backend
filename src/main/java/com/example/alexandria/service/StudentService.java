package com.example.alexandria.service;

import com.example.alexandria.repository.StudentRepository;
import com.example.alexandria.repository.entity.Group;
import com.example.alexandria.repository.entity.Student;
import com.example.alexandria.security.PasswordUtil;
import com.example.alexandria.service.dto.StudentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final GroupService groupService;

    public StudentDTO mapStudent(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .login(student.getLogin())
                .groupName(student.getGroupName())
                                .build();
    }

    public StudentDTO logIn(StudentDTO student) {
        requireCredentials(student.login(), student.password());
        var foundStudent = studentRepository.findByLogin(student.login())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (PasswordUtil.checkPassword(student.password(), foundStudent.getPassword())) {
            return mapStudent(foundStudent);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    public StudentDTO findStudent(long id) {
        return studentRepository.findById(id)
                .map(this::mapStudent)
                .orElseThrow();
    }

    public List<StudentDTO> findStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapStudent)
                .toList();
    }

    public void delete(String login) {
        var student = studentRepository.findByLogin(login).orElseThrow();
        studentRepository.delete(student);
    }

    public void update(String login, StudentDTO student) {
        requireCredentials(student.login(), student.password());
        var studentToUpdate = studentRepository.findByLogin(login).orElseThrow();
        if (!PasswordUtil.checkPassword(student.password(), studentToUpdate.getPassword())) {
            String hashedPassword = PasswordUtil.hashPassword(student.password());
            studentToUpdate.setPassword(hashedPassword);
        }
        studentToUpdate.setLogin(student.login());
        studentRepository.save(studentToUpdate);
    }

    @Transactional
    public StudentDTO create(StudentDTO student) {
        requireCredentials(student.login(), student.password());
        if (student.fullName() == null || student.fullName().trim().length() < 5) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid full name");
        if (student.groupName() == null || student.groupName().isBlank() || student.groupName().length() > 8) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid group");
        String hashedPassword = PasswordUtil.hashPassword(student.password());
        Group group = groupService.findGroup(student.groupName());
        if (group == null) {
            groupService.create(Group.builder()
                    .name(student.groupName())
                    .build());
        }
        var savedStudent = studentRepository.save(Student.builder()
                .fullName(student.fullName())
                .login(student.login())
                .password(hashedPassword)
                .groupName(student.groupName())
                .build());
        return mapStudent(savedStudent);
    }

    @Transactional
    public List<StudentDTO> createStudents(List<StudentDTO> students) {
        return students.stream()
                .map(this::create)
                .toList();
    }

    private void requireCredentials(String login, String password) {
        if (login == null || login.trim().length() < 3 || password == null || password.length() < 6 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials");
        }
    }

}
