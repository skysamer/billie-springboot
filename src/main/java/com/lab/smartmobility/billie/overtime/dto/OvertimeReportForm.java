package com.lab.smartmobility.billie.overtime.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "추가근무 월별 리포트 폼")
public class OvertimeReportForm {
    @ApiModelProperty(value = "추가근무 데이터 시퀀스")
    private final Long id;

    @ApiModelProperty(value = "직원 고유 번호")
    private final Long staffNum;

    @ApiModelProperty(value = "이름")
    private final String name;

    @ApiModelProperty(value = "부서")
    private final String department;

    @ApiModelProperty(value = "추가근무시간 (단위 : 시간)")
    private final double overtimeHour;

    @ApiModelProperty(value = "날짜")
    private final LocalDate dayOfOvertime;

    @ApiModelProperty(value = "시작시간")
    private final String startTime;

    @ApiModelProperty(value = "종료시간")
    private final String endTime;

    @ApiModelProperty(value = "내용 (사유)")
    private final String content;

    @ApiModelProperty(value = "식사여부")
    private final int isMeal;

    @ApiModelProperty(value = "제출시간")
    private final double subTime;

    @ApiModelProperty(value = "인정시간")
    private final Double admitTime;

    @QueryProjection
    public OvertimeReportForm(Long id, Long staffNum, String name, String department,
                              double overtimeHour, LocalDate dayOfOvertime, int isMeal,
                              double subTime, Double admitTime, String startTime,
                              String endTime, String content) {
        this.id = id;
        this.staffNum = staffNum;
        this.name = name;
        this.department = department;
        this.overtimeHour = overtimeHour;
        this.dayOfOvertime = dayOfOvertime;
        this.isMeal = isMeal;
        this.subTime = subTime;
        this.admitTime = admitTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
    }
}
