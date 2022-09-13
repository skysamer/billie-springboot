package com.lab.smartmobility.billie.controller.traffic;

import com.lab.smartmobility.billie.dto.traffic.TrafficCardApplyDTO;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
import com.lab.smartmobility.billie.service.traffic.TrafficCardReservationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"교통카드 예약 api"})
@RequestMapping("/traffic-card")
public class TrafficCardReservationController {
    private final TrafficCardReservationService service;
    private final Log log;

    @PostMapping("/apply-rental")
    @ApiOperation(value = "교통카드 대여 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "대여신청 실패 or 해당 날짜에 이미 대여중인 카드입니다 or 이전 날짜로 대여할 수 없습니다 or 대여신청 성공")
    })
    public HttpBodyMessage applyRental(@Valid @RequestBody TrafficCardApplyDTO trafficCardApplyDTO){
        return service.applyCardRental(trafficCardApplyDTO);
    }

    @GetMapping("/rental-list/{start-date}/{end-date}")
    @ApiOperation(value = "교통카드 월별 대여 목록 조회")
    public List<TrafficCardReservation> getCardRentalList(@ApiParam(value = "yyyy-MM-dd") @PathVariable("start-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                          @ApiParam(value = "yyyy-MM-dd") @PathVariable("end-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return service.getCardRentalList(startDate, endDate);
    }

    @GetMapping("/reservation/{reservation-num}")
    @ApiOperation(value = "교통카드 개별 상세 대여 정보 조회(반납 이력 상세 조회시에도 사용)")
    public TrafficCardReservation getCardRentalInfo(@PathVariable("reservation-num") Long reservationNum){
        return service.getCardRentalInfo(reservationNum);
    }

    @PutMapping("/modify/{reservation-num}")
    @ApiOperation(value = "교통카드 대여 정보 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미 대여중인 카드입니다 or 이전 날짜로 대여할 수 없습니다 or 대여자 정보가 일치하지 않습니다 or 대여시작시간 이후에는 수정할 수 없습니다 or 대여정보 수정 실패 or 대여정보 수정 성공")
    })
    public HttpBodyMessage modifyCardReservationInfo(@PathVariable("reservation-num") Long reservationNum, @RequestBody TrafficCardApplyDTO trafficCardApplyDTO){
        return service.modifyCardReservation(reservationNum, trafficCardApplyDTO);
    }

    @DeleteMapping("/remove/{reservation-num}")
    @ApiOperation(value = "교통카드 대여 정보 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제할 수 없습니다. 반납 처리를 먼저 진행해주세요 or 대여 정보 삭제 완료")
    })
    public HttpBodyMessage removeCardReservationInfo(@PathVariable("reservation-num") Long reservationNum){
        return service.removeCardReservationInfo(reservationNum);
    }

    @ApiOperation(value = "관리자의 대여 신청 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reservation-num", value = "교통카드 예약 시퀀스")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "refund-processing-is-in-progress // success-remove")
    })
    @DeleteMapping("/admin/remove/{reservation-num}")
    public HttpBodyMessage removeReservationByAdmin(@PathVariable("reservation-num") Long reservationNum){
        return service.removeReservationByAdmin(reservationNum);
    }

    @ApiOperation(value = "관리자의 대여 신청 정보 수정 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reservation-num", value = "교통카드 예약 시퀀스")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "refund-processing-is-in-progress // this-time-already-reservation // success-modify")
    })
    @PutMapping("/admin/modify/{reservation-num}")
    public HttpBodyMessage modifyReservationInfoByAdmin(@PathVariable("reservation-num") Long reservationNum, @RequestBody TrafficCardApplyDTO trafficCardApplyDTO){
        return service.modifyReservationInfoByAdmin(reservationNum, trafficCardApplyDTO);
    }

    @GetMapping("/my/{staff-num}")
    @ApiOperation(value = "금일 나의 교통카드 예약 목록 조회")
    public List<TrafficCardReservation> getMyTodayCardReservation(@PathVariable("staff-num") Long staffNum){
        return service.getMyCardReservation(staffNum);
    }
}
