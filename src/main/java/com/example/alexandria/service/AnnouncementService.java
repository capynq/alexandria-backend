package com.example.alexandria.service;


import com.example.alexandria.repository.entity.Announcement;
import com.example.alexandria.repository.AnnouncementRepository;
import com.example.alexandria.repository.entity.Teacher;
import com.example.alexandria.service.dto.AnnouncementDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;


    public AnnouncementDTO mapAnnouncements(Announcement announcement) {
        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .authorId(announcement.getAuthorId().getId())
                .createdAt(announcement.getPostedAt())
                .updatedAt(announcement.getEditedAt())
                .build();
    }

    public AnnouncementDTO findAnnouncement(long id) {
        return announcementRepository.findById(id)
                .map(this::mapAnnouncements)
                .orElseThrow();
    }

    public AnnouncementDTO findAnnouncementByTitle(String title) {
        return announcementRepository.findAnnouncementByTitle(title)
                .map(this::mapAnnouncements)
                .orElseThrow();
    }

    public List<AnnouncementDTO> findAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::mapAnnouncements)
                .toList();
    }

    public void delete(Long id) {
        var announce = announcementRepository.findAnnouncementById(id).orElseThrow();
        announcementRepository.delete(announce);
    }

    public void update(AnnouncementDTO announcement) {
        validate(announcement);
        var announce = announcement.id() > 0 ? announcementRepository.findById(announcement.id()).orElseThrow() : announcementRepository.findAnnouncementByTitle(announcement.title()).orElseThrow();
        announce.setTitle(announcement.title());
        announce.setContent(announcement.content());
        announcementRepository.save(announce);
    }

    public AnnouncementDTO create(AnnouncementDTO announcement) {
        validate(announcement);
        var newAnnouncement = announcementRepository.save(Announcement.builder()
                .id(announcement.id())
                .title(announcement.title())
                .content(announcement.content())
                .authorId(Teacher.builder().id(announcement.authorId()).build())
                .build());
        return mapAnnouncements(newAnnouncement);
    }
    private void validate(AnnouncementDTO value) {
        if (value.title() == null || value.title().isBlank() || value.title().length() > 100 || value.content() == null || value.content().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid announcement");
        }
    }
}
