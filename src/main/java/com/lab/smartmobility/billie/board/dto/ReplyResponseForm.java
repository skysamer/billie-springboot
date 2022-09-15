package com.lab.smartmobility.billie.board.dto;

import com.lab.smartmobility.billie.board.dto.NestedReplyResponseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "댓글 폼")
public class ReplyResponseForm {
    @ApiModelProperty(value = "번호")
    private Long id;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "생성일")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "수정일")
    private LocalDateTime modifiedAt;

    @ApiModelProperty(value = "직원 고유 번호")
    private Long staffNum;

    @ApiModelProperty(value = "0: 실명, 1: 익명")
    private int isAnonymous;

    @ApiModelProperty(value = "이름")
    private String name;

    @ApiModelProperty(value = "대댓글 리스트")
    private List<NestedReplyResponseForm> children = new ArrayList<>();

    public void addChildren(List<NestedReplyResponseForm> children){
        this.children.addAll(children);
    }
}
