package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "카드 사용 일괄 승인 폼")
public class ApprovalCardUseForm {
    @ApiModelProperty(value = "법인카드 사용 신청 고유 시퀀스")
    private Long applicationId;

    @ApiModelProperty(value = "법인카드 이름 (경비청구의 경우는 '개인경비 청구', 부서장 승인의 경우 null 입력)")
    private String cardName;

    @ApiModelProperty(value = "카드사 (경비청구의 혹은 부서장 승인의 경우 null 입력)")
    private String company;
}
