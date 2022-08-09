package com.lab.smartmobility.billie.repository.announcement;

import com.lab.smartmobility.billie.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Announcement findFirstByOrderByIdDesc();
    long countByIsMain(int isMain);
    Announcement findFirstByIsMainOrderByModifiedAt(int isMain);
}
