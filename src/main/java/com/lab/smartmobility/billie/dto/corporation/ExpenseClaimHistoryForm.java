package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "개인경비 청구 이력 조회 폼")
public class ExpenseClaimHistoryForm {
    @ApiModelProperty(value = "경비청구 고유 시퀀스")
    private Long expenseId;

    @ApiModelProperty(value = "사용자")
    private String name;

    @ApiModelProperty(value = "시작일 (yyyy-MM-dd)")
    private LocalDate startDate;

    @ApiModelProperty(value = "시작시간 (hh:mm)")
    private LocalTime startTime;

    @ApiModelProperty(value = "종료일 (yyyy-MM-dd)")
    private LocalDate endDate;

    @ApiModelProperty(value = "종료시간 (hh:mm)")
    private LocalTime endTime;

    @ApiModelProperty(value = "내용(용도)")
    private String content;

    @ApiModelProperty(value = "개인경비청구여부")
    private int isClaimedExpense;

    @ApiModelProperty(value = "입금은행")
    private String depositBank;

    @ApiModelProperty(value = "입금 계좌번호")
    private String depositAccountNumber;

    @ApiModelProperty(value = "총 사용금액")
    private int totalAmountUsed;

    @ApiModelProperty(value = "비고")
    private String note;

    private List<ExpenseCaseForm> expenseCaseList=new ArrayList<>();

    public void addExpenseCase(List<ExpenseCaseForm> expenseCaseList){
        this.expenseCaseList.addAll(expenseCaseList);
    }
}
