package com.lab.smartmobility.billie.overtime.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@ApiModel(value = "근무확정 폼")
public class OvertimeConfirmationForm {
    @ApiModelProperty(value = "시작시간 (hh:mm)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String startTime;

    @ApiModelProperty(value = "종료시간 (hh:mm)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String endTime;
}
