package com.lab.smartmobility.billie.announcement.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "공지 및 내규 목록 조회 폼")
public class AnnouncementListForm {
    @ApiModelProperty(value = "번호")
    private final Long id;

    @ApiModelProperty(value = "종류")
    private final String type;

    @ApiModelProperty(value = "제목")
    private final String title;

    @ApiModelProperty(value = "내용")
    private final String content;

    @ApiModelProperty(value = "메인공지 여부")
    private final int isMain;

    @ApiModelProperty(value = "조회수")
    private final long views;

    @ApiModelProperty(value = "좋아요")
    private final int likes;

    @QueryProjection
    public AnnouncementListForm(Long id, String type, String title, String content, int isMain, long views, int likes) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.isMain = isMain;
        this.views = views;
        this.likes = likes;
    }
}
