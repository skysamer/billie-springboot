package com.lab.smartmobility.billie.overtime.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@ApiModel(value = "추가근무 캘린더 폼")
public class OvertimeMonthlyForm {
    @ApiModelProperty(value = "추가근무 시퀀스")
    private final Long id;

    @ApiModelProperty(value = "날짜")
    private final LocalDate dayOfOvertime;

    @ApiModelProperty(value = "직원이름")
    private final String name;

    @ApiModelProperty(value = "부서")
    private final String department;

    @QueryProjection
    public OvertimeMonthlyForm(Long id, LocalDate dayOfOvertime, String name, String department) {
        this.id = id;
        this.dayOfOvertime = dayOfOvertime;
        this.name = name;
        this.department = department;
    }
}
