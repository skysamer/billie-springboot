package com.lab.smartmobility.billie.overtime.controller;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApproveListForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeReportForm;
import com.lab.smartmobility.billie.overtime.service.OvertimeReportService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"추가근무 리포트 api"})
@RestController
@RequestMapping("/overtime")
@RequiredArgsConstructor
public class OvertimeReportController {
    private final OvertimeReportService service;

    @ApiOperation(value = "나의 추가근무 신청 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "기준연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "department", value = "부서명 (전체는 all)"),
            @ApiImplicitParam(name = "name", value = "검색어 (직원이름, 전체는 all)"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음"),
    })
    @GetMapping("/approve/manager/{base-date}/{name}/{page}/{size}")
    public ResponseEntity<List<OvertimeReportForm>> getApproveListByManager(@PathVariable("base-date") String baseDate,
                                                                            @PathVariable String department,
                                                                            @PathVariable String name){
        List<OvertimeReportForm> result = service.getReport(baseDate, department, name);

        if(result.size() == 0){
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
