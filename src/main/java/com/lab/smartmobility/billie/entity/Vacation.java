package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity @Table(name = "tbl_vacation")
@ApiModel(value = "휴가 관리 엔티티")
public class Vacation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "휴가 데이터 시퀀스")
    @Column(name = "vacation_num", insertable = false)
    private Long vacationId;

    @ApiModelProperty(value = "휴가 시작일")
    @Column(name = "start_date")
    private LocalDate startDate;

    @ApiModelProperty(value = "휴가 종료일")
    @Column(name = "end_date")
    private LocalDate endDate;

    @ApiModelProperty(value = "출근시간 (반차일 경우)")
    @Column(name = "work_at")
    private LocalTime workAt;

    @ApiModelProperty(value = "퇴근시간 (반차일 경우)")
    @Column(name = "home_at")
    private LocalTime homeAt;

    @ApiModelProperty(value = "비상연락망")
    private String contact;

    @ApiModelProperty(value = "사유")
    private String reason;

    @ApiModelProperty(value = "휴가종류")
    @Column(name = "vacation_type")
    private String vacationType;

    @ApiModelProperty(value = "승인상태(w: 대기중, t: 팀장승인, f: 최종승인, c: 반려)")
    @Column(name = "approval_status")
    private Character approvalStatus;

    @ApiModelProperty(value = "반려 사유")
    @Column(name = "companion_reason")
    private String companionReason;

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;

    @PrePersist
    public void prePersist(){
        this.approvalStatus = this.approvalStatus == null ? 'w' : this.approvalStatus;
    }

    public void register(Staff staff){
        this.staff = staff;
    }
}
