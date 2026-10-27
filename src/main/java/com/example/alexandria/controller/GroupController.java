package com.example.alexandria.controller;

import com.example.alexandria.repository.entity.Group;
import com.example.alexandria.service.GroupService;
import com.example.alexandria.service.dto.GroupDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    public List<GroupDTO> getGroups(
    ) {
        return groupService.findGroups();
    }

    @GetMapping("/{id}")
    public GroupDTO getGroup(@PathVariable long id) {
        return groupService.findGroup(id);
    }

    @PostMapping("/create")
    public GroupDTO createGroup(@RequestBody Group group) {
        return groupService.create(group);
    }

    @PostMapping("/assign/{teacherId}")
    public void assignGroups(@PathVariable long teacherId, @RequestBody List<Long> groupIds) {
        groupService.assignGroupsToTeacher(teacherId, groupIds);
    }
}
