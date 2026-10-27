package com.example.alexandria.controller;

import com.example.alexandria.service.HomeworkService;
import com.example.alexandria.service.dto.HomeworkDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/homework")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class HomeworkController {
    private final HomeworkService homeworkService;

    @GetMapping
    public List<HomeworkDTO> getHomeworks() {
        return homeworkService.findHomeworks();
    }

    @GetMapping("/search/id/{id}")
    public HomeworkDTO getHomework(@PathVariable long id) {
        return homeworkService.findHomework(id);
    }

    @GetMapping("/search/title/{title}")
    public HomeworkDTO getHomeworkByTitle(@PathVariable String title) {
        return homeworkService.findHomeworkByTitle(title);
    }

    @PostMapping
    public HomeworkDTO createHomework(@RequestBody HomeworkDTO homeworkDTO) {
        return homeworkService.create(homeworkDTO);
    }

    @PutMapping("/{id}")
    public HomeworkDTO updateHomework(@PathVariable long id, @RequestBody HomeworkDTO homeworkDTO) {
        return homeworkService.update(id, homeworkDTO);
    }
}
