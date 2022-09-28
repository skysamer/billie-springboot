package com.lab.smartmobility.billie.overtime.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.overtime.dto.OvertimeDetailsForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeHourDTO;
import com.lab.smartmobility.billie.overtime.dto.OvertimeMonthlyForm;
import com.lab.smartmobility.billie.overtime.service.OvertimeHomeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Api(tags = {"추가근무 홈 api"})
@RestController
@RequestMapping("/overtime")
@RequiredArgsConstructor
public class OvertimeHomeController {
    private final OvertimeHomeService service;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "나의 추가근무 개수 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않은 경우")
    })
    @GetMapping("/home/user")
    public ResponseEntity<OvertimeHourDTO> getMyOvertimeHour(@RequestHeader("X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        OvertimeHourDTO overtimeHourDTO = service.getMyOvertimeHour(email);
        return new ResponseEntity<>(overtimeHourDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "나의 추가근무 개수 반환")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "추가근무 고유 시퀀스")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공")
    })
    @GetMapping("/home/user/{id}")
    public ResponseEntity<OvertimeDetailsForm> getOvertimeMonthly(@PathVariable Long id){
        OvertimeDetailsForm detailsForm = service.getOvertime(id);
        return new ResponseEntity<>(detailsForm, HttpStatus.OK);
    }

    @ApiOperation(value = "월별 추가근무 내역 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start-date", value = "월의 시작일 (yyyy-MM-dd)"),
            @ApiImplicitParam(name = "end-date", value = "월의 종료일 (yyyy-MM-dd)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공")
    })
    @GetMapping("/home/user/{start-date}/{end-date}")
    public ResponseEntity<List<OvertimeMonthlyForm>> getOvertime(@DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("start-date") LocalDate startDate,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("end-date") LocalDate endDate){
        List<OvertimeMonthlyForm> detailsForm = service.getOvertimeMonthly(startDate, endDate);
        return new ResponseEntity<>(detailsForm, HttpStatus.OK);
    }
}
