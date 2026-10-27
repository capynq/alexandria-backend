package com.example.alexandria.controller;

import com.example.alexandria.service.HomeworkStatusService;
import com.example.alexandria.service.dto.HomeworkStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/homework-status")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class HomeworkStatusController {
    private final HomeworkStatusService homeworkStatusService;

    @GetMapping
    public List<HomeworkStatusDTO> findAll() {
        return homeworkStatusService.findAll();
    }

    @GetMapping("/{homeworkId}/{isDone}")
    public List<HomeworkStatusDTO> findByHomeworkIdAndDone(@PathVariable Long homeworkId, @PathVariable boolean isDone) {
        return homeworkStatusService.findByHomeworkIdAndDone(homeworkId, isDone);
    }

    @GetMapping("/student/{studentId}")
    public List<HomeworkStatusDTO> findByStudentId(@PathVariable Long studentId) {
        return homeworkStatusService.findByStudentId(studentId);
    }

    @GetMapping("/homework/{homeworkId}")
    public List<HomeworkStatusDTO> findByHomeworkId(@PathVariable Long homeworkId) {
        return homeworkStatusService.findByHomeworkId(homeworkId);
    }

    @PutMapping("/mark/{homeworkId}/{studentId}")
    public HomeworkStatusDTO markHomeworkAsDone(@PathVariable Long homeworkId, @PathVariable Long studentId) {
        return homeworkStatusService.markHomeworkAsDone(homeworkId, studentId);
    }
}
