package com.lab.smartmobility.billie.dto.vehicle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "차량 예약 신청 폼 데이터")
public class ApplyRentalVehicleDTO {
    @ApiModelProperty(value = "차종 및 번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String vehicleName;

    @ApiModelProperty(value = "대여일")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate dateOfRental;

    @ApiModelProperty(value = "대여예정시간")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalTime timeOfRental;

    @ApiModelProperty(value = "반납예정일")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate expectedReturnDate;

    @ApiModelProperty(value = "반납예정시간")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalTime expectedReturnTime;

    @ApiModelProperty(value = "대여자 고유번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long staffNum;

    @ApiModelProperty(value = "동승자")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String passenger;

    @ApiModelProperty(value = "내용(장소)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String content;
}
