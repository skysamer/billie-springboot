package com.lab.smartmobility.billie.dto.vehicle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @ToString
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "반납 이력 조회 데이터 셋")
public class VehicleReturnHistoryInfo {
    @ApiModelProperty(value = "차량 예약 시퀀스")
    private Long rentNum;
    @ApiModelProperty(value = "차종 및 번호")
    private String vehicleName;
    @ApiModelProperty(value = "예약날짜 및 시간")
    private LocalDateTime rentedAt;
    @ApiModelProperty(value = "반납날짜 및 시간")
    private LocalDateTime returnedAt;
    @ApiModelProperty(value = "대여자 이름")
    private String render;
    @ApiModelProperty(value = "동승자")
    private String passenger;
    @ApiModelProperty(value = "내용(장소)")
    private String content;
    @ApiModelProperty(value = "주행 후 계기판")
    private int distanceDriven;
    @ApiModelProperty(value = "주차위치")
    private String parkingLoc;
}
