package com.lab.smartmobility.billie.vacation.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "휴가 요청 관리 내역 목록 폼")
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

    @ApiModelProperty(value = "사번")
    private final String employeeNumber;

    @QueryProjection
    public VacationApproveListForm(Long vacationId, String name, LocalDate startDate, LocalDate endDate,
                                   LocalTime workAt, LocalTime homeAt, String reason, String vacationType, String approvalStatus, String employeeNumber) {
        this.vacationId = vacationId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workAt = workAt;
        this.homeAt = homeAt;
        this.reason = reason;
        this.vacationType = vacationType;
        this.approvalStatus = approvalStatus;
        this.employeeNumber = employeeNumber;
    }
}
