package com.lab.smartmobility.billie.vacation.domain;

import com.lab.smartmobility.billie.staff.domain.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @ToString
@Entity @Table(name = "tbl_vacation")
@ApiModel(value = "휴가 관리 엔티티")
public class Vacation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ApiModelProperty(value = "승인상태(WAITING, DEPARTMENT, FINAL, COMPANION, CANCEL)")
    @Column(name = "approval_status")
    @Enumerated(value = EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @ApiModelProperty(value = "반려 사유")
    @Column(name = "companion_reason")
    private String companionReason;

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;

    @PrePersist
    public void prePersist(){
        this.approvalStatus = this.approvalStatus == null ? ApprovalStatus.WAITING : this.approvalStatus;
    }

    public void register(Staff staff){
        this.staff = staff;
    }

    public void cancel(){
        this.approvalStatus = ApprovalStatus.CANCEL;
    }

    public void reject(String reason){
        this.approvalStatus = ApprovalStatus.COMPANION;
        this.companionReason = reason;
    }

    public void approve(ApprovalStatus approvalStatus){
        this.approvalStatus = approvalStatus;
    }
}
