package com.lab.smartmobility.billie.repository.announcement;

import com.lab.smartmobility.billie.entity.AnnouncementStaffLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementStaffLikeRepository extends JpaRepository<AnnouncementStaffLike, Long> {
    boolean existsByEmailAndAnnouncementId(String email, Long announcementId);
    void deleteByEmailAndAnnouncementId(String email, Long announcementId);
}
