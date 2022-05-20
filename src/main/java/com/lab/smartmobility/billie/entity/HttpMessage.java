package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@ApiModel(value = "api 요청 상태값")
@AllArgsConstructor
public class HttpMessage {
    @ApiModelProperty(value = "상태코드")
    private String code;

    @ApiModelProperty(value = "상태 메시지")
    private Object message;
}
