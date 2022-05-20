package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ApiModel(value = "기자재 정보 엔티티")
@Table(name = "tbl_equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "기자재 고유 번호")
    @Column(name = "equipment_num", insertable = false)
    private Long equipmentNum;

    @ApiModelProperty(value = "대분류")
    @Column(name = "main_category")
    private String mainCategory;

    @ApiModelProperty(value = "중분류")
    @Column(name = "middle_class")
    private String middleClass;

    @ApiModelProperty(value = "기자재 이름")
    private String name;

    @ApiModelProperty(value = "제조사")
    private String manufacturer;

    @ApiModelProperty(value = "수량")
    private int quantity;

    @ApiModelProperty(value = "사양")
    private String specification;

    @ApiModelProperty(value = "관리 부서")
    private String department;

    @ApiModelProperty(value = "비고")
    private String note;

    @ApiModelProperty(value = "대여여부")
    private String rent;

    @ApiModelProperty(value = "단가")
    @Column(name = "unit_price")
    private int unitPrice;

    @ApiModelProperty(value = "구매정보")
    @Column(name = "purchase_info")
    private String purchaseInfo;

    @ApiModelProperty(value = "대여일자")
    @Column(name = "day_of_rental")
    private LocalDate dayOfRental;

    @ApiModelProperty(value = "반납일자")
    @Column(name = "day_of_return")
    private LocalDate dayOfReturn;

}
