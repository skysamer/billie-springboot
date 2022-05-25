package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter @Setter @ToString
@Builder @AllArgsConstructor @NoArgsConstructor
@Entity @ApiModel(value = "법인카드 엔티티")
@Table(name = "tbl_corporation_card")
public class CorporationCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    @ApiModelProperty(value = "법인카드 고유 시퀀스")
    private Long cardId;

    @ApiModelProperty(value = "카드사")
    private String company;

    @ApiModelProperty(value = "구매일")
    @Column(name = "purchased_at")
    private LocalDate purchasedAt;

    @ApiModelProperty(value = "16자리 카드번호")
    @Column(name = "card_number")
    private String cardNumber;

    @ApiModelProperty(value = "유효기간")
    private String validity;

    @ApiModelProperty(value = "대여상태")
    @Column(name = "rental_status")
    private int rentalStatus;
}
