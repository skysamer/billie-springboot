package com.lab.smartmobility.billie.announcement.repository;

import com.lab.smartmobility.billie.announcement.domain.Announcement;
import com.lab.smartmobility.billie.announcement.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByAnnouncement(Announcement announcement);
    Attachment findByFilename(String filename);
    void deleteAllByAnnouncement(Announcement announcement);
}
