package com.lab.smartmobility.billie.announcement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "공지 및 내규 상세 조회 폼")
public class AnnouncementDetailsForm {
    @ApiModelProperty(value = "번호")
    private Long id;

    @ApiModelProperty(value = "종류")
    private String type;

    @ApiModelProperty(value = "제목")
    private String title;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "메인공지 여부")
    private int isMain;

    @ApiModelProperty(value = "조회수")
    private long views;

    @ApiModelProperty(value = "좋아요")
    private int likes;

    @ApiModelProperty(value = "생성일")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "수정일")
    private LocalDateTime modifiedAt;

    @ApiModelProperty(value = "내가 좋아요를 눌렀는지 여부")
    private boolean isLiked;

    @ApiModelProperty(value = "첨부파일 정보 (uuid가 포함된 파일이름)")
    private final List<String> filename = new ArrayList<>();

    public void addFilename(List<String> filename){
        this.filename.addAll(filename);
    }

    public void checkIsLiked(boolean isLiked){
        this.isLiked = isLiked;
    }
}
