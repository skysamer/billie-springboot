package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.*;

@Getter
@ApiModel(value = "자유게시판 엔티티")
@Entity @Table(name = "tbl_board")
public class Board extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
