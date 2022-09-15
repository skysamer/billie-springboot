package com.lab.smartmobility.billie.vacation.dto;

import com.lab.smartmobility.billie.vacation.domain.ApprovalStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class VacationApproveListForm {
    @ApiModelProperty(value = "휴가 데이터 시퀀스")
    private final Long vacationId;

    @ApiModelProperty(value = "직원이름")
    private final String name;

    @ApiModelProperty(value = "휴가 시작일")
    private final LocalDate startDate;

    @ApiModelProperty(value = "휴가 종료일")
    @Column(name = "end_date")
    private final LocalDate endDate;

    @ApiModelProperty(value = "출근시간 (반차일 경우)")
    @Column(name = "work_at")
    private final LocalTime workAt;

    @ApiModelProperty(value = "퇴근시간 (반차일 경우)")
    private final LocalTime homeAt;

    @ApiModelProperty(value = "사유")
    private final String reason;

    @ApiModelProperty(value = "휴가종류")
    private final String vacationType;

    @ApiModelProperty(value = "승인상태(WAITING, DEPARTMENT, FINAL, COMPANION, CANCEL)")
    private final String approvalStatus;

    @QueryProjection
    public VacationApproveListForm(Long vacationId, String name, LocalDate startDate, LocalDate endDate, LocalTime workAt, LocalTime homeAt, String reason, String vacationType, String approvalStatus) {
        this.vacationId = vacationId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workAt = workAt;
        this.homeAt = homeAt;
        this.reason = reason;
        this.vacationType = vacationType;
        this.approvalStatus = approvalStatus;
    }
}