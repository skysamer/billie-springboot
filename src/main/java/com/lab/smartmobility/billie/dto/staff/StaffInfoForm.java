package com.lab.smartmobility.billie.dto.staff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;

@Data @ApiModel(value = "마이페이지 직원정보 폼")
public class StaffInfoForm {
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

    @ApiModelProperty(value = "생년월일")
    private LocalDate birth;

    @ApiModelProperty(value = "입사일")
    private LocalDate hireDate;

    @ApiModelProperty(value = "최종학위")
    private String degree;

    @ApiModelProperty(value = "최종졸업학교")
    private String graduationSchool;

    @ApiModelProperty(value = "전공")
    private String major;

    @ApiModelProperty(value = "졸업연도")
    private String graduationYear;

    @ApiModelProperty(value = "연구자번호")
    private String researcherNumber;

    @ApiModelProperty(value = "퇴사여부 (0:퇴사x, 1:퇴사o)")
    private int isResigned;

    @ApiModelProperty(value = "영문이름")
    private String englishName;
}
