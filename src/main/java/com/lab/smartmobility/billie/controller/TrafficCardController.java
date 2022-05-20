package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.dto.ReturnTrafficCardDTO;
import com.lab.smartmobility.billie.dto.TrafficCardApplyDTO;
import com.lab.smartmobility.billie.dto.TrafficCardForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.TrafficCard;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
import com.lab.smartmobility.billie.service.TrafficCardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"교통카드 관리 api"})
@RequestMapping("/traffic-card")
public class TrafficCardController {
    private final TrafficCardService service;
    private final Log log= LogFactory.getLog(getClass());

    @GetMapping("/card-list")
    @ApiOperation(value = "보유 교통카드 목록 조회")
    public List<TrafficCard> getPossessCardList(){
        return service.getPossessCardList();
    }

    @PostMapping("/register")
    @ApiOperation(value = "신규 교통카드 등록")
    @ApiResponses({
            @ApiResponse(code = 200, message = "등록 성공 or 등록 실패")
    })
    public HttpMessage register(@RequestBody TrafficCardForm trafficCardForm){
        if(service.registerCard(trafficCardForm)==9999){
            return new HttpMessage("fail", "등록 실패");
        }
        return new HttpMessage("success", "등록 성공");
    }

    @GetMapping("/card/{card-num}")
    @ApiOperation(value = "개별 교통카드 상세 정보")
    public TrafficCard getCardInfo(@PathVariable("card-num") Long cardNum){
        return service.getCardInfo(cardNum);
    }

    @PutMapping("/modify")
    @ApiOperation(value = "교통카드 등록 정보 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "수정 성공 or 수정 실패")
    })
    public HttpMessage modify(@RequestBody TrafficCardForm trafficCardForm){
        if(service.updateCardInfo(trafficCardForm)==9999){
            return new HttpMessage("fail", "수정 실패");
        }
        return new HttpMessage("success", "수정 성공");
    }

    @PutMapping("/discard/{card-num}")
    @ApiOperation(value = "교통카드 폐기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "폐기 성공 or 이미 폐기된 카드입니다")
    })
    public HttpMessage discard(@PathVariable("card-num") Long cardNum, @RequestBody HashMap<String, String> reason){
        if(service.discardCard(cardNum, reason)==500){
            return new HttpMessage("fail", "이미 폐기된 카드입니다");
        }
        return new HttpMessage("success", "폐기 성공");
    }

    @DeleteMapping("/{card-num}")
    @ApiOperation(value = "교통카드 정보 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 성공 or 삭제 실패")
    })
    public HttpMessage removeCardInfo(@PathVariable("card-num") Long cardNum){
        if(service.removeCardInfo(cardNum)==9999){
            return new HttpMessage("fail", "삭제 실패");
        }
        return new HttpMessage("success", "삭제 성공");
    }

    @PostMapping("/apply-rental")
    @ApiOperation(value = "교통카드 대여 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "대여신청 실패 or 이미 대여중인 카드입니다 or 이전 날짜로 대여할 수 없습니다 or 대여신청 성공")
    })
    public HttpMessage applyRental(@Valid @RequestBody TrafficCardApplyDTO trafficCardApplyDTO){
        int checkApplyRental=service.applyCardRental(trafficCardApplyDTO);

        if(checkApplyRental==9999){
            return new HttpMessage("fail", "대여신청 실패");
        }
        else if(checkApplyRental==500){
            return new HttpMessage("fail", "이미 대여중인 카드입니다");
        }else if(checkApplyRental==400){
            return new HttpMessage("fail", "이전 날짜로 대여할 수 없습니다");
        }
        return new HttpMessage("success", "대여신청 성공");
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
            @ApiResponse(code = 200, message = "이미 대여중인 카드입니다 or 이전 날짜로 대여할 수 없습니다 or 대여정보 수정 실패 or 대여정보 수정 성공")
    })
    public HttpMessage modifyCardReservationInfo(@PathVariable("reservation-num") Long reservationNum, @RequestBody TrafficCardApplyDTO trafficCardApplyDTO){
        int isUpdated=service.modifyCardReservation(reservationNum, trafficCardApplyDTO);

        if(isUpdated==500){
            return new HttpMessage("fail", "이미 대여중인 카드입니다");
        }else if(isUpdated==400){
            return new HttpMessage("fail", "이전 날짜로 대여할 수 없습니다");
        }else if(isUpdated==9999){
            return new HttpMessage("fail", "대여정보 수정 실패");
        }
        return new HttpMessage("success", "대여정보 수정 성공");
    }

    @DeleteMapping("/remove/{reservation-num}")
    @ApiOperation(value = "교통카드 대여 정보 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제할 수 없습니다. 반납 처리를 먼저 진행해주세요 or 대여 정보 삭제 완료")
    })
    public HttpMessage removeCardReservationInfo(@PathVariable("reservation-num") Long reservationNum){
        int isDeleted=service.removeCardReservationInfo(reservationNum);

        if(isDeleted==9999){
            return new HttpMessage("fail", "삭제할 수 없습니다. 반납 처리를 먼저 진행해주세요");
        }
        return new HttpMessage("success", "대여 정보 삭제 완료");
    }

    @GetMapping("/{staff-num}/{today}")
    @ApiOperation(value = "금일 나의 교통카드 예약 목록 조회")
    public List<TrafficCardReservation> getMyTodayCardReservation(@PathVariable("staff-num") Long staffNum,
                                                                  @ApiParam(value = "yyyy-MM-dd") @PathVariable("today") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate today){
        return service.getMyTodayCardReservation(today, staffNum);
    }

    @PostMapping("/apply-return")
    @ApiOperation(value = "교통카드 반납 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "반납 신청 실패 or 반납 신청 완료")
    })
    public HttpMessage applyCardReturn(@Valid @RequestBody ReturnTrafficCardDTO returnTrafficCard){
        if(service.applyCardReturn(returnTrafficCard)==9999){
            return new HttpMessage("fail", "반납 신청 실패");
        }
        return new HttpMessage("success", "반납 신청 완료");
    }

    @GetMapping("/return-list")
    @ApiOperation(value = "교통카드 반납 목록 조회")
    public List<TrafficCardReservation> getCardReturnList(@RequestParam("page") Integer page, @RequestParam("size") Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return service.getCardReturnList(pageRequest);
    }

    @GetMapping("/return/{reservation-num}")
    @ApiOperation(value = "교통카드 반납 이력 상세 조회")
    public TrafficCardReservation getCardReturnHistory(@PathVariable("reservation-num") Long reservationNum){
        return service.getCardReturn(reservationNum);
    }

    @GetMapping("/return-count")
    @ApiOperation(value = "교통카드 반납 이력 개수 조회")
    public HttpMessage getReturnCount(){
        return new HttpMessage("count", service.getReturnCount());
    }
}
