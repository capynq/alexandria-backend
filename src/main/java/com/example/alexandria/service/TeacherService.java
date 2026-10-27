package com.example.alexandria.service;

import com.example.alexandria.repository.TeacherRepository;
import com.example.alexandria.repository.entity.Teacher;
import com.example.alexandria.security.PasswordUtil;
import com.example.alexandria.service.dto.GroupDTO;
import com.example.alexandria.service.dto.SubjectDTO;
import com.example.alexandria.service.dto.TeacherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public TeacherDTO mapTeacher(Teacher teacher) {
        List<GroupDTO> groupDTOs = teacher.getGroups() != null ? teacher.getGroups().stream().map(group -> GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .build()).toList() : new ArrayList<>();

        List<SubjectDTO> subjectDTOs = teacher.getSubjects() != null ? teacher.getSubjects().stream().map(subject -> SubjectDTO.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .build()).toList() : new ArrayList<>();

        return TeacherDTO.builder()
                .id(teacher.getId())
                .fullName(teacher.getFullName())
                .login(teacher.getLogin())
                .isAdmin(teacher.isAdmin())
                                .groups(groupDTOs)
                .subjects(subjectDTOs)
                .build();
    }

    public TeacherDTO logIn(TeacherDTO teacher) {
        requireCredentials(teacher.login(), teacher.password());
        var foundTeacher = teacherRepository.findByLogin(teacher.login())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (PasswordUtil.checkPassword(teacher.password(), foundTeacher.getPassword())) {
            return mapTeacher(foundTeacher);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }


    public TeacherDTO findTeacher(long id) {
        return teacherRepository.findById(id)
                .map(this::mapTeacher)
                .orElseThrow();
    }

    public List<TeacherDTO> findTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::mapTeacher)
                .toList();
    }

    public void delete(String login) {
        var teacher = teacherRepository.findByLogin(login).orElseThrow();
        teacherRepository.delete(teacher);
    }

    public void update(String login, TeacherDTO teacher) {
        var teacherToUpdate = teacherRepository.findByLogin(login).orElseThrow();
        if (teacher.password() != null && !teacher.password().isEmpty()) {
            if (teacher.password().length() < 6 || teacher.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
            String hashedPassword = PasswordUtil.hashPassword(teacher.password());
            teacherToUpdate.setPassword(hashedPassword);
        }
        teacherRepository.save(teacherToUpdate);
    }

    public TeacherDTO create(TeacherDTO teacher) {
        requireCredentials(teacher.login(), teacher.password());
        if (teacher.fullName() == null || teacher.fullName().trim().length() < 5) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid full name");
        String hashedPassword = PasswordUtil.hashPassword(teacher.password());
        var savedTeacher = teacherRepository.save(Teacher.builder()
                .fullName(teacher.fullName())
                .login(teacher.login())
                .isAdmin(false)
                .password(hashedPassword)
                .build());
        return mapTeacher(savedTeacher);
    }

    public List<GroupDTO> findGroupsByTeacher(long teacherId) {
        Optional<Teacher> optionalTeacher = teacherRepository.findByIdWithGroups(teacherId);
        List<GroupDTO> groupDTOs = new ArrayList<>();
        if (optionalTeacher.isPresent()) {
            Teacher teacher = optionalTeacher.get();
            groupDTOs = teacher.getGroups().stream()
                    .map(group -> GroupDTO.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .build())
                    .toList();
        }
        return groupDTOs;
    }

    public List<SubjectDTO> findSubjectsByTeacher(long teacherId) {
        Optional<Teacher> optionalTeacher = teacherRepository.findByIdWithSubjects(teacherId);
        List<SubjectDTO> subjectDTOs = new ArrayList<>();
        if (optionalTeacher.isPresent()) {
            Teacher teacher = optionalTeacher.get();
            subjectDTOs = teacher.getSubjects().stream()
                    .map(subject -> SubjectDTO.builder()
                            .id(subject.getId())
                            .subjectName(subject.getSubjectName())
                            .build())
                    .toList();
        }
        return subjectDTOs;
    }

    private void requireCredentials(String login, String password) {
        if (login == null || login.trim().length() < 3 || password == null || password.length() < 6 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials");
        }
    }

}

