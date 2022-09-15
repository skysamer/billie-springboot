package com.lab.smartmobility.billie.global.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter @Setter @ToString
@RequiredArgsConstructor
@ApiModel(value = "api 요청 상태값")
@AllArgsConstructor @Builder
public class HttpBodyMessage {
    @ApiModelProperty(value = "상태코드")
    private String code;

    @ApiModelProperty(value = "상태 메시지", dataType = "object")
    private Object message;
}
