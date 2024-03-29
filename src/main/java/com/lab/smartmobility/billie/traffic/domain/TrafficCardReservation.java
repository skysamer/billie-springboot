package com.lab.smartmobility.billie.traffic.domain;

import com.lab.smartmobility.billie.staff.domain.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @ToString
@AllArgsConstructor @NoArgsConstructor @Builder
@Entity @Table(name = "tbl_traffic_card_reservation")
@ApiModel(value = "교통카드 예약 관리 엔티티")
public class TrafficCardReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "교통카드 예약 시퀀스")
    @Column(name = "reservation_num")
    private Long reservationNum;

    @ApiModelProperty(value = "내용(사유)")
    private String content;

    @ApiModelProperty(value = "잔액이력")
    @Column(name = "balance_history")
    private Integer balanceHistory;

    @ApiModelProperty(value = "대여날짜 및 시간")
    @Column(name = "rented_at")
    private LocalDateTime rentedAt;

    @ApiModelProperty(value = "반납날짜 및 시간")
    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @ApiModelProperty(value = "반납상태")
    @Column(name = "return_status")
    private int returnStatus;

    @ManyToOne
    @JoinColumn(name = "card_num")
    private TrafficCard trafficCard;

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;

    public void updateReservationInfo(TrafficCard trafficCard, LocalDateTime rentedAt, LocalDateTime returnedAt){
        this.trafficCard = trafficCard;
        this.rentedAt = rentedAt;
        this.returnedAt = returnedAt;
    }

    public void insert(TrafficCard card, Staff render, LocalDateTime rentedAt, LocalDateTime returnedAt){
        this.trafficCard = card;
        this.staff = render;
        this.rentedAt = rentedAt;
        this.returnedAt = returnedAt;
    }

    public void update(int returnStatus, LocalDateTime returnedAt, int balanceHistory){
        this.returnStatus = returnStatus;
        this.returnedAt = returnedAt;
        this.balanceHistory = balanceHistory;
    }
}
