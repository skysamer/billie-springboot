package com.lab.smartmobility.billie.vacation.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class VacationCalendarForm {
    @ApiModelProperty(value = "휴가 데이터 시퀀스")
    private final Long vacationId;

    @ApiModelProperty(value = "휴가 시작일")
    private final LocalDate startDate;

    @ApiModelProperty(value = "휴가 종료일")
    private final LocalDate endDate;

    @ApiModelProperty(value = "직원이름")
    private final String name;

    @ApiModelProperty(value = "부서명")
    private final String department;

    @QueryProjection
    public VacationCalendarForm(Long vacationId, LocalDate startDate, LocalDate endDate, String name, String department) {
        this.vacationId = vacationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.department = department;
    }
}
