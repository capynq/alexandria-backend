package com.example.alexandria.repository;

import com.example.alexandria.repository.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findGroupByName(String name);
    Optional<Group> findGroupById(Long id);
    List<Group> findGroupsByTeachersId(long teacherId);
}
