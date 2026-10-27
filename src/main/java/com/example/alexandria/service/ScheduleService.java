package com.example.alexandria.service;

import com.example.alexandria.repository.ScheduleRepository;
import com.example.alexandria.repository.entity.Group;
import com.example.alexandria.repository.entity.Schedule;
import com.example.alexandria.repository.entity.Subject;
import com.example.alexandria.repository.entity.Teacher;
import com.example.alexandria.service.dto.ScheduleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleDTO findSchedule(Long id) {
        return scheduleRepository.findScheduleById(id)
                .map(this::map)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Schedule not found"));
    }

    public List<ScheduleDTO> findSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public List<ScheduleDTO> findScheduleByGroupName(String groupName) {
        return scheduleRepository.findScheduleByGroup_Name(groupName)
                .stream()
                .map(this::map)
                .toList();
    }

    public ScheduleDTO create(ScheduleDTO schedule) {
        validate(schedule);
        var savedSchedule = scheduleRepository.save(Schedule.builder()
                .dayOfWeek(schedule.dayOfWeek())
                .lessonNum(schedule.lessonNum())
                .weekType(schedule.weekType())
                .subject(Subject.builder().id(schedule.subjectId()).build())
                .group(Group.builder().id(schedule.groupId()).build())
                .teacher(Teacher.builder().id(schedule.teacherId()).build())
                .build());
        return map(savedSchedule);
    }

    public void update(Long id, ScheduleDTO updatedSchedule) {
        validate(updatedSchedule);
        Schedule scheduleToUpdate = scheduleRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Schedule not found"));

        scheduleToUpdate.setSubject(Subject.builder().id(updatedSchedule.subjectId()).build());
        scheduleToUpdate.setGroup(Group.builder().id(updatedSchedule.groupId()).build());
        scheduleToUpdate.setTeacher(Teacher.builder().id(updatedSchedule.teacherId()).build());
        scheduleToUpdate.setDayOfWeek(updatedSchedule.dayOfWeek());
        scheduleToUpdate.setLessonNum(updatedSchedule.lessonNum());
        scheduleToUpdate.setWeekType(updatedSchedule.weekType());

        scheduleRepository.save(scheduleToUpdate);
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }

    private ScheduleDTO map(Schedule schedule) {
        return ScheduleDTO.builder()
                .id(schedule.getId())
                .dayOfWeek(schedule.getDayOfWeek())
                .subjectId(schedule.getSubject().getId())
                .groupId(schedule.getGroup().getId())
                .teacherId(schedule.getTeacher().getId())
                .weekType(schedule.getWeekType())
                .lessonNum(schedule.getLessonNum())
                .build();
    }
    private void validate(ScheduleDTO schedule) {
        if (schedule.dayOfWeek() == null || schedule.dayOfWeek().isBlank() || schedule.dayOfWeek().length() > 15 || schedule.lessonNum() < 1 || schedule.lessonNum() > 6 || schedule.weekType() < 1 || schedule.weekType() > 2 || schedule.groupId() <= 0 || schedule.teacherId() <= 0 || schedule.subjectId() <= 0) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid schedule");
        }
    }
}
