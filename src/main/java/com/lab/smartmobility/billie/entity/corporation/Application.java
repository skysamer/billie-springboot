package com.lab.smartmobility.billie.entity.corporation;

import com.lab.smartmobility.billie.entity.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity @Table(name = "tbl_application")
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "법인카드 및 경비 신청 엔티티", description = "staff: 신청직원정보, 법인카드 미배정시 null")
@DynamicInsert @DynamicUpdate
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "법인카드 사용 신청 고유 시퀀스")
    @Column(name = "application_id")
    private Long applicationId;

    @ApiModelProperty(value = "시작일 (yyyy-MM-dd)")
    @Column(name = "start_date")
    private LocalDate startDate;

    @ApiModelProperty(value = "시작시간 (hh:mm)")
    @Column(name = "start_time")
    private LocalTime startTime;

    @ApiModelProperty(value = "종료일 (yyyy-MM-dd)")
    @Column(name = "end_date")
    private LocalDate endDate;

    @ApiModelProperty(value = "종료시간 (hh:mm)")
    @Column(name = "end_time")
    private LocalTime endTime;

    @ApiModelProperty(value = "내용(용도)")
    private String content;

    @ApiModelProperty(value = "승인상태(w:대기, t:팀장승인, f:승인완료, c:반려)")
    @Column(name = "approval_status")
    private char approvalStatus = 'w';

    @ApiModelProperty(value = "반려사유")
    @Column(name = "reason_for_rejection")
    private String reasonForRejection;

    @ApiModelProperty(value = "개인경비청구여부 (99: 후불경비청구)")
    @Column(name = "is_claimed_expense")
    private int isClaimedExpense;

    @ApiModelProperty(value = "반납여부 (혹은 경비청구 여부)")
    @Column(name = "is_returned")
    private int isReturned;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private CorporationCard corporationCard;

    @ManyToOne
    @JoinColumn(name = "staff_num")
    private Staff staff;

    public void insertRequesterAndPostExpense(Staff staff, int isClaimedExpense){
        this.staff = staff;
        this.isClaimedExpense = isClaimedExpense;
    }

    public void updateApprovalStatus(char approvalStatus){
        this.approvalStatus = approvalStatus;
    }

    public void assignRequester(Staff requester){
        this.staff = requester;
    }

    public void approveExpenseByAdmin(int isClaimedExpense, char approvalStatus){
        this.isClaimedExpense = isClaimedExpense;
        this.approvalStatus = approvalStatus;
    }

    public void approveCorporationByAdmin(CorporationCard card, char approvalStatus){
        this.corporationCard = card;
        this.approvalStatus = approvalStatus;
    }

    public void approveByManager(char approvalStatus){
        this.approvalStatus = approvalStatus;
    }

    public void reject(char approvalStatus, String reasonForRejection){
        this.approvalStatus = approvalStatus;
        this.reasonForRejection = reasonForRejection;
    }

    public void returnUpdate(LocalDate endDate, LocalTime endTime, int isReturned){
        this.endDate = endDate;
        this.endTime = endTime;
        this.isReturned = isReturned;
    }
}
