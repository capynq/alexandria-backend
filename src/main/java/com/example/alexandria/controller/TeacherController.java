package com.example.alexandria.controller;


import com.example.alexandria.service.TeacherService;
import com.example.alexandria.service.dto.GroupDTO;
import com.example.alexandria.service.dto.SubjectDTO;
import com.example.alexandria.service.dto.TeacherDTO;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @GetMapping
    public List<TeacherDTO> getTeacher() {
        log.info("Get all teachers");
        return teacherService.findTeachers();
    }

    @GetMapping("/{id}")
    public TeacherDTO findTeacherById(@PathVariable long id) {
        log.info("Get teacher by id: {}", id);
        return teacherService.findTeacher(id);
    }

    @PostMapping("/login")
    public TeacherDTO logIn(@RequestBody TeacherDTO teacherDTO) {
        return teacherService.logIn(teacherDTO);
    }

    @PostMapping("/create")
    public TeacherDTO createTeacher(@RequestBody TeacherDTO teacherDTO) {
        return teacherService.create(teacherDTO);
    }

    @PutMapping("/update/{login}")
    public void updateTeacher(@PathVariable String login, @RequestBody TeacherDTO teacherDTO) {
        log.info("Update teacher: {}", teacherDTO.login());
        teacherService.update(login, teacherDTO);
    }

    @GetMapping("/{teacherId}/groups")
    public List<GroupDTO> getTeacherGroups(@PathVariable long teacherId) {
        return teacherService.findGroupsByTeacher(teacherId);
    }

    @GetMapping("/{teacherId}/subjects")
    public List<SubjectDTO> getTeacherSubjects(@PathVariable long teacherId) {
        return teacherService.findSubjectsByTeacher(teacherId);
    }

    @DeleteMapping("/{login}")
    public void deleteTeacher(@PathVariable String login) {
        teacherService.delete(login);
    }
}


