package com.lab.smartmobility.billie.vacation.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter @ToString
public class VacationExcelForm {
    @ApiModelProperty(value = "휴가 데이터 시퀀스")
    private final Long vacationId;

    @ApiModelProperty(value = "직원이름")
    private final String name;

    @ApiModelProperty(value = "휴가 시작일")
    private final LocalDate startDate;

    @ApiModelProperty(value = "휴가 종료일")
    @Column(name = "end_date")
    private final LocalDate endDate;

    @ApiModelProperty(value = "사유")
    private final String reason;

    @ApiModelProperty(value = "휴가종류")
    private final String vacationType;

    @ApiModelProperty(value = "승인상태(WAITING, DEPARTMENT, FINAL, COMPANION, CANCEL)")
    private final String approvalStatus;

    @ApiModelProperty(value = "사번")
    private final String employeeNumber;

    @QueryProjection
    public VacationExcelForm(Long vacationId, String name, LocalDate startDate, LocalDate endDate, String reason, String vacationType, String approvalStatus, String employeeNumber) {
        this.vacationId = vacationId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.vacationType = vacationType;
        this.approvalStatus = approvalStatus;
        this.employeeNumber = employeeNumber;
    }
}
