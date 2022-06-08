package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "법인카드 반납 이력과 경비청구 이력을 한번에 보기 위한 폼")
public class CorporationReturnAndExpenseForm {
    private List<CorporationHistoryForm> corporationHistoryFormList=new ArrayList<>();
    private List<ExpenseClaimHistoryForm> expenseClaimHistoryFormList=new ArrayList<>();
}
