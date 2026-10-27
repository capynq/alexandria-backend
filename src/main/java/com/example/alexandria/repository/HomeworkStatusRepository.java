package com.example.alexandria.repository;

import com.example.alexandria.repository.entity.HomeworkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkStatusRepository extends JpaRepository<HomeworkStatus, Long> {
    List<HomeworkStatus> findByHomeworkIdAndIsDone(Long homeworkId, boolean isDone);

    List<HomeworkStatus> findByHomeworkId(Long homeworkId);

    List<HomeworkStatus> findByStudentId(Long studentId);
    Optional<HomeworkStatus> findByHomeworkIdAndStudentId(Long homeworkId, Long studentId);
    boolean existsByHomeworkId(Long homeworkId);
}
