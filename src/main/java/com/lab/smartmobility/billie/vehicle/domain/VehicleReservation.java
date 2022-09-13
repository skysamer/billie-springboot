package com.lab.smartmobility.billie.vehicle.domain;

import com.lab.smartmobility.billie.entity.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @ToString
@AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@ApiModel(value = "차량 예약 관리 엔티티")
@Table(name = "tbl_vehicle_reservation")
public class VehicleReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "차량 예약 고유번호")
    @Column(name = "rent_num", insertable = false)
    private Long rentNum;

    @ApiModelProperty(value = "차량 대여일 및 시간")
    @Column(name = "rented_at")
    private LocalDateTime rentedAt;

    @ApiModelProperty(value = "차량 반납일 및 시간")
    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @ApiModelProperty(value = "동승자")
    private String passenger;

    @ApiModelProperty(value = "장소(내용)")
    private String content;

    @Column(name = "parking_loc")
    @ApiModelProperty(value = "주차위치")
    private String parkingLoc;

    @Column(name = "distance_driven")
    @ApiModelProperty(value = "주행거리")
    private int distanceDriven;

    @Column(name = "return_status_code")
    @ApiModelProperty(value = "반납상태코드")
    private int returnStatusCode;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "vehicle_num")
    @ApiModelProperty(value = "대여 차량 정보")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "staff_num", referencedColumnName = "staff_num")
    private Staff staff;

    public void modifyRentInfoByAdmin(Vehicle vehicle, LocalDateTime rentedAt, LocalDateTime returnedAt){
        this.vehicle = vehicle;
        this.rentedAt = rentedAt;
        this.returnedAt = returnedAt;
    }

    public void update(LocalDateTime returnedAt){
        this.returnStatusCode = 1;
        this.returnedAt = returnedAt;
    }

    public void insert(Vehicle vehicle, Staff staff, LocalDateTime rentedAt, LocalDateTime returnedAt){
        this.vehicle = vehicle;
        this.staff = staff;
        this.rentedAt = rentedAt;
        this.returnedAt = returnedAt;
    }

    public void modifyInfo(Vehicle vehicle, LocalDateTime rentedAt, LocalDateTime returnedAt){
        this.vehicle = vehicle;
        this.rentedAt = rentedAt;
        this.returnedAt = returnedAt;
    }
}
