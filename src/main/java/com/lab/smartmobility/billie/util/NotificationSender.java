package com.lab.smartmobility.billie.util;

import com.lab.smartmobility.billie.dto.NotificationEventDTO;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.Vacation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSender {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void sendNotification(Staff applicant, Staff approval, Vacation vacation){
        NotificationEventDTO notificationEvent = NotificationEventDTO.builder()
                .requester(applicant.getName()).receiver(approval.getName())
                .approvalStatus(vacation.getApprovalStatus())
                .type(vacation.getClass().getSimpleName()).approval(approval)
                .build();

        applicationEventPublisher.publishEvent(notificationEvent);
    }
}
