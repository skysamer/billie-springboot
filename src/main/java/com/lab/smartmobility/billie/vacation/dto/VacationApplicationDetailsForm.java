package com.lab.smartmobility.billie.vacation.dto;

import com.lab.smartmobility.billie.vacation.domain.ApprovalStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "휴가 신청 상세 내역 폼")
public class VacationApplicationDetailsForm {
    @ApiModelProperty(value = "휴가 데이터 시퀀스")
    private final Long vacationId;

    @ApiModelProperty(value = "이름")
    private final String name;

    @ApiModelProperty(value = "부서")
    private final String department;

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

    @ApiModelProperty(value = "반려 사유")
    private String companionReason;

    @ApiModelProperty(value = "휴가종류")
    private final String vacationType;

    @ApiModelProperty(value = "승인상태(WAITING, DEPARTMENT, FINAL, COMPANION, CANCEL)")
    private final ApprovalStatus approvalStatus;

    @QueryProjection
    public VacationApplicationDetailsForm(Long vacationId, String name, String department, LocalDate startDate, LocalDate endDate,
                                          LocalTime workAt, LocalTime homeAt, String contact, String reason,
                                          String companionReason, String vacationType, ApprovalStatus approvalStatus) {
        this.vacationId = vacationId;
        this.name = name;
        this.department = department;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workAt = workAt;
        this.homeAt = homeAt;
        this.contact = contact;
        this.reason = reason;
        this.companionReason = companionReason;
        this.vacationType = vacationType;
        this.approvalStatus = approvalStatus;
    }
}
