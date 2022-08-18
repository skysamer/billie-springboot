package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @ApiModel(value = "자유게시판 좋아요 엔티티")
@Entity @Table(name = "tbl_board_like")
@NoArgsConstructor
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "시퀀스")
    private Long id;

    @ApiModelProperty(value = "직원 이메일")
    private String email;

    @ApiModelProperty(value = "글번호")
    @Column(name = "board_id")
    private Long boardId;

    public BoardLike(String email, Long boardId){
        this.email = email;
        this.boardId = boardId;
    }
}
