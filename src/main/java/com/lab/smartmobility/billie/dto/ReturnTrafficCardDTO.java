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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "교통카드 반납 폼 데이터 객체")
public class ReturnTrafficCardDTO {
    @ApiModelProperty(value = "교통카드 예약 시퀀스")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long reservationNum;

    @ApiModelProperty(value = "카드번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long cardNum;

    @ApiModelProperty(value = "잔액")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private int balance;

    @ApiModelProperty(value = "반납날짜")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate dateOfReturn;

    @ApiModelProperty(value = "반납시간")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalTime timeOfReturn;
}
