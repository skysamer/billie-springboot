package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
@ApiModel(value = "법인카드 사용 건 입력 폼")
public class CorporationUseCaseForm {
    @ApiModelProperty(value = "사용날짜")
    private LocalDate usedAt;

    @ApiModelProperty(value = "사용목적")
    private String purpose;

    @ApiModelProperty(value = "사용금액")
    private int amount;

    @ApiModelProperty(value = "참석자")
    private String participants;

}
