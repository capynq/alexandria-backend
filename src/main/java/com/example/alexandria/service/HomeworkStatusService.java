package com.example.alexandria.service;

import com.example.alexandria.repository.HomeworkStatusRepository;
import com.example.alexandria.repository.HomeworkRepository;
import com.example.alexandria.repository.StudentRepository;
import com.example.alexandria.repository.entity.HomeworkStatus;
import com.example.alexandria.repository.entity.Homework;
import com.example.alexandria.repository.entity.Student;
import com.example.alexandria.service.dto.HomeworkStatusDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkStatusService {
    private final HomeworkStatusRepository homeworkStatusRepository;
    private final HomeworkRepository homeworkRepository;
    private final StudentRepository studentRepository;

    public HomeworkStatusDTO map(HomeworkStatus status) {
        return HomeworkStatusDTO.builder().id(status.getId()).homeworkId(status.getHomework().getId())
                .studentId(status.getStudent().getId()).isDone(status.isDone())
                .markedAt(status.getMarkedAt() == null ? null : status.getMarkedAt().toString()).build();
    }

    private void reconcile(Homework homework) {
        for (Student student : studentRepository.findByGroupName(homework.getGroup().getName())) {
            homeworkStatusRepository.insertIfAbsent(homework.getId(), student.getId());
        }
    }

    @Transactional
    public List<HomeworkStatusDTO> findAll() {
        homeworkRepository.findAll().forEach(this::reconcile);
        return homeworkStatusRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional
    public List<HomeworkStatusDTO> findByHomeworkIdAndDone(Long id, boolean done) {
        reconcile(findHomework(id));
        return homeworkStatusRepository.findByHomeworkIdAndIsDone(id, done).stream().map(this::map).toList();
    }

    @Transactional
    public List<HomeworkStatusDTO> findByStudentId(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        homeworkRepository.findAll().stream().filter(h -> h.getGroup().getName().equals(student.getGroupName())).forEach(this::reconcile);
        return homeworkStatusRepository.findByStudentId(id).stream().map(this::map).toList();
    }

    @Transactional
    public List<HomeworkStatusDTO> findByHomeworkId(Long id) {
        reconcile(findHomework(id));
        return homeworkStatusRepository.findByHomeworkId(id).stream().map(this::map).toList();
    }

    private Homework findHomework(Long id) {
        return homeworkRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public HomeworkStatusDTO markHomeworkAsDone(Long homeworkId, Long studentId) {
        Homework homework = findHomework(homeworkId);
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!homework.getGroup().getName().equals(student.getGroupName())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Student is not in homework group");
        reconcile(homework);
        HomeworkStatus status = homeworkStatusRepository.findByHomeworkIdAndStudentId(homeworkId, studentId).orElseThrow();
        if (!status.isDone()) {
            status.setDone(true);
            status.setMarkedAt(Instant.now());
            homeworkStatusRepository.save(status);
        }
        return map(status);
    }
}
