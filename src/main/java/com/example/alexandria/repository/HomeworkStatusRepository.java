package com.example.alexandria.repository;

import com.example.alexandria.repository.entity.HomeworkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Modifying
    @Query(value = "INSERT INTO homework_status (homework_id, student_id, is_done, marked_at) VALUES (:homeworkId, :studentId, false, CURRENT_TIMESTAMP) ON CONFLICT (student_id, homework_id) DO NOTHING", nativeQuery = true)
    int insertIfAbsent(@Param("homeworkId") long homeworkId, @Param("studentId") long studentId);
}
