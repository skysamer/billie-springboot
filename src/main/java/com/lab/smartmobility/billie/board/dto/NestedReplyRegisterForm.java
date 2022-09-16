package com.lab.smartmobility.billie.board.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "대댓글 등록 폼")
public class NestedReplyRegisterForm {
    @ApiModelProperty(value = "내용")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private String content;

    @ApiModelProperty(value = "글번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long boardId;

    @ApiModelProperty(value = "0: 실명, 1: 익명")
    private int isAnonymous;

    @ApiModelProperty(value = "부모댓글 번호")
    @NotNull(message="해당 값은 null 일 수 없습니다.")
    private Long parentId;
}
