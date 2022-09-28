package com.lab.smartmobility.billie.overtime.dto;

import com.lab.smartmobility.billie.overtime.domain.ApprovalStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "추가근무 상세 조회 폼")
public class OvertimeDetailsForm {
    @ApiModelProperty(value = "추가근무 데이터 시퀀스")
    private Long id;

    @ApiModelProperty(value = "날짜")
    private LocalDate dayOfOvertime;

    @ApiModelProperty(value = "시작시간")
    private LocalTime startTime;

    @ApiModelProperty(value = "종료시간")
    private LocalTime endTime;

    @ApiModelProperty(value = "식사여부")
    private boolean isMeal;

    @ApiModelProperty(value = "내용 (사유)")
    private String content;

    @ApiModelProperty(value = "결제상태 (WAITING, PRE, CONFIRMATION, FINAL, COMPANION")
    private ApprovalStatus approvalStatus;
}
