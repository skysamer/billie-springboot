package com.lab.smartmobility.billie.dto.traffic;

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
@ApiModel(value = "교통카드 대여신청 폼 전달 객체")
public class TrafficCardApplyDTO {
    @ApiModelProperty(value = "카드번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long cardNum;

    @ApiModelProperty(value = "대여일")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate dateOfRental;

    @ApiModelProperty(value = "대여시간")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalTime timeOfRental;

    @ApiModelProperty(value = "반납예정일")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate expectedReturnDate;

    @ApiModelProperty(value = "반납예정시간")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalTime expectedReturnTime;

    @ApiModelProperty(value = "내용(사유)")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String content;

    @ApiModelProperty(value = "대여자 직원 번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long staffNum;
}
