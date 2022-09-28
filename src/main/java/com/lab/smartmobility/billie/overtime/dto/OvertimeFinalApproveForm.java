package com.lab.smartmobility.billie.overtime.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "추가근무 최종승인 폼")
public class OvertimeFinalApproveForm {
    @ApiModelProperty(value = "추가근무 시퀀스")
    private Long id;

    @ApiModelProperty(value = "인정시간")
    private double admitTime;
}
