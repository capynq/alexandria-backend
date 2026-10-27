package com.example.alexandria.controller;

import com.example.alexandria.service.dto.SubjectDTO;
import com.example.alexandria.service.SubjectService;
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
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping
    public List<SubjectDTO> getSubjects() {
        return subjectService.findSubjects();
    }

    @GetMapping("/{id}")
    public SubjectDTO getSubject(@PathVariable long id) {
        return subjectService.findSubject(id);
    }

    @PostMapping("/create")
    public SubjectDTO createSubject(@RequestBody SubjectDTO subject) {
        return subjectService.create(subject);
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable long id) {
        subjectService.delete(id);
    }
    @PostMapping("/assign/{teacherId}")
    public void assignSubjects(@PathVariable long teacherId, @RequestBody List<Long> subjectIds) {
        log.info("Assigning subjects {} to teacher {}", subjectIds, teacherId);
        subjectService.assignSubjectsToTeacher(teacherId, subjectIds);
    }
    @PutMapping("/update")
    public SubjectDTO updateSubject(@RequestBody SubjectDTO subject) {
        return subjectService.update(subject);
    }
}
