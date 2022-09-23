package com.lab.smartmobility.billie.vacation.domain;

import com.lab.smartmobility.billie.staff.domain.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Getter @Builder
@Entity @Table(name = "tbl_vacation_report")
@ApiModel(value = "휴가 월별 리포트 엔티티")
public class VacationReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "데이터 순번")
    @Column(name = "report_id")
    private Long id;

    @ApiModelProperty(value = "사용개수")
    private double count;

    @ApiModelProperty(value = "사용날짜")
    @Column(name = "start_date")
    private LocalDate startDate;

    @ApiModelProperty(value = "사용날짜")
    @Column(name = "end_date")
    private LocalDate endDate;

    @ApiModelProperty(value = "휴가종류")
    private String note;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "staff_num")
    private Staff staff;
}
