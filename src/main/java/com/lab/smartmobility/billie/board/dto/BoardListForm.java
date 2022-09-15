package com.lab.smartmobility.billie.board.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "자유게시판 목록 폼")
public class BoardListForm {
    @ApiModelProperty(value = "번호")
    private Long id;

    @ApiModelProperty(value = "제목")
    private String title;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "조회수")
    private long views;

    @ApiModelProperty(value = "댓글 수")
    private int replyCnt;

    @ApiModelProperty(value = "좋아요 수")
    private long likes;

    @ApiModelProperty(value = "생성일")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "수정일")
    private LocalDateTime modifiedAt;
}
