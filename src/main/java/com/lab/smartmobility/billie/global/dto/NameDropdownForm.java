package com.lab.smartmobility.billie.global.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "직원 이름 드롭다운 폼")
public class NameDropdownForm {
    @ApiModelProperty(value = "직원이름")
    private final String name;

    @QueryProjection
    public NameDropdownForm(String name) {
        this.name = name;
    }
}
