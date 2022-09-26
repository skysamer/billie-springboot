package com.lab.smartmobility.billie.overtime.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
@ApiModel(value = "이번달 나의 추가근무 시간 dto")
public class OvertimeHourDTO {
    @ApiModelProperty(value = "추가근무 시간")
    private double overtimeHour;
}
