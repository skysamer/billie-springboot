package com.lab.smartmobility.billie.controller.traffic;

import com.lab.smartmobility.billie.dto.traffic.NonBorrowableTrafficCard;
import com.lab.smartmobility.billie.dto.traffic.TrafficCardForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.TrafficCard;
import com.lab.smartmobility.billie.service.traffic.TrafficCardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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


    @ApiOperation(value = "해당 날짜에 대여가 불가능한 카드 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vacation-id", value = "각 휴가 데이터의 고유 시퀀스")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 404, message = "모든 카드 대여 가능")
    })
    @GetMapping("/user/not-borrow/{rented-at}/{returned-at}")
    public ResponseEntity<List<NonBorrowableTrafficCard>> getNonBorrowableCardList(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @PathVariable("rented-at") LocalDateTime rentedAt,
                                                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @PathVariable("returned-at") LocalDateTime returnedAt){
        List<NonBorrowableTrafficCard> nonBorrowableTrafficCardList = service.getNonBorrowableCardList(rentedAt, returnedAt);
        if(nonBorrowableTrafficCardList.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(nonBorrowableTrafficCardList, HttpStatus.OK);
    }
}
