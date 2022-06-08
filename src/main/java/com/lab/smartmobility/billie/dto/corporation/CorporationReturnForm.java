package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@ApiModel(value = "법인카드 반납 폼")
public class CorporationReturnForm {
    @ApiModelProperty(value = "법인카드 사용 신청 고유 시퀀스")
    private Long applicationId;

    @ApiModelProperty(value = "사용종료일 (yyyy-MM-dd)")
    private LocalDate endDate;

    @ApiModelProperty(value = "사용종료시간 (hh:mm)")
    private LocalTime endTime;

    @ApiModelProperty(value = "총 사용금액")
    private int totalAmountUsed;

    @ApiModelProperty(value = "비고")
    private String note;

    private List<CorporationUseCaseForm> useCaseFormList;
}
