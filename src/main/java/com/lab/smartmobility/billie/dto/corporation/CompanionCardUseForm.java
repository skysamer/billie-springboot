package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "카드 사용 일괄 반려 폼")
public class CompanionCardUseForm {
    @ApiModelProperty(value = "법인카드 사용 신청 고유 시퀀스")
    private Long applicationId;

    @ApiModelProperty(value = "반려사유")
    private String reason;
}
