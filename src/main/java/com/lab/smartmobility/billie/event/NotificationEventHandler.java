package com.lab.smartmobility.billie.event;

import com.lab.smartmobility.billie.dto.NotificationEventDTO;
import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.repository.NotificationRepository;
import com.lab.smartmobility.billie.util.SseEmitterSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationRepository notificationRepository;
    private final SseEmitterSender sseEmitterSender;

    @EventListener
    public void notificationEvent(NotificationEventDTO notificationEventDTO){
        Notification notification=Notification.builder()
                .requester(notificationEventDTO.getRequester())
                .receiver(notificationEventDTO.getReceiver())
                .type(notificationEventDTO.getType())
                .approveStatus(notificationEventDTO.getApprovalStatus())
                .build();

        notificationRepository.save(notification);
        sseEmitterSender.sendSseEmitter(notificationEventDTO.getApproval(), notification);
    }
}
