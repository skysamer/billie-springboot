package com.lab.smartmobility.billie.corporation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "폐기사유 폼")
public class DisposalForm {
    @ApiModelProperty(value = "폐기사유")
    private String reasonForDisposal;
}
