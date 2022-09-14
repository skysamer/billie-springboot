package com.lab.smartmobility.billie.corporation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;

@Entity @Table(name = "tbl_expense_claim")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
@ApiModel(value = "경비청구 엔티티")
public class ExpenseClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "경비청구 시퀀스")
    @Column(name = "expense_id")
    private Long expenseId;

    @ApiModelProperty(value = "입금은행")
    @Column(name = "deposit_bank")
    private String depositBank;

    @ApiModelProperty(value = "입금 계좌번호")
    @Column(name = "deposit_account_number")
    private String depositAccountNumber;

    @ApiModelProperty(value = "총 사용금액")
    @Column(name = "total_amount_used")
    private int totalAmountUsed;

    @ApiModelProperty(value = "비고")
    private String note;

    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;
}
