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

    @ApiModelProperty(value = "시작시간 (hh:mm)")
    @Column(name = "start_time")
    private String startTime;

    @ApiModelProperty(value = "종료시간 (hh:mm)")
    @Column(name = "end_time")
    private String endTime;

    @ApiModelProperty(value = "식사여부")
    @Column(name = "is_meal")
    private int isMeal;

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
        if(this.approvalStatus == null && this.staff.getRole().equals("ROLE_MANAGER")){
            this.approvalStatus = PRE;
        }else if(this.approvalStatus == null && this.staff.getRole().equals("ROLE_ADMIN") && this.staff.getStaffNum() != 4L){
            this.approvalStatus = PRE;
        }else if(this.approvalStatus == null){
            this.approvalStatus = WAITING;
        }
    }

    public void calculateSubTime(String startTime, String endTime){
        String[] startHHMM = startTime.split(":");
        double start = Double.parseDouble(startHHMM[0]) + (Double.parseDouble(startHHMM[1]) == 30.0 ? 0.5 : 0);

        String[] endHHMM = endTime.split(":");
        double end = Double.parseDouble(endHHMM[0]) + (Double.parseDouble(endHHMM[1]) == 30.0 ? 0.5 : 0);

        this.subTime = end - start;
    }

    public void setApplicant(Staff applicant){
        this.staff = applicant;
    }

    public void reject(String reason){
        this.approvalStatus = COMPANION;
        this.companionReason = reason;
    }

    public void confirm(String startTime, String endTime){
        this.startTime = startTime;
        this.endTime = endTime;
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
