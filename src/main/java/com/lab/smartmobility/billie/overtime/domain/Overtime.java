package com.lab.smartmobility.billie.overtime.domain;

import com.lab.smartmobility.billie.staff.domain.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.lab.smartmobility.billie.overtime.domain.ApprovalStatus.*;
import static javax.persistence.FetchType.LAZY;

@Getter
@Entity @Table(name = "tbl_overtime")
@ApiModel(value = "추가근무 관리 엔티티")
public class Overtime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "추가근무 데이터 시퀀스")
    @Column(name = "overtime_id", insertable = false)
    private Long id;

    @ApiModelProperty(value = "날짜")
    @Column(name = "day_of_overtime")
    private LocalDate dayOfOvertime;

    @ApiModelProperty(value = "시작시간")
    @Column(name = "start_time")
    private LocalTime startTime;

    @ApiModelProperty(value = "종료시간")
    @Column(name = "end_time")
    private LocalTime endTime;

    @ApiModelProperty(value = "식사여부")
    @Column(name = "is_meal")
    private boolean isMeal;

    @ApiModelProperty(value = "내용 (사유)")
    private String content;

    @ApiModelProperty(value = "결제상태 (WAITING, PRE, CONFIRMATION, FINAL, COMPANION")
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @ApiModelProperty(value = "반려사유")
    @Column(name = "companion_reason")
    private String companionReason;

    @ApiModelProperty(value = "제출시간")
    @Column(name = "sub_time")
    private double subTime;

    @ApiModelProperty(value = "인정시간")
    @Column(name = "admit_time")
    private Double admitTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "staff_num")
    private Staff staff;

    @PrePersist
    public void prePersist(){
        this.approvalStatus = this.approvalStatus == null ? WAITING : this.approvalStatus;
    }

    public void calculateSubTime(LocalTime startTime, LocalTime endTime, int isMeal){
        Duration duration = Duration.between(startTime, endTime);
        double subTime = (double) duration.getSeconds() / (60 * 60);
        if(isMeal == 1){
            this.subTime = subTime - 1;
            return;
        }
        this.subTime = subTime;
    }

    public void setApplicant(Staff applicant){
        this.staff = applicant;
    }

    public void reject(String reason){
        this.approvalStatus = COMPANION;
        this.companionReason = reason;
    }

    public void confirm(LocalTime finalEndTime){
        this.endTime = finalEndTime;
        this.approvalStatus = CONFIRMATION;
    }

    public void finalApprove(double admitTime){
        this.admitTime = admitTime;
        this.approvalStatus = FINAL;
    }

    public void preApprove(){
        this.approvalStatus = PRE;
    }
}
