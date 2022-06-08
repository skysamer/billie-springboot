package com.lab.smartmobility.billie.dto.corporation;

import com.lab.smartmobility.billie.entity.corporation.CorporationCard;
import com.lab.smartmobility.billie.entity.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ApiModel(value = "나의 법인카드 사용신청 목록 조회를 위한 데이터 객체")
public class MyCardApplicationDTO {
    @ApiModelProperty(value = "법인카드 사용 신청 고유 시퀀스")
    private Long applicationId;

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

    @ApiModelProperty(value = "승인상태(w:대기, t:팀장승인, f:승인완료)")
    private char approvalStatus;

    @ApiModelProperty(value = "반려사유")
    private String reasonForRejection;

    @ApiModelProperty(value = "부여된 법인카드(미승인 혹은 경비청구일 경우 null)")
    private CorporationCard corporationCard;

    @ApiModelProperty(value = "사용신청 직원 정보")
    private Staff staff;

    @ApiModelProperty(name = "개인경비청구여부")
    private int isClaimedExpense;
}
