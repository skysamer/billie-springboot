package com.lab.smartmobility.billie.overtime.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ApiModel(value = "추가근무 신청 폼")
public class OvertimeApplyForm {
    @ApiModelProperty(value = "날짜")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate dayOfOvertime;

    @ApiModelProperty(value = "시작시간 (hh:mm)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String startTime;

    @ApiModelProperty(value = "종료시간 (hh:mm)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String endTime;

    @ApiModelProperty(value = "식사여부")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private int isMeal;

    @ApiModelProperty(value = "내용 (사유)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String content;
}
