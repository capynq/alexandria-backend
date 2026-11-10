package com.example.alexandria.repository;

import com.example.alexandria.repository.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findScheduleById(Long id);

    List<Schedule> findScheduleByGroup_Name(String groupName);

}
