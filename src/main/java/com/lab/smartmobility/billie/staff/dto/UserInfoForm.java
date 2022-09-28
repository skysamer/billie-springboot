package com.lab.smartmobility.billie.staff.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@ApiModel(value = "로그인한 유저의 정보 전달 폼")
public class UserInfoForm {
    @ApiModelProperty(value = "직원 고유 번호")
    private Long staffNum;

    @ApiModelProperty(value = "이름")
    private String name;

    @ApiModelProperty(value = "직급")
    private String rank;

    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "전화번호")
    private String phone;

    @ApiModelProperty(value = "부서")
    private String department;

    @ApiModelProperty(value = "권한등급")
    private String role;

    @ApiModelProperty(value = "생년월일")
    private LocalDate birth;

    @ApiModelProperty(value = "입사일")
    private LocalDate hireDate;

    @ApiModelProperty(value = "인증여부")
    private final Boolean isAuth = true;
}
