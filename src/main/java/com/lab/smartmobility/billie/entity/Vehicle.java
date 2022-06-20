package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ApiModel(value = "등록 차량 정보 엔티티")
@Table(name = "tbl_vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "차량 시퀀스")
    @Column(name = "vehicle_num", insertable = false)
    private Long vehicleNum;

    @Column(name = "vehicle_name")
    @ApiModelProperty(value = "차종+고유번호")
    private String vehicleName;

    @Column(name = "parking_loc")
    @ApiModelProperty(value = "주차위치")
    private String parkingLoc;

    @Column(name = "date_of_purchase")
    @ApiModelProperty(value = "구매날짜")
    private LocalDate dateOfPurchase;

    @Column(name = "distance_driven")
    @ApiModelProperty(value = "주행거리")
    private int distanceDriven;

    @Column(name = "rental_status")
    @ApiModelProperty(value = "대여상태", notes = "폐기상태는 99")
    private int rentalStatus;

    @Column(name = "discard_reason")
    @ApiModelProperty(value = "폐기 사유")
    private String discardReason;

    public void update(int rentalStatus, String parkingLoc, int distanceDriven){
        this.rentalStatus = rentalStatus;
        this.parkingLoc = parkingLoc;
        this.distanceDriven = distanceDriven;
    }

    public void discard(int rentalStatus, String discardReason){
        this.rentalStatus = rentalStatus;
        this.discardReason = discardReason;
    }
}
