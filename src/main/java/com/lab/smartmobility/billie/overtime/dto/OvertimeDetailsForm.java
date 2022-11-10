package com.lab.smartmobility.billie.overtime.dto;

import com.lab.smartmobility.billie.overtime.domain.ApprovalStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "추가근무 상세 조회 폼")
public class OvertimeDetailsForm {
    @ApiModelProperty(value = "추가근무 데이터 시퀀스")
    private final Long id;

    @ApiModelProperty(value = "날짜")
    private final LocalDate dayOfOvertime;

    @ApiModelProperty(value = "시작시간")
    private final LocalTime startTime;

    @ApiModelProperty(value = "종료시간")
    private final LocalTime endTime;

    @ApiModelProperty(value = "식사여부")
    private final int isMeal;

    @ApiModelProperty(value = "내용 (사유)")
    private final String content;

    @ApiModelProperty(value = "결제상태 (WAITING, PRE, CONFIRMATION, FINAL, COMPANION")
    private final ApprovalStatus approvalStatus;

    @ApiModelProperty(value = "반려사유")
    private final String companionReason;

    @ApiModelProperty(value = "직원고유번호")
    private final Long staffNum;

    @QueryProjection
    public OvertimeDetailsForm(Long id, LocalDate dayOfOvertime, LocalTime startTime, LocalTime endTime,
                               int isMeal, String content, ApprovalStatus approvalStatus, String companionReason, Long staffNum) {
        this.id = id;
        this.dayOfOvertime = dayOfOvertime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMeal = isMeal;
        this.content = content;
        this.approvalStatus = approvalStatus;
        this.companionReason = companionReason;
        this.staffNum = staffNum;
    }
}
