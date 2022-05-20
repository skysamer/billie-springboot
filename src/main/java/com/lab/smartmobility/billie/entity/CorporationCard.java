package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter @Setter @ToString
@Builder @AllArgsConstructor @NoArgsConstructor
@Entity @ApiModel(value = "법인카드 엔티티")
public class CorporationCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "법인카드 고유 시퀀스")
    private Long cardId;

    @ApiModelProperty(value = "카드사")
    private String company;

    @ApiModelProperty(value = "구매일")
    private LocalDate purchasedAt;

    @ApiModelProperty(value = "16자리 카드번호")
    private String cardNumber;

    @ApiModelProperty(value = "유효기간")
    private String validity;

    @ApiModelProperty(value = "대여상태")
    private int rentalStatus;
}
