package com.lab.smartmobility.billie.announcement.repository;

import com.lab.smartmobility.billie.announcement.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Announcement findFirstByOrderByIdDesc();
    long countByIsMain(int isMain);
    Announcement findFirstByIsMainOrderByModifiedAt(int isMain);
}
