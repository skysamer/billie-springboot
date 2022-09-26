package com.lab.smartmobility.billie.vacation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "휴가 반려 폼")
public class VacationCompanionForm {
    @ApiModelProperty(value = "휴가 데이터 시퀀스")
    private Long vacationId;

    @ApiModelProperty(value = "반려 사유")
    private String companionReason;
}
