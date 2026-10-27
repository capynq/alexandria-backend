package com.example.alexandria.repository;

import com.example.alexandria.repository.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGroupName(String groupName);
    Optional<Student> findById(long id);
    Optional<Student> findByLogin(String login);
}

