package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ApiModel(value = "법인카드 사용 신청 폼")
public class ApplyCorporationCardForm {
    @ApiModelProperty(value = "사용시작일 (yyyy-MM-dd)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate startDate;

    @ApiModelProperty(value = "사용시작시간 (hh:mm)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalTime startTime;

    @ApiModelProperty(value = "사용종료일 (yyyy-MM-dd)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate endDate;

    @ApiModelProperty(value = "사용종료시간 (hh:mm)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalTime endTime;

    @ApiModelProperty(value = "내용(용도)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String content;

    @ApiModelProperty(value = "대여자 고유번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long staffNum;
}
