package com.lab.smartmobility.billie.board.domain;

import com.lab.smartmobility.billie.global.domain.BaseTimeEntity;
import com.lab.smartmobility.billie.staff.domain.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @ApiModel(value = "댓글 엔티티")
@Entity @Table(name = "tbl_reply")
public class Reply extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "번호")
    private Long id;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "0: 실명, 1: 익명")
    @Column(name = "is_anonymous")
    private int isAnonymous;

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Reply parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Reply> children = new ArrayList<>();

    public void insert(Staff staff, Board board){
        this.staff = staff;
        this.board = board;
    }

    public void insertNested(Staff staff, Board board, Reply parent, int isAnonymous){
        this.staff = staff;
        this.board = board;
        this.parent = parent;
        this.isAnonymous = isAnonymous;
    }
}
