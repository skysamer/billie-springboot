package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.NotificationRepository;
import com.lab.smartmobility.billie.user.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final StaffRepository staffRepository;

    /*읽지 않은 알림 목록 조회*/
    public List<Notification> getMyNotificationList(Long staffNum) {
        Staff staff = staffRepository.findByStaffNum(staffNum);
        return notificationRepository.findAllByReceiver(staff.getName());
    }

    /*개별 알림 조회 및 삭제*/
    public HttpBodyMessage readAndDelete(Long id){
        Notification notification=notificationRepository.findById(id).orElse(null);
        if(notification == null){
            return new HttpBodyMessage("fail", "알림 데이터를 찾을 수 없음");
        }

        notificationRepository.delete(notification);
        return new HttpBodyMessage("success", "알림 확인 및 삭제 성공");
    }

    /*나에게 수신된 알림 전체 삭제*/
    public HttpBodyMessage removeAll(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        notificationRepository.deleteByReceiver(staff.getName());
        return new HttpBodyMessage("success", "전체 알림 삭제 완료");
    }
}
