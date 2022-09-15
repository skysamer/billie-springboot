package com.lab.smartmobility.billie.announcement.repository;

import com.lab.smartmobility.billie.announcement.domain.AnnouncementStaffLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementStaffLikeRepository extends JpaRepository<AnnouncementStaffLike, Long> {
    boolean existsByEmailAndAnnouncementId(String email, Long announcementId);
    void deleteByEmailAndAnnouncementId(String email, Long announcementId);
}
