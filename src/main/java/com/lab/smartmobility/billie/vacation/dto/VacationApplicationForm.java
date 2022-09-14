package com.lab.smartmobility.billie.vacation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "휴가 신청 폼")
public class VacationApplicationForm {
    @ApiModelProperty(value = "휴가 시작일")
    private LocalDate startDate;

    @ApiModelProperty(value = "휴가 종료일")
    private LocalDate endDate;

    @ApiModelProperty(value = "출근시간 (반차일 경우만 전송)")
    private LocalTime workAt;

    @ApiModelProperty(value = "퇴근시간 (반차일 경우만 전송)")
    private LocalTime homeAt;

    @ApiModelProperty(value = "비상연락망")
    private String contact;

    @ApiModelProperty(value = "사유")
    private String reason;

    @ApiModelProperty(value = "휴가종류")
    private String vacationType;

    @ApiModelProperty(value = "신청자 직원 고유 번호")
    private Long staffNum;
}
