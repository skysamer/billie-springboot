package com.lab.smartmobility.billie.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "부서 드롭다운 데이터 셋")
@EqualsAndHashCode(of = "department")
public class DepartmentDTO {
    private String department;
}
