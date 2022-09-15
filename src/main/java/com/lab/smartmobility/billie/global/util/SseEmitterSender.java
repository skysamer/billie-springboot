package com.lab.smartmobility.billie.global.util;

import com.lab.smartmobility.billie.notification.domain.Notification;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import static com.lab.smartmobility.billie.global.controller.SseController.SSE_EMITTERS;

@Component
@RequiredArgsConstructor
public class SseEmitterSender {
    private final NotificationRepository notificationRepository;
    private final Log log = LogFactory.getLog(getClass());

    public void sendSseEmitter(Staff staff, Notification notification){
        notificationRepository.save(notification);

        if(SSE_EMITTERS.containsKey(staff.getEmail())){
            SseEmitter sseEmitter= SSE_EMITTERS.get(staff.getEmail());
            try{
                sseEmitter.send(SseEmitter.event().name("notification").data(notification));
                log.info("success");
            }catch (Exception e){
                log.error(e.getMessage());
                SSE_EMITTERS.remove(staff.getEmail());
            }
        }
    }

}
