package com.lab.smartmobility.billie.util;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import static com.lab.smartmobility.billie.controller.SseController.SSE_EMITTERS;

@Component
@RequiredArgsConstructor
public class SseEmitterSender {
    private final NotificationRepository notificationRepository;
    private final Log log= LogFactory.getLog(getClass());

    public void sendSseEmitter(Staff staff){
        log.info("sse-start");
        if(SSE_EMITTERS.containsKey(staff.getEmail())){
            SseEmitter sseEmitter= SSE_EMITTERS.get(staff.getEmail());
            log.info(staff.getEmail());
            try{
                sseEmitter.send(SseEmitter.event().name("notification").data("new-notification"));
                log.info("success");
            }catch (Exception e){
                e.printStackTrace();
                SSE_EMITTERS.remove(staff.getEmail());
            }
        }
    }

}
