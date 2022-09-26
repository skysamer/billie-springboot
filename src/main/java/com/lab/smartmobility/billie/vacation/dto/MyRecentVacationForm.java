package com.lab.smartmobility.billie.vacation.dto;

import com.lab.smartmobility.billie.vacation.domain.ApprovalStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "나의 최근 휴가 신청 내역 폼")
public class MyRecentVacationForm {
    @ApiModelProperty(value = "휴가 데이터 시퀀스")
    private final Long vacationId;

    @ApiModelProperty(value = "휴가 시작일")
    private final LocalDate startDate;

    @ApiModelProperty(value = "휴가 종료일")
    private final LocalDate endDate;

    @ApiModelProperty(value = "출근시간 (반차일 경우)")
    private final LocalTime workAt;

    @ApiModelProperty(value = "퇴근시간 (반차일 경우)")
    private final LocalTime homeAt;

    @ApiModelProperty(value = "휴가종류")
    private final String vacationType;

    @ApiModelProperty(value = "승인상태(WAITING, DEPARTMENT, FINAL, COMPANION, CANCEL)")
    private final ApprovalStatus approvalStatus;

    @QueryProjection
    public MyRecentVacationForm(Long vacationId, LocalDate startDate, LocalDate endDate,
                                LocalTime workAt, LocalTime homeAt, String vacationType, ApprovalStatus approvalStatus) {
        this.vacationId = vacationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workAt = workAt;
        this.homeAt = homeAt;
        this.vacationType = vacationType;
        this.approvalStatus = approvalStatus;
    }
}
