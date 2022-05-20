package com.lab.smartmobility.billie.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
@ApiModel(value = "나의 남은 휴가 개수, 전체 개수, 사용 개수 및 소진기한 전달 객체")
public class MyVacationDTO {
    @ApiModelProperty(value = "남은휴가개수")
    private double remainingVacationCount;
    @ApiModelProperty(value = "전체 휴가개수")
    private double totalVacationCount;
    @ApiModelProperty(value = "사용한 휴가 개수")
    private double numberOfUses;

    @ApiModelProperty(value = "소진기한 시작일")
    private LocalDate startDate;
    @ApiModelProperty(value = "소진기한 종료일")
    private LocalDate endDate;
}
