package com.lab.smartmobility.billie.dto;

import com.lab.smartmobility.billie.entity.Staff;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class NotificationEventDTO {
    private String requester;
    private String receiver;
    private char approvalStatus;
    private Staff approval;
}
