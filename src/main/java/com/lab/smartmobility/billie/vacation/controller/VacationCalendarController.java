package com.lab.smartmobility.billie.vacation.controller;

import com.lab.smartmobility.billie.vacation.dto.VacationCalendarForm;
import com.lab.smartmobility.billie.vacation.service.VacationCalendarService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = {"휴가 캘린더 api"})
@RestController
@RequestMapping("/vacation")
@RequiredArgsConstructor
public class VacationCalendarController {
    private final VacationCalendarService service;

    @ApiOperation(value = "승인된 휴가 내역 월별 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start-date", value = "시작일 (yyyy-MM-dd)"),
            @ApiImplicitParam(name = "end-date", value = "종료일 (yyyy-MM-dd)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 204, message = "결과값없음"),
    })
    @GetMapping("/calendar/user/{start-date}/{end-date}")
    public ResponseEntity<List<VacationCalendarForm>> getCalendarList(@DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("start-date") LocalDate startDate,
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("end-date") LocalDate endDate){
        List<VacationCalendarForm> result = service.getCalendarList(startDate, endDate);
        if(result.size() == 0){
            return new ResponseEntity<>(result, NO_CONTENT);
        }
        return new ResponseEntity<>(result, OK);
    }
}
