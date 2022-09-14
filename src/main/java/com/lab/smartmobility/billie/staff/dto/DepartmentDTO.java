package com.lab.smartmobility.billie.staff.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "부서 드롭다운 데이터 셋")
@EqualsAndHashCode(of = "department")
public class DepartmentDTO {
    private String department;
}
