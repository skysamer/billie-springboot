package com.lab.smartmobility.billie.dto.reply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "대댓글 폼")
public class NestedReplyResponseForm {
    @ApiModelProperty(value = "부모댓글번호")
    private Long id;

    @ApiModelProperty(value = "번호")
    private Long parentId;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "생성일")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "수정일")
    private LocalDateTime modifiedAt;

    @ApiModelProperty(value = "0: 실명, 1: 익명")
    private int isAnonymous;

    @ApiModelProperty(value = "직원 고유 번호")
    private Long staffNum;

    @ApiModelProperty(value = "이름")
    private String name;
}
