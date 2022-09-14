package com.lab.smartmobility.billie.corporation.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity @Table(name = "tbl_expense_case")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
@ApiModel(value = "경비청구 건 엔티티")
public class ExpenseCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "청구건 시퀀스")
    @Column(name = "case_id")
    private Long caseId;

    @ApiModelProperty(value = "사용날짜")
    @Column(name = "used_at")
    private LocalDate usedAt;

    @ApiModelProperty(value = "사용목적")
    private String purpose;

    @ApiModelProperty(value = "사용금액")
    private int amount;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    private ExpenseClaim expenseClaim;
}
