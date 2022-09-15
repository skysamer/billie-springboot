package com.lab.smartmobility.billie.global.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "메일 발송 엔티티")
public class Mail {
    @ApiModelProperty(value = "제목")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String title;

    @ApiModelProperty(value = "내용")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String content;

    @ApiModelProperty(value = "이메일 주소")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String address;
}
