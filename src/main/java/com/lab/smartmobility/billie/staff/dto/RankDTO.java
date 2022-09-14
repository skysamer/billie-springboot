package com.lab.smartmobility.billie.staff.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "직급 드롭다운 데이터 셋")
@EqualsAndHashCode(of = "rank")
public class RankDTO {
    private String rank;
}
