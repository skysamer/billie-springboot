package com.lab.smartmobility.billie.controller.vacation;

import com.lab.smartmobility.billie.dto.PageResult;
import com.lab.smartmobility.billie.dto.vacation.VacationApplicationForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Vacation;
import com.lab.smartmobility.billie.service.vacation.VacationApplicationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"휴가 신청 api"})
@RequiredArgsConstructor
@RequestMapping("/vacation/*")
@RestController
public class VacationApplicationController {
    private final VacationApplicationService service;
    private final Log log;

    @ApiOperation(value = "휴가 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이전 날짜로 신청할 수 없습니다 // 휴가 신청 성공")
    })
    @PostMapping("/user/apply")
    public HttpMessage apply(@RequestBody VacationApplicationForm vacationApplicationForm){
        return service.apply(vacationApplicationForm);
    }

    @ApiOperation(value = "나의 휴가 신청 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "대여자 직원 고유 번호"),
            @ApiImplicitParam(name = "base-date", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "vacation-type", value = "휴가종류 (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수")
    })
    @GetMapping("/user/application/{staff-num}/{base-date}/{vacation-type}/{page}/{size}")
    public PageResult<Vacation> getApplicationList(@PathVariable("staff-num") Long staffNum,
                                                   @PathVariable("base-date") String baseDate,
                                                   @PathVariable("vacation-type") String vacationType,
                                                   @PathVariable("page") Integer page,
                                                   @PathVariable("size") Integer size){
        return service.getApplicationList(staffNum, baseDate, vacationType, PageRequest.of(page, size));
    }

    @ApiOperation(value = "나의 휴가 신청 내역 상세 조회")
    @GetMapping("/user/application/{vacation-id}")
    public Vacation getMyApplication(@PathVariable("vacation-id") Long vacationId){
        return service.getMyApplication(vacationId);
    }
}
