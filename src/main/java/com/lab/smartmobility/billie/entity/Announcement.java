package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity @Table(name = "tbl_announcement")
@ApiModel(value = "공지 및 내규 엔티티")
public class Announcement extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "번호")
    private Long id;

    @ApiModelProperty(value = "종류")
    private String type;

    @ApiModelProperty(value = "제목")
    private String title;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "메인공지 여부")
    @Column(name = "is_main")
    private int isMain;

    @ApiModelProperty(value = "조회수")
    private long views;

    @ApiModelProperty(value = "좋아요")
    private int likes;

    public void cancelMain(){
        this.isMain = 0;
    }

    public void plusViews(){
        this.views++;
    }

    public void plusLike(){
        this.likes++;
    }

    public void minusLike(){
        this.likes--;
    }
}
