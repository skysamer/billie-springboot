package com.lab.smartmobility.billie.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "조건 별 데이터의 전체 개수 조회 엔티티")
public class TotalCount {
    @ApiModelProperty(value = "조건 별 전체 데이터 개수")
    private long totalCount;
}
