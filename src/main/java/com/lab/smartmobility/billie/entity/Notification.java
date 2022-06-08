package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @ToString
@AllArgsConstructor @NoArgsConstructor
@Entity @ApiModel(value = "알림 기능 엔티티") @Table(name = "tbl_notification")
@DynamicInsert @DynamicUpdate
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "알림 시퀀스")
    @Column(name = "id", insertable = false)
    private Long notificationNum;

    @ApiModelProperty(value = "알림 확인여부")
    @Column(name = "read_at")
    private int readAt;

    @ApiModelProperty(value = "알림 종류(vacation, overtime, corporation)")
    @Column(name = "type")
    private String type;

    @ApiModelProperty(name = "승인상태(w:대기, t:팀장승인, f:승인완료)")
    @Column(name = "approval_status")
    private char approveStatus;

    @ApiModelProperty(value = "알림 생성시간")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "요청자")
    private String requester;

    @ApiModelProperty(value = "수신자(수신자와 요청자가 같을 경우 승인 요청에 대한 응답 알림임)")
    private String receiver;
}
