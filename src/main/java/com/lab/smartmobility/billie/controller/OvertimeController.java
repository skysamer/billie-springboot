package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.dto.ApplyOvertimeForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Overtime;
import com.lab.smartmobility.billie.service.OvertimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/overtime")
@RequiredArgsConstructor
@Api(tags = {"추가근무 관리 api"})
public class OvertimeController {
    private final OvertimeService service;

    @GetMapping("/count/{staff-num}")
    @ApiOperation(value = "나의 이번달 추가근무 시간")
    public HttpMessage getMyOvertimeCount(@PathVariable("staff-num") Long staffNum){
        return new HttpMessage("success", service.getMyOvertimeCount(staffNum));
    }

    @PostMapping("/register")
    @ApiOperation(value = "추가근무 신청")
    public HttpMessage registerOvertime(@RequestBody ApplyOvertimeForm applyOvertimeForm){
        if(service.applyOvertime(applyOvertimeForm)==9999){
            return new HttpMessage("fail", "추가근무 신청 실패");
        }
        return new HttpMessage("success", "추가근무 신청 성공");
    }

    @GetMapping("/apply-list/{staff-num}/{start-date}/{end-date}/{approval-status}")
    @ApiOperation(value = "나의 추가근무 신청 목록 조회", notes = "모든 결제상태 조회 시 approval-status : 'A' 전달")
    public List<Overtime> getMyApplyList(@PathVariable("staff-num") Long staffNum,
                                         @ApiParam(value = "yyyy-MM-dd") @PathVariable("start-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                         @ApiParam(value = "yyyy-MM-dd") @PathVariable("end-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                         @PathVariable("approval-status") char approvalStatus,
                                         @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        return service.getMyOvertimeList(staffNum, startDate, endDate, approvalStatus, PageRequest.of(page, size));
    }

    @GetMapping("/my/{overtime-num}")
    @ApiOperation(value = "나의 추가근무 신청 상세조회")
    public Overtime getMyApply(@PathVariable("overtime-num") Long overtimeNum){
        return service.getMyOvertime(overtimeNum);
    }

    @PutMapping("/my/{overtime-num}")
    @ApiOperation(value = "나의 추가근무 신청 내역 수정")
    public HttpMessage modifyMyApply(@PathVariable("overtime-num") Long overtimeNum,
                                     @RequestBody ApplyOvertimeForm applyOvertimeForm){
        return new HttpMessage();
    }
}
