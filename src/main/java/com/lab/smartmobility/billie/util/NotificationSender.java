package com.lab.smartmobility.billie.util;

import com.lab.smartmobility.billie.dto.NotificationEventDTO;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.Vacation;
import com.lab.smartmobility.billie.entity.corporation.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSender {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void sendVacationNotification(Staff applicant, Staff approval, Vacation vacation){
        NotificationEventDTO notificationEvent = NotificationEventDTO.builder()
                .requester(applicant.getName()).receiver(approval.getName())
                .approvalStatus(vacation.getApprovalStatus())
                .type(vacation.getClass().getSimpleName().toLowerCase()).approval(approval)
                .build();

        applicationEventPublisher.publishEvent(notificationEvent);
    }

    public void sendCorporationNotification(Staff applicant, Staff approval, Application application){
        NotificationEventDTO notificationEvent = NotificationEventDTO.builder()
                .requester(applicant.getName()).receiver(approval.getName())
                .approvalStatus(application.getApprovalStatus())
                .type(application.getCorporationCard().getClass().getSimpleName()).approval(approval)
                .build();

        applicationEventPublisher.publishEvent(notificationEvent);
    }
}
