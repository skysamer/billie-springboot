package com.lab.smartmobility.billie.vehicle.domain;

import com.lab.smartmobility.billie.vehicle.domain.VehicleReservation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.*;

@Getter @ToString
@AllArgsConstructor @NoArgsConstructor
@Builder @Entity
@ApiModel(value = "차량 반납 관련 이미지 엔티티")
@Table(name = "tbl_vehicle_image")
public class ImageVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "차량 반납 이미지 고유 번호")
    @Column(name = "image_id", insertable = false)
    private Long image_id;

    @ApiModelProperty(value = "이미지 이름(uuid 포함)")
    @Column(name = "origin_image_name")
    private String originImageName;

    @ApiModelProperty(value = "이미지 저장 경로")
    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "rent_num")
    private VehicleReservation vehicleReservation;

}
