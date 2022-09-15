package com.lab.smartmobility.billie.dto.board;

import com.lab.smartmobility.billie.dto.ReplyResponseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "자유게시판 상세 폼")
public class BoardDetailsForm {
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

    @ApiModelProperty(value = "0: 실명, 1: 익명")
    private int isAnonymous;

    @ApiModelProperty(value = "생성일")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "수정일")
    private LocalDateTime modifiedAt;

    @ApiModelProperty(value = "직원 고유 번호")
    private Long staffNum;

    @ApiModelProperty(value = "이름")
    private String name;

    @ApiModelProperty(value = "내가 좋아요를 눌렀는지 여부")
    private boolean isLiked;

    @ApiModelProperty(value = "댓글 목록")
    private List<ReplyResponseForm> replyList = new ArrayList<>();

    public void addReply(List<ReplyResponseForm> replyList){
        this.replyList = replyList;
    }

    public void checkIsLiked(boolean isLiked){
        this.isLiked = isLiked;
    }
}
