package com.lab.smartmobility.billie.vacation.controller;

import com.lab.smartmobility.billie.vacation.dto.VacationReportForm;
import com.lab.smartmobility.billie.vacation.service.VacationReportService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = {"월별 휴가 리포트 api"})
@RestController
@RequestMapping("/vacation")
@RequiredArgsConstructor
public class VacationReportController {
    private final VacationReportService service;

    @ApiOperation(value = "월별 휴가 리포트 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "연월 (yyyy-MM)"),
            @ApiImplicitParam(name = "department", value = "부서명 (전체는 all)"),
            @ApiImplicitParam(name = "name", value = "검색어 (직원이름, 전체는 all)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 204, message = "결과값없음"),
    })
    @GetMapping("/report/admin/{base-date}/{department}/{name}")
    public ResponseEntity<List<VacationReportForm>> getCalendarList(@PathVariable("base-date") String baseDate,
                                                                    @PathVariable String department,
                                                                    @PathVariable String name){
        List<VacationReportForm> reportList = service.getReport(baseDate, department, name);
        if(reportList.size() == 0){
            return new ResponseEntity<>(reportList, NO_CONTENT);
        }
        return new ResponseEntity<>(reportList, OK);
    }
}
