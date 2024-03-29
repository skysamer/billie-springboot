package com.lab.smartmobility.billie.vehicle.controller;

import com.lab.smartmobility.billie.vehicle.dto.ApplyRentalVehicleDTO;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.vehicle.domain.VehicleReservation;
import com.lab.smartmobility.billie.vehicle.service.VehicleReservationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Api(tags = {"차량 예약 api"})
@RequestMapping("/vehicle")
@RestController
public class VehicleReservationController {
    private final VehicleReservationService service;

    @PostMapping("/apply-rental")
    @ApiOperation(value = "차량 이용 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "차량 대여 신청 실패 or 해당 날짜에 차량이 이미 대여중입니다 or 현재 시각보다 과거로 예약할 수 없습니다 or 대여 성공")
    })
    public HttpBodyMessage applyForRent(@Valid @RequestBody ApplyRentalVehicleDTO rentalVehicleDTO){
        return service.applyForRent(rentalVehicleDTO);
    }

    @GetMapping("/reservation-list/{startDate}/{endDate}")
    @ApiOperation(value = "월 단위 차량 예약 목록 조회")
    public List<VehicleReservation> reservationList(@ApiParam(value = "yyyy-MM-dd") @PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                    @ApiParam(value = "yyyy-MM-dd") @PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return service.reservationList(startDate, endDate);
    }

    @GetMapping("/reservation/{rent-num}")
    @ApiOperation(value = "개별 차량 예약 상세조회")
    public VehicleReservation getReservate(@PathVariable("rent-num") Long rentNum){
        return service.getReservation(rentNum);
    }

    @PutMapping("/modify/{rent-num}")
    @ApiOperation(value = "개별 차량 예약 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "현재 시각보다 과거로 예약할 수 없습니다 " +
                    "or 해당 날짜에 차량이 이미 대여중입니다 " +
                    "or 대여자 정보가 일치하지 않습니다 " +
                    "or 대여시작시간 이후에는 변경할 수 없습니다 " +
                    "or 예약 수정 실패 or 예약 수정 성공")
    })
    public HttpBodyMessage modify(@PathVariable("rent-num") Long rentNum, @RequestBody ApplyRentalVehicleDTO applyRentalVehicleDTO){
        return service.modifyVehicleReservation(rentNum, applyRentalVehicleDTO);
    }

    @DeleteMapping("/remove/{rent-num}")
    @ApiOperation(value = "개별 차량 예약 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 실패 or 삭제 성공 or 삭제 불가")
    })
    public HttpBodyMessage remove(@PathVariable("rent-num") Long rentNum){
        return service.removeReservationInfo(rentNum);
    }

    @ApiOperation(value = "관리자의 차량 예약 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rent-num", value = "차량 예약 시퀀스")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "success-remove // refund-processing-is-in-progress")
    })
    @DeleteMapping("/admin/remove/{rent-num}")
    public HttpBodyMessage removeRentByAdmin(@PathVariable("rent-num") Long rentNum){
        return service.removeReservationByAdmin(rentNum);
    }

    @ApiOperation(value = "관리자의 차량예약 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rent-num", value = "차량 예약 시퀀스")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "success-remove // refund-processing-is-in-progress // already-reservation")
    })
    @PutMapping("/admin/modify/{rent-num}")
    public HttpBodyMessage modifyRentInfoByAdmin(@PathVariable("rent-num") Long rentNum, @RequestBody ApplyRentalVehicleDTO applyRentalVehicleDTO){
        return service.modifyRentInfoByAdmin(rentNum, applyRentalVehicleDTO);
    }

    @GetMapping("/my/{staff-num}")
    @ApiOperation(value = "나의 차량 예약 현황 조회", notes = "대여이력이 없을경우, 공백의 list 반환")
    public List<VehicleReservation> getMyReservationList(@PathVariable("staff-num") Long staffNum){
        return service.getMyReservation(staffNum);
    }
}
