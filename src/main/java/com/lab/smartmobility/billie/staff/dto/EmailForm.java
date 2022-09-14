package com.lab.smartmobility.billie.staff.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "이메일 전송 폼")
public class EmailForm {
    @ApiModelProperty(value = "이메일")
    private String email;
}
