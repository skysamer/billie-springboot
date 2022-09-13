package com.lab.smartmobility.billie.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "회원가입 폼")
public class SignUpForm {
    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "비밀번호")
    @Length(min = 6)
    private String password;
}
