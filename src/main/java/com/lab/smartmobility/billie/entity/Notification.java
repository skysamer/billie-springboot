package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@Entity @ApiModel(value = "알림 기능 엔티티") @Table(name = "tbl_notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "알림 번호")
    @Column(name = "id", insertable = false)
    private Long id;

    @ApiModelProperty(value = "알림 종류(vacation, overtime, corporation)")
    @Column(name = "type")
    private String type;

    @ApiModelProperty(value = "알림 생성시간")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "요청알림인지 (요청이 아닐 경우, 요청에 대한 응답 업데이트 알림)")
    @Column(name = "is_request")
    private int isRequest;  // 부서장 or 관리자가 1인 경우가 대부분임

    @ApiModelProperty(value = "수신자")
    private String receiver;

    @PrePersist
    public void setCurrentTime(){
        this.createdAt = LocalDateTime.now();
    }
}
