package com.lab.smartmobility.billie.traffic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor @Builder
@ApiModel(value = "교통카드 정보 등록 및 수정 폼")
public class TrafficCardForm {
    @ApiModelProperty(value = "교통카드 고유 번호(혹은 이름)")
    @Column(name = "card_num")
    private Long cardNum;

    @ApiModelProperty(value = "카드회사")
    private String company;

    @ApiModelProperty(value = "카드 구매일자")
    @Column(name = "date_of_purchase")
    private LocalDate dateOfPurchase;

    @ApiModelProperty(value = "잔액")
    private int balance;

    @ApiModelProperty(value = "유효기간")
    @Nullable
    private String validity;
}
