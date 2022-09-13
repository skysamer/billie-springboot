package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "법인카드 반납 이력 조회 폼")
public class CorporationHistoryForm {
    @ApiModelProperty(value = "법인카드 반납 고유 시퀀스")
    private Long returnId;

    @ApiModelProperty(value = "사용자")
    private String name;

    @ApiModelProperty(value = "부서")
    private String department;

    @ApiModelProperty(value = "시작일 (yyyy-MM-dd)")
    private LocalDate startDate;

    @ApiModelProperty(value = "카드명(뒷자리4개)")
    private String cardName;

    @ApiModelProperty(value = "시작일 (yyyy-MM-dd)")
    private String cardNumber;

    @ApiModelProperty(value = "시작일 (yyyy-MM-dd)")
    private String company;

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

    @ApiModelProperty(value = "총 사용금액")
    private int totalAmountUsed;

    @ApiModelProperty(value = "비고")
    private String note;

    @ApiModelProperty(value = "대여상태", notes = "폐기상태는 99")
    private int rentalStatus;

    private List<CorporationUseCaseForm> cardUseCases = new ArrayList<>();

    public void addCardUseCases(List<CorporationUseCaseForm> cardUseCase){
        this.cardUseCases.addAll(cardUseCase);
    }

    public void addCardUseCase(CorporationUseCaseForm cardUseCase){
        this.cardUseCases.add(cardUseCase);
    }
}
