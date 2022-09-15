package com.lab.smartmobility.billie.notification.dto;

import com.lab.smartmobility.billie.staff.domain.Staff;
import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class NotificationEventDTO {
    private String requester;
    private String receiver;
    private char approvalStatus;
    private Staff approval;
    private String type;
}
