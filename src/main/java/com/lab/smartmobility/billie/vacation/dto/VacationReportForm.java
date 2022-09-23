package com.lab.smartmobility.billie.vacation.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class VacationReportForm {
    @ApiModelProperty(value = "사용개수")
    private final double count;

    @ApiModelProperty(value = "사용날짜")
    private final LocalDate startDate;

    @ApiModelProperty(value = "사용날짜")
    private final LocalDate endDate;

    @ApiModelProperty(value = "휴가종류")
    private final String note;

    @ApiModelProperty(value = "이름")
    private final String name;

    @ApiModelProperty(value = "부서")
    private final String department;

    @ApiModelProperty(value = "잔여휴가개수 (이미 연산된 고정값입니다.)")
    private final double vacationCount;

    @QueryProjection
    public VacationReportForm(double count, LocalDate startDate, LocalDate endDate, String note, String name, String department, double vacationCount) {
        this.count = count;
        this.startDate = startDate;
        this.endDate = endDate;
        this.note = note;
        this.name = name;
        this.department = department;
        this.vacationCount = vacationCount;
    }
}
