package com.lab.smartmobility.billie.dto.announcement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class AnnouncementRegisterForm {
    @ApiModelProperty(value = "종류")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String type;

    @ApiModelProperty(value = "제목")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String title;

    @ApiModelProperty(value = "내용")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String content;

    @ApiModelProperty(value = "메인공지 여부")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private int isMain;

    @ApiModelProperty(value = "메인공지 개수가 초과하는지 여부")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private int isExceedMainCount;
}
