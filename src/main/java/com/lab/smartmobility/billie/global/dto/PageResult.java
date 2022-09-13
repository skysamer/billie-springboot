package com.lab.smartmobility.billie.global.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "페이징을 위한 데이터 전달 객체")
public class PageResult<T> {
    @ApiModelProperty(value = "내용")
    private List<T> content;

    @ApiModelProperty(value = "전체 개수")
    private long count;
}
