package com.lab.smartmobility.billie.dto.announcement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
@ApiModel(value = "메인공지의 수 전송 객체")
public class MainAnnouncementCountDTO {
    @ApiModelProperty(value = "메인공지의 수")
    private long count;
}
