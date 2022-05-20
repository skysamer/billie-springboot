package com.lab.smartmobility.billie.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @ToString
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "알림 목록 조회 폼")
public class NotificationListForm {
    @ApiModelProperty(value = "요청자")
    private String requester;

    @ApiModelProperty(value = "요청일")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "알림 종류(vacation, overtime)")
    private String type;
}
