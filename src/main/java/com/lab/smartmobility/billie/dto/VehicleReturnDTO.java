package com.lab.smartmobility.billie.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@ApiModel(value = "차량 반납 폼 데이터 전달 객체")
@NoArgsConstructor
public class VehicleReturnDTO {
    @ApiModelProperty(value = "차량 예약 고유번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long rentNum;

    @ApiModelProperty(value = "차종 및 번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String vehicleName;

    @ApiModelProperty(value = "반납날짜")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate dateOfReturn;

    @ApiModelProperty(value = "반납시간")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalTime timeOfReturn;

    @ApiModelProperty(value = "주행 후 계기판")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private int distanceDriven;

    @ApiModelProperty(value = "주차위치")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String parkingLoc;

}
