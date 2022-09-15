package com.lab.smartmobility.billie.notification.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "알림목록 조회 폼")
public class NoticeListForm {
    @ApiModelProperty(value = "수신자")
    private String sender;

    @ApiModelProperty(value = "알림종류(vacation, overtime, corporation)")
    private String type;

    @ApiModelProperty(value = "날짜")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "요청자")
    private String requester;
}
