package com.lab.smartmobility.billie.event;

import com.lab.smartmobility.billie.entity.Vacation;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class VacationApplyEventListener {
    @EventListener
    public void handleVacationApplyEvent(VacationApplyEvent vacationApplyEvent){
        Vacation vacation=vacationApplyEvent.getVacation();
    }
}
