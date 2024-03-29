package com.lab.smartmobility.billie.vacation.controller;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.vacation.dto.MyRecentVacationForm;
import com.lab.smartmobility.billie.vacation.dto.VacationApplicationDetailsForm;
import com.lab.smartmobility.billie.vacation.dto.VacationApplicationForm;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.vacation.service.VacationApplicationService;
import com.lab.smartmobility.billie.vacation.dto.VacationApplicationListForm;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Timer;

@Api(tags = {"휴가 신청 api"})
@RequiredArgsConstructor
@RequestMapping("/vacation/*")
@RestController
public class VacationApplicationController {
    private final VacationApplicationService service;
    private final Log log;

    @ApiOperation(value = "휴가 신청")
    @ApiResponses({
            @ApiResponse(code = 201, message = "휴가 신청 성공"),
            @ApiResponse(code = 400, message = "이전 날짜로 신청할 수 없습니다 // 휴가 개수를 모두 소진했습니다")
    })
    @PostMapping("/user/apply")
    public ResponseEntity<HttpBodyMessage> apply(@RequestBody VacationApplicationForm vacationApplicationForm){
        HttpBodyMessage httpMessage = service.apply(vacationApplicationForm);
        if(httpMessage.getCode().equals("fail")){
            return new ResponseEntity<>(httpMessage, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(httpMessage, HttpStatus.CREATED);
    }

    @ApiOperation(value = "나의 휴가 신청 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "대여자 직원 고유 번호"),
            @ApiImplicitParam(name = "base-date", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "vacation-type", value = "휴가종류 (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 404, message = "조건에 맞는 데이터 없음")
    })
    @GetMapping("/application/user/{staff-num}/{base-date}/{vacation-type}/{page}/{size}")
    public ResponseEntity<PageResult<VacationApplicationListForm>> getApplicationList(@PathVariable("staff-num") Long staffNum,
                                                                                      @PathVariable("base-date") String baseDate,
                                                                                      @PathVariable("vacation-type") String vacationType,
                                                                                      @PathVariable("page") Integer page,
                                                                                      @PathVariable("size") Integer size){
        PageResult<VacationApplicationListForm> pageResult = service.getApplicationList(staffNum, baseDate, vacationType, PageRequest.of(page, size));
        if(pageResult.getCount() == 0){
            return new ResponseEntity<>(pageResult, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pageResult, HttpStatus.OK);
    }

    @ApiOperation(value = "휴가 신청 내역 상세 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vacation-id", value = "각 휴가 데이터의 고유 시퀀스")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 404, message = "조건에 맞는 데이터 없음")
    })
    @GetMapping("/application/user/{vacation-id}")
    public ResponseEntity<VacationApplicationDetailsForm> getMyApplication(@PathVariable("vacation-id") Long vacationId){
        VacationApplicationDetailsForm vacation = service.getMyApplication(vacationId);
        if(vacation == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vacation, HttpStatus.OK);
    }

    @ApiOperation(value = "나의 최근 휴가 신청 내역 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 404, message = "조건에 맞는 데이터 없음")
    })
    @GetMapping("/application/user/recent/{staff-num}")
    public ResponseEntity<List<MyRecentVacationForm>> getMyRecentApplication(@PathVariable("staff-num") Long staffNum){
        List<MyRecentVacationForm> myRecentVacationList = service.getMyRecentApplication(staffNum);
        if(myRecentVacationList.size() == 0){
            return new ResponseEntity<>(myRecentVacationList, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(myRecentVacationList, HttpStatus.OK);
    }

    @ApiOperation(value = "휴가 신청 내역 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "휴가 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "휴가 삭제 완료 // 승인된 휴가에 대한 취소 처리 완료"),
    })
    @DeleteMapping("application/user/{id}")
    public ResponseEntity<HttpBodyMessage> remove(@PathVariable Long id){
        HttpBodyMessage result = service.cancel(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
