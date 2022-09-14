package com.lab.smartmobility.billie.vacation.dto;

import com.lab.smartmobility.billie.vacation.domain.ApprovalStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class VacationApplicationDetailsForm {
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

    @ApiModelProperty(value = "비상연락망")
    private final String contact;

    @ApiModelProperty(value = "사유")
    private final String reason;

    @ApiModelProperty(value = "휴가종류")
    private final String vacationType;

    @ApiModelProperty(value = "승인상태(WAITING, DEPARTMENT, FINAL, COMPANION, CANCEL)")
    private final ApprovalStatus approvalStatus;

    @QueryProjection
    public VacationApplicationDetailsForm(Long vacationId, LocalDate startDate, LocalDate endDate, LocalTime workAt, LocalTime homeAt,
                                          String contact, String reason, String vacationType, ApprovalStatus approvalStatus) {
        this.vacationId = vacationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workAt = workAt;
        this.homeAt = homeAt;
        this.contact = contact;
        this.reason = reason;
        this.vacationType = vacationType;
        this.approvalStatus = approvalStatus;
    }
}
