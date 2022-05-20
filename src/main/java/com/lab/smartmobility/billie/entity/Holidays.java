package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;

@Entity @ApiModel(value = "휴가-알림 연결 엔티티") @Table(name = "holidays")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Holidays {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holidays_id", insertable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vacation_num")
    private Vacation vacation;

    @ManyToOne
    @JoinColumn(name = "id")
    private Notification notification;
}
