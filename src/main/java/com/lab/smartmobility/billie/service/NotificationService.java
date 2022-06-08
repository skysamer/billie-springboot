package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.NotificationListForm;
import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.NotificationRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final StaffRepository staffRepository;

    /*읽지 않은 알림 목록 조회*/
    public List<Notification> getMyNotificationList(Long staffNum) {
        Staff staff=staffRepository.findByStaffNum(staffNum);
        return notificationRepository.findAllByReceiverAndReadAt(staff.getName(), 0);
    }

    /*개별 알림 조회*/
    public int updateIsRead(Long notificationNum){
        try{
            Notification notification=notificationRepository.findByNotificationNum(notificationNum);
            notification.setReadAt(1);
            notificationRepository.save(notification);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
       return 0;
    }
}
