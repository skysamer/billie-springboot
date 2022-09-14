package com.lab.smartmobility.billie.traffic.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;

@Getter @ToString
@AllArgsConstructor @NoArgsConstructor @Builder
@Entity @ApiModel(value = "등록 교통카드 엔티티")
@Table(name = "tbl_traffic_card")
public class TrafficCard {
    @Id
    @ApiModelProperty(value = "교통카드 고유 번호(혹은 이름)")
    @Column(name = "card_num", unique = true)
    private Long cardNum;

    @ApiModelProperty(value = "카드회사")
    private String company;

    @ApiModelProperty(value = "카드 구매일자")
    @Column(name = "date_of_purchase")
    private LocalDate dateOfPurchase;

    @ApiModelProperty(value = "잔액")
    private int balance;

    @ApiModelProperty(value = "대여상태", notes = "폐기상태는 99")
    @Column(name = "rental_status")
    private int rentalStatus;

    @ApiModelProperty(value = "유효기간")
    @Nullable
    private String validity;

    @ApiModelProperty(value = "폐기이유")
    @Column(name = "discard_reason")
    private String discardReason;

    public void discard(int rentalStatus, String discardReason){
        this.rentalStatus = rentalStatus;
        this.discardReason = discardReason;
    }

    public void rent(int rentalStatus){
        this.rentalStatus = rentalStatus;
    }

    public void returnCard(int balance){
        this.rentalStatus = 0;
        this.balance = balance;
    }
}
