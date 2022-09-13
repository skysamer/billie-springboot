package com.lab.smartmobility.billie.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
@Builder
@ApiModel(value = "추가근무 신청 폼")
public class ApplyOvertimeForm {
    @ApiModelProperty(value = "날짜")
    @Column(name = "date_of_overtime")
    private LocalDate dateOfOvertime;

    @ApiModelProperty(value = "식사여부")
    @Column(name = "whether_to_eat")
    private int whetherToEat;

    @ApiModelProperty(value = "시작시간")
    @Column(name = "started_at")
    private LocalTime startedAt;

    @ApiModelProperty(value = "종료시간")
    @Column(name = "finished_at")
    private LocalTime finishedAt;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "직원 고유번호")
    private Long staffNum;
}
