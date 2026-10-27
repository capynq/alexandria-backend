package com.example.alexandria.service;


import com.example.alexandria.repository.HomeworkRepository;
import com.example.alexandria.repository.HomeworkStatusRepository;
import com.example.alexandria.repository.StudentRepository;
import com.example.alexandria.repository.GroupRepository;
import com.example.alexandria.repository.entity.HomeworkStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.example.alexandria.repository.entity.Group;
import com.example.alexandria.repository.entity.Homework;
import com.example.alexandria.repository.entity.Subject;
import com.example.alexandria.repository.entity.Teacher;
import com.example.alexandria.service.dto.HomeworkDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final HomeworkStatusRepository homeworkStatusRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    public HomeworkDTO map(Homework homework) {
        return HomeworkDTO.builder()
                .id(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .deadline(homework.getDeadline().toString())
                .subjectId(homework.getSubject().getId())
                .teacherId(homework.getTeacher().getId())
                .groupId(homework.getGroup().getId())
                .build();
    }

    public HomeworkDTO findHomework(long id) {
        return homeworkRepository.findHomeworkById(id)
                .map(this::map)
                .orElseThrow();
    }

    public HomeworkDTO findHomeworkByTitle(String title) {
        return homeworkRepository.findHomeworkByTitle(title)
                .map(this::map)
                .orElseThrow();
    }

    public List<HomeworkDTO> findHomeworks() {
        return homeworkRepository.findAll().stream()
                .map(this::map)
                .toList();
    }

    @Transactional
    public HomeworkDTO create(HomeworkDTO homeworkDTO) {
        validate(homeworkDTO);
        Group group = groupRepository.findById(homeworkDTO.groupId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var homework = homeworkRepository.save(Homework.builder()
                .title(homeworkDTO.title())
                .description(homeworkDTO.description())
                .deadline(Instant.parse(homeworkDTO.deadline()))
                .subject(Subject.builder().id(homeworkDTO.subjectId()).build())
                .teacher(Teacher.builder().id(homeworkDTO.teacherId()).build())
                .group(group)
                .build());
        studentRepository.findByGroupName(group.getName()).forEach(student -> homeworkStatusRepository.save(
                HomeworkStatus.builder().homework(homework).student(student).isDone(false).markedAt(Instant.now()).build()));
        return map(homework);
    }

    @Transactional
    public HomeworkDTO update(Long id, HomeworkDTO updatedHomework) {
        validate(updatedHomework);
        Homework homeworkToUpdate = homeworkRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework not found"));

        if (homeworkToUpdate.getGroup().getId() != updatedHomework.groupId() && homeworkStatusRepository.existsByHomeworkId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot change group after assignments exist");
        }
        homeworkToUpdate.setSubject(Subject.builder().id(updatedHomework.subjectId()).build());
        homeworkToUpdate.setGroup(Group.builder().id(updatedHomework.groupId()).build());
        homeworkToUpdate.setTeacher(Teacher.builder().id(updatedHomework.teacherId()).build());
        homeworkToUpdate.setTitle(updatedHomework.title());
        homeworkToUpdate.setDescription(updatedHomework.description());
        homeworkToUpdate.setDeadline(Instant.parse(updatedHomework.deadline()));

        var savedHomework = homeworkRepository.save(homeworkToUpdate);
        return map(savedHomework);
    }
    private void validate(HomeworkDTO value) {
        if (value.title() == null || value.title().isBlank() || value.title().length() > 255 || value.description() == null || value.description().isBlank() || value.deadline() == null || value.groupId() <= 0 || value.subjectId() <= 0 || value.teacherId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid homework");
        }
        try { Instant.parse(value.deadline()); } catch (RuntimeException ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid deadline"); }
    }
}
