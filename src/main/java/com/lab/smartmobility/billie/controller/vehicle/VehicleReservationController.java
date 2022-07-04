package com.lab.smartmobility.billie.controller.vehicle;

import com.lab.smartmobility.billie.dto.vehicle.ApplyRentalVehicleDTO;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.lab.smartmobility.billie.service.vehicle.VehicleReservationService;
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
    public HttpMessage applyForRent(@Valid @RequestBody ApplyRentalVehicleDTO rentalVehicleDTO){
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
    public HttpMessage modify(@PathVariable("rent-num") Long rentNum, @RequestBody ApplyRentalVehicleDTO applyRentalVehicleDTO){
        int isUpdated=service.modifyVehicleReservation(rentNum, applyRentalVehicleDTO);

        if(isUpdated==400){
            return new HttpMessage("fail", "현재 시각보다 과거로 예약할 수 없습니다");
        }else if(isUpdated==500){
            return new HttpMessage("fail", "해당 날짜에 차량이 이미 대여중입니다");
        }else if(isUpdated==300){
            return new HttpMessage("fail", "대여자 정보가 일치하지 않습니다");
        }else if(isUpdated==303){
            return new HttpMessage("fail", "대여시작시간 이후에는 변경할 수 없습니다");
        }else if(isUpdated==9999){
            return new HttpMessage("fail", "예약 수정 실패");
        }
        return new HttpMessage("success", "예약 수정 성공");
    }

    @DeleteMapping("/remove/{rent-num}")
    @ApiOperation(value = "개별 차량 예약 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 실패 or 삭제 성공 or 삭제 불가")
    })
    public HttpMessage remove(@PathVariable("rent-num") Long rentNum){
        int isDeleted=service.removeReservationInfo(rentNum);

        if(isDeleted==9999){
            return new HttpMessage("fail", "삭제 실패");
        }else if(isDeleted==500){
            return new HttpMessage("fail", "삭제 불가");
        }
        return new HttpMessage("success", "삭제 성공");
    }

    @ApiOperation(value = "관리자의 차량 예약 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rent-num", value = "차량 예약 시퀀스")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "success-remove // refund-processing-is-in-progress")
    })
    @DeleteMapping("/admin/remove/{rent-num}")
    public HttpMessage removeRentByAdmin(@PathVariable("rent-num") Long rentNum){
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
    public HttpMessage modifyRentInfoByAdmin(@PathVariable("rent-num") Long rentNum, @RequestBody ApplyRentalVehicleDTO applyRentalVehicleDTO){
        return service.modifyRentInfoByAdmin(rentNum, applyRentalVehicleDTO);
    }

    @GetMapping("/my/{staff-num}")
    @ApiOperation(value = "나의 차량 예약 현황 조회", notes = "대여이력이 없을경우, 공백의 list 반환")
    public List<VehicleReservation> getMyReservationList(@PathVariable("staff-num") Long staffNum){
        return service.getMyReservation(staffNum);
    }
}
