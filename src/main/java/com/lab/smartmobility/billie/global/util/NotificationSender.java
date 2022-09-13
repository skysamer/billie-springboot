package com.lab.smartmobility.billie.global.util;

import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.entity.Staff;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSender {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SseEmitterSender sseEmitterSender;

    public void sendNotification(String type, Staff receiver, int isRequest){
        Notification notification = Notification.builder()
                .type(type)
                .receiver(receiver.getName())
                .isRequest(isRequest).build();
        sseEmitterSender.sendSseEmitter(receiver, notification);
    }
}
