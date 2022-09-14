package com.lab.smartmobility.billie.entity;
import com.lab.smartmobility.billie.staff.domain.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @ToString
@Builder @NoArgsConstructor
@AllArgsConstructor
@Entity @ApiModel(value = "회의실 예약 정보 엔티티", description = "매핑된 직원정보가 대여자 정보")
@Table(name = "tbl_meeting")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "예약 고유 번호")
    @Column(name = "meeting_num", insertable = false)
    private Long meetingNum;

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

    @ManyToOne
    @JoinColumn(name = "staff_num")
    @ApiModelProperty(value = "대여자 정보")
    private Staff staff;

    public void insertRenderInfo(Staff staff){
        this.staff = staff;
    }
}
