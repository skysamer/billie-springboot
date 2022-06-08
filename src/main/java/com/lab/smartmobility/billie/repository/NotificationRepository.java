package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiverAndReadAt(String receiver, int readAt);
    //boolean existsByStaffAndReadAt(Staff staff, int readAt);
    Notification findByNotificationNum(Long notificationNum);
}
