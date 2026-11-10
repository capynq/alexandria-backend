package com.example.alexandria.controller;

import com.example.alexandria.service.dto.ScheduleDTO;
import com.example.alexandria.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public List<ScheduleDTO> getSchedules() {
        return scheduleService.findSchedules();
    }

    @GetMapping("/{id}")
    public ScheduleDTO getSchedule(@PathVariable long id) {
        return scheduleService.findSchedule(id);
    }

    @GetMapping("/groups/{name}")
    public List<ScheduleDTO> getScheduleByGroup(@PathVariable String name) {
        return scheduleService.findScheduleByGroupName(name);
    }

    @PostMapping("/create")
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO schedule) {
        return scheduleService.create(schedule);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable long id) {
        scheduleService.delete(id);
    }

    @PutMapping("/{id}")
    public void updateSchedule(@PathVariable long id, @RequestBody ScheduleDTO schedule) {
        scheduleService.update(id, schedule);
    }
}
