package com.lab.smartmobility.billie.entity.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;

@Entity @Table(name = "tbl_corporation_card_return")
@Getter @Setter
@ApiModel(value = "법인카드 반납 엔티티")
@AllArgsConstructor @NoArgsConstructor @Builder
public class CorporationCardReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    @ApiModelProperty(value = "법인카드 반납 시퀀스")
    private Long returnId;

    @ApiModelProperty(value = "총 사용금액")
    @Column(name = "total_amount_used")
    private int totalAmountUsed;

    @ApiModelProperty(value = "비고")
    private String note;

    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;
}
