package com.lab.smartmobility.billie.entity.corporation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter
@ApiModel(value = "법인카드 사용건 엔티티")
@Table(name = "tbl_corporation_card_use_case")
@AllArgsConstructor @NoArgsConstructor @Builder
public class CorporationCardUseCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "사용건 시퀀스")
    @Column(name = "case_id")
    private Long caseId;

    @ApiModelProperty(value = "사용날짜")
    @Column(name = "used_at")
    private LocalDate usedAt;

    @ApiModelProperty(value = "사용목적")
    private String purpose;

    @ApiModelProperty(value = "사용금액")
    private int amount;

    @ApiModelProperty(value = "참여자명")
    private String participants;

    @ManyToOne
    @JoinColumn(name = "return_id")
    private CorporationCardReturn corporationCardReturn;
}
