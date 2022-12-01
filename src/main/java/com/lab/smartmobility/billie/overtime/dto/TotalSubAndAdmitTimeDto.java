package com.lab.smartmobility.billie.overtime.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter @ApiModel(value = "직원별 총 추가근무 제출시간 및 인정시간 전달 객체")
public class TotalSubAndAdmitTimeDto {
    @ApiModelProperty(value = "총 제출시간")
    private final double totalSubTime;

    @ApiModelProperty(value = " 총 인정시간")
    private final Double totalAdmitTime;

    @QueryProjection
    public TotalSubAndAdmitTimeDto(double totalSubTime, Double totalAdmitTime) {
        this.totalSubTime = totalSubTime;
        this.totalAdmitTime = totalAdmitTime;
    }
}
