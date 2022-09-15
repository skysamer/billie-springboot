package com.lab.smartmobility.billie.global.dto;

import com.lab.smartmobility.billie.notification.domain.Notification;
import com.lab.smartmobility.billie.staff.domain.Staff;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventRequestParam {
    private Notification notification;
    private Staff staff;
}
