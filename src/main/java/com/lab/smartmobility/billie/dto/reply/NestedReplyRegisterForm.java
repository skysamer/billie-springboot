package com.lab.smartmobility.billie.dto.reply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "댓글 등록 폼")
public class NestedReplyRegisterForm {
    @ApiModelProperty(value = "내용")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String content;

    @ApiModelProperty(value = "글번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long boardId;

    @ApiModelProperty(value = "부모댓글 번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long parentId;
}
