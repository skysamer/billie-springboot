package com.lab.smartmobility.billie.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "등록 교통카드 엔티티")
@Builder
public class VehicleDTO {
    @ApiModelProperty(value = "차량 시퀀스")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long vehicleNum;

    @ApiModelProperty(value = "차종")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String name;

    @ApiModelProperty(value = "차량번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String number;

    @ApiModelProperty(value = "주차위치")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String parkingLoc;

    @ApiModelProperty(value = "대여상태")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private int rentalStatus;
}
