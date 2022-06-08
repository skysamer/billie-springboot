package com.lab.smartmobility.billie.dto.staff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "이메일 전송 폼")
public class EmailForm {
    @ApiModelProperty(value = "이메일")
    private String email;
}
