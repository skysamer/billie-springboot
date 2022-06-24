package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.NotificationEventDTO;
import com.lab.smartmobility.billie.dto.corporation.ApplyCorporationCardForm;
import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeService {
    private final StaffRepository staffRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void test(){
        Staff staff = Staff.builder()
                .name("test")
                .build();

        NotificationEventDTO notificationEvent =
                new NotificationEventDTO(staff.getName(), staff.getName(), 't', staff);

        staffRepository.save(staff);
        applicationEventPublisher.publishEvent(notificationEvent);
    }
}
