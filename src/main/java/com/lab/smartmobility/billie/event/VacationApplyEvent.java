package com.lab.smartmobility.billie.event;

import com.lab.smartmobility.billie.entity.Vacation;
import lombok.Getter;

@Getter
public class VacationApplyEvent {
    private final Vacation vacation;

    public VacationApplyEvent(Vacation vacation){
        this.vacation=vacation;
    }
}
