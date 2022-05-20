package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Getter @Setter @Builder
@ToString
@AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "tbl_vacation") @ApiModel(value = "휴가 관리 엔티티")
public class Vacation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "휴가 데이터 시퀀스")
    @Column(name = "vacation_num", insertable = false)
    private Long vacationNum;

    @ApiModelProperty(value = "휴가 시작일")
    @Column(name = "start_date")
    private LocalDate startDate;

    @ApiModelProperty(value = "휴가 종료일")
    @Column(name = "end_date")
    private LocalDate endDate;

    @ApiModelProperty(value = "휴가 최종 종료일")
    @Column(name = "final_end_date")
    private LocalDate finalEndDate;

    @ApiModelProperty(value = "비상연락망")
    private String contact;

    @ApiModelProperty(value = "사유")
    private String reason;

    @ApiModelProperty(value = "휴가종류")
    @Column(name = "vacation_type")
    private String vacationType;

    @ApiModelProperty(value = "승인상태(W: 대기중, T: 팀장승인, F: 최종승인, C: 반려)")
    @Column(name = "approval_status")
    private char approvalStatus;

    @ApiModelProperty(value = "반려 사유")
    @Column(name = "companion_reason")
    private String companionReason;

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;
}
