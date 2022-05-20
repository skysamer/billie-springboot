package com.lab.smartmobility.billie.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "휴가신청 폼 데이터")
public class ApplyVacationForm {
    @ApiModelProperty(value = "시작일")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate startDate;

    @ApiModelProperty(value = "종료일")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate endDate;

    @ApiModelProperty(value = "휴가종류")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String vacationType;

    @ApiModelProperty(value = "최종 종료일")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private LocalDate finalEndDate;

    @ApiModelProperty(value = "비상연락망")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String contact;

    @ApiModelProperty(value = "사유")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String reason;

    @ApiModelProperty(value = "직원 고유번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long staffNum;
}
