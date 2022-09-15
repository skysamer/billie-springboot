package com.lab.smartmobility.billie.notification.repository;

import com.lab.smartmobility.billie.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiver(String receiver);
    void deleteByReceiver(String receiver);
}
