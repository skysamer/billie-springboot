package com.lab.smartmobility.billie.global.event;

import com.lab.smartmobility.billie.dto.EventRequestParam;
import com.lab.smartmobility.billie.repository.NotificationRepository;
import com.lab.smartmobility.billie.global.util.SseEmitterSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationRepository notificationRepository;
    private final SseEmitterSender sseEmitterSender;

    @EventListener
    public void notificationEvent(EventRequestParam eventRequestParam){
        notificationRepository.save(eventRequestParam.getNotification());
        sseEmitterSender.sendSseEmitter(eventRequestParam.getStaff(), eventRequestParam.getNotification());
    }
}
