package com.example.alexandria.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.alexandria.repository.SubjectRepository;
import com.example.alexandria.repository.TeacherRepository;
import com.example.alexandria.repository.entity.Subject;
import com.example.alexandria.service.dto.SubjectDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    public SubjectDTO findSubject(Long id) {
        return subjectRepository.findSubjectById(id)
                .map(this::mapSubject)
                .orElseThrow();
    }

    public SubjectDTO mapSubject(Subject subject) {
        return SubjectDTO.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .build();
    }

    public List<SubjectDTO> findSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapSubject)
                .toList();
    }

    public SubjectDTO create(SubjectDTO subject) {
        validate(subject);
        var savedSubject = subjectRepository.save(Subject.builder()
                .subjectName(subject.subjectName())
                .build());
        return mapSubject(savedSubject);
    }

    public SubjectDTO update(SubjectDTO subject) {
        validate(subject);
        return subjectRepository.findSubjectById(subject.id())
                .map(foundSubject -> {
                    foundSubject.setSubjectName(subject.subjectName());
                    return mapSubject(subjectRepository.save(foundSubject));
                })
                .orElseThrow();
    }

    public void delete(Long id) {
        subjectRepository.deleteById(id);
    }

    @Transactional
    public void assignSubjectsToTeacher(long teacherId, List<Long> subjectIds) {
        var teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        if (subjects.size() != subjectIds.stream().distinct().count()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found");
        }
        teacher.setSubjects(subjects);
        teacherRepository.save(teacher);
    }
    private void validate(SubjectDTO subject) {
        if (subject.subjectName() == null || subject.subjectName().isBlank() || subject.subjectName().length() > 80) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid subject");
    }
}
