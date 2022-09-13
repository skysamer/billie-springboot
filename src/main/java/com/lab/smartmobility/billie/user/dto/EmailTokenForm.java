package com.lab.smartmobility.billie.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "이메일 토큰 폼")
public class EmailTokenForm {
    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "이메일 토큰")
    private String emailToken;
}
