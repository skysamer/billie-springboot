package com.lab.smartmobility.billie.vacation.controller;

import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.vacation.dto.VacationApproveListForm;
import com.lab.smartmobility.billie.vacation.service.VacationApproveService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"휴가 승인 api"})
@RestController
@RequestMapping("/vacation")
@RequiredArgsConstructor
public class VacationApproveController {
    private final VacationApproveService service;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "부서장의 휴가승인 요청 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "연월(yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "keyword", value = "검색어(직원이름, 전체는 all)"),
            @ApiImplicitParam(name = "is-toggle-on", value = "반려, 취소데이터 포함 여부(0: 미포함, 1: 포함)"),
            @ApiImplicitParam(name = "page", value = "페이지 번호"),
            @ApiImplicitParam(name = "size", value = "페이지 당 데이터 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않은 경우"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음")
    })
    @GetMapping("/approve/manager/{base-date}/{keyword}/{is-toggle-on}/{page}/{size}")
    public ResponseEntity<PageResult<VacationApproveListForm>> getMyRecentApplication(@RequestHeader("X-AUTH-TOKEN") String token,
                                                                                      @PathVariable("base-date") String baseDate,
                                                                                      @PathVariable String keyword,
                                                                                      @PathVariable("is-toggle-on") int isToggleOn,
                                                                                      @PathVariable Integer page,
                                                                                      @PathVariable Integer size){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);
        PageResult<VacationApproveListForm> vacationApproveList = service.getApproveListByManager(baseDate, email, keyword, isToggleOn, PageRequest.of(page, size));

        if(vacationApproveList.getCount() == 0){
            return new ResponseEntity<>(vacationApproveList, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(vacationApproveList, HttpStatus.OK);
    }
}
