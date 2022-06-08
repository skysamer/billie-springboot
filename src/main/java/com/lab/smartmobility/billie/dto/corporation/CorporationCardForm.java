package com.lab.smartmobility.billie.dto.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
@ApiModel(value = "신규 법인카드 등록 폼")
public class CorporationCardForm {
    @ApiModelProperty(value = "카드명")
    private String cardName;

    @ApiModelProperty(value = "카드사")
    private String company;

    @ApiModelProperty(value = "구매일 (yyyy-MM-dd)")
    private LocalDate purchasedAt;

    @ApiModelProperty(value = "16자리 카드번호 (0000-0000-0000-0000)")
    private String cardNumber;

    @ApiModelProperty(value = "유효기간 (MM/yy)")
    private String validity;
}
