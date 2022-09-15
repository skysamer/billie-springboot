package com.lab.smartmobility.billie.board.domain;

import com.lab.smartmobility.billie.global.domain.BaseTimeEntity;
import com.lab.smartmobility.billie.staff.domain.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "자유게시판 엔티티")
@Entity @Table(name = "tbl_board")
public class Board extends BaseTimeEntity {
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
    @Column(name = "reply_cnt")
    private int replyCnt;

    @ApiModelProperty(value = "0: 실명, 1: 익명")
    @Column(name = "is_anonymous")
    private int isAnonymous;

    @ApiModelProperty(value = "좋아요 수")
    private long likes;

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Reply> replyList = new ArrayList<>();

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public void plusReplyCnt(){
        this.replyCnt++;
    }

    public void plusLikes(){
        this.likes++;
    }

    public void minusLikes(){
        this.likes--;
    }
}
