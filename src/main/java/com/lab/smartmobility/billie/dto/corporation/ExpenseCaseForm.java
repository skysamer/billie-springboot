package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
@ApiModel(value = "개인 경비청구 건 신청 폼")
public class ExpenseCaseForm {
    @ApiModelProperty(value = "사용날짜")
    private LocalDate usedAt;

    @ApiModelProperty(value = "사용목적")
    private String purpose;

    @ApiModelProperty(value = "사용금액")
    private int amount;
}
