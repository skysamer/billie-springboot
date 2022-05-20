package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @ToString
@AllArgsConstructor @NoArgsConstructor
@Entity @ApiModel(value = "알림 기능 엔티티") @Table(name = "tbl_notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "알림 시퀀스")
    @Column(name = "id", insertable = false)
    private Long notificationNum;

    @ApiModelProperty(value = "알림 확인여부")
    @Column(name = "read_at")
    private int readAt;

    @ApiModelProperty(value = "알림 종류(vacation, overtime)")
    @Column(name = "type")
    private String type;

    @ApiModelProperty(value = "알림 생성시간")
    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "결재 요청자 이름 (요청자가 본인일 경우 결제 응답 알림으로 처리)")
    private String requester;

    @ManyToOne
    @ApiModelProperty(value = "수신자")
    @JoinColumn(name = "staff_num")
    private Staff staff;
}
