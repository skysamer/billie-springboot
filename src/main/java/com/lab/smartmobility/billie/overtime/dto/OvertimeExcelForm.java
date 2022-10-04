package com.lab.smartmobility.billie.overtime.dto;

import com.lab.smartmobility.billie.overtime.domain.ApprovalStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "추가근무 엑셀 폼")
public class OvertimeExcelForm {
    @ApiModelProperty(value = "추가근무 데이터 시퀀스")
    private final Long id;

    @ApiModelProperty(value = "이름")
    private final String name;

    @ApiModelProperty(value = "사번")
    private final String employeeNumber;

    @ApiModelProperty(value = "날짜")
    private final LocalDate dayOfOvertime;

    @ApiModelProperty(value = "시작시간")
    private final LocalTime startTime;

    @ApiModelProperty(value = "종료시간")
    private final LocalTime endTime;

    @ApiModelProperty(value = "식사여부")
    private final boolean isMeal;

    @ApiModelProperty(value = "내용 (사유)")
    private final String content;

    @ApiModelProperty(value = "결제상태 (WAITING, PRE, CONFIRMATION, FINAL, COMPANION")
    private final ApprovalStatus approvalStatus;

    @ApiModelProperty(value = "제출시간")
    private final double subTime;

    @ApiModelProperty(value = "인정시간")
    private final Double admitTime;

    @QueryProjection
    public OvertimeExcelForm(Long id, String name, String employeeNumber, LocalDate dayOfOvertime,
                             LocalTime startTime, LocalTime endTime, boolean isMeal, String content,
                             ApprovalStatus approvalStatus, double subTime, Double admitTime) {
        this.id = id;
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.dayOfOvertime = dayOfOvertime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMeal = isMeal;
        this.content = content;
        this.approvalStatus = approvalStatus;
        this.subTime = subTime;
        this.admitTime = admitTime;
    }
}
