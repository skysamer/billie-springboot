package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@ApiModel(value = "개인 경비청구 신청 폼")
public class ExpenseClaimForm {
    @ApiModelProperty(value = "법인카드 신청 고유 시퀀스")
    private Long applicationId;

    @ApiModelProperty(value = "입금은행")
    private String depositBank;

    @ApiModelProperty(value = "입금 계좌번호")
    private String depositAccountNumber;

    @ApiModelProperty(value = "사용종료일")
    private LocalDate endDate;

    @ApiModelProperty(value = "사용종료 시간")
    private LocalTime endTime;

    @ApiModelProperty(value = "총 청구금액")
    private int totalAmountUsed;

    @ApiModelProperty(value = "비고")
    private String note;

    private List<ExpenseCaseForm> expenseCaseFormList;
}
