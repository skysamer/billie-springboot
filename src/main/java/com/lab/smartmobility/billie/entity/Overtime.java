package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @ToString @Builder
@Entity @Table(name = "tbl_overtime")
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "추가근무 관리 엔티티")
public class Overtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "추가근무 데이터 시퀀스")
    @Column(name = "overtime_num", insertable = false)
    private Long overtimeNum;

    @ApiModelProperty(value = "날짜")
    @Column(name = "date_of_overtime")
    private LocalDate dateOfOvertime;

    @ApiModelProperty(value = "식사여부")
    @Column(name = "whether_to_eat")
    private int whetherToEat;

    @ApiModelProperty(value = "시작시간")
    @Column(name = "started_at")
    private LocalTime startedAt;

    @ApiModelProperty(value = "종료시간")
    @Column(name = "finished_at")
    private LocalTime finishedAt;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "승인상태(W: 대기중, T: 팀장승인, F: 최종승인, C: 반려)")
    @Column(name = "approval_status")
    private char approvalStatus;

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;
}
