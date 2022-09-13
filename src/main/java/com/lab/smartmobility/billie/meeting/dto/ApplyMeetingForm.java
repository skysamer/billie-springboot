package com.lab.smartmobility.billie.meeting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
@ApiModel(value = "회의실 예약 폼")
public class ApplyMeetingForm {
    @ApiModelProperty(value = "회의실 예약 제목")
    private String title;

    @ApiModelProperty(value = "예약날짜")
    private LocalDate date;

    @ApiModelProperty(value = "회의 시작 시간")
    @Column(name = "starttime")
    private LocalTime startTime;

    @ApiModelProperty(value = "회의 종료 시간")
    @Column(name = "endtime")
    private LocalTime endTime;

    @ApiModelProperty(value = "참여부서")
    private String department;

    @ApiModelProperty(value = "참여자")
    private String participants;

    @ApiModelProperty(value = "대여직원의 고유번호")
    private Long staffNum;
}
