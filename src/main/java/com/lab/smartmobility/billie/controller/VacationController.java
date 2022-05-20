package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.dto.ApplyVacationForm;
import com.lab.smartmobility.billie.dto.MyVacationDTO;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Vacation;
import com.lab.smartmobility.billie.service.VacationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(tags = {"휴가 관리 api"})
@RequestMapping("/vacation/*")
@RequiredArgsConstructor
public class VacationController {
    private final VacationService service;

    @GetMapping("/my-vacation-count/{staff-num}")
    @ApiOperation(value = "나의 휴가 개수, 전체 휴가 개수, 사용한 휴가 개수 및 소진기한 전달")
    public MyVacationDTO getMyVacationCount(@PathVariable(value = "staff-num") Long staffNum){
        return service.getMyVacationInfo(staffNum);
    }

    @PostMapping("/apply-vacation")
    @ApiOperation(value = "휴가 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "휴가 신청 실패 or 사용할 수 있는 휴가 개수를 모두 소진했습니다 or 휴가 신청 성공")
    })
    public HttpMessage applyVacation(@Valid @RequestBody List<ApplyVacationForm> applyVacationForms){
        int checkInsertData=service.applyVacation(applyVacationForms);

        if(checkInsertData==9999){
            return new HttpMessage("fail", "휴가 신청 실패");
        }
        else if(checkInsertData==500){
            return new HttpMessage("fail", "사용할 수 있는 휴가 개수를 모두 소진했습니다");
        }
        return new HttpMessage("success", "휴가 신청 성공");
    }

    @GetMapping("/{staff-num}/{start-date}/{end-date}/{approval-status}")
    @ApiOperation(value = "나의 휴가 신청 내역", notes = "승인 상태 전체 조회의 경우 approval-status에 'A'를 입력하여 전송")
    public List<Vacation> getMyApplicationList(@PathVariable("staff-num") Long staffNum,
                                               @ApiParam(value = "yyyy-MM-dd") @PathVariable("start-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                               @ApiParam(value = "yyyy-MM-dd") @PathVariable("end-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                               @ApiParam(value = "승인상태") @PathVariable("approval-status") char approvalStatus,
                                               @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return service.getMyApplicationList(staffNum, startDate, endDate, approvalStatus, pageRequest);
    }

    @PutMapping("/{vacation-num}")
    @ApiOperation(value = "휴가 신청 내역 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미 승인 절차가 진행중인 휴가 내역입니다 or 휴가 신청 내역 수정실패 or 휴가 신청 내역 수정완료")
    })
    public HttpMessage modifyVacationInfo(@PathVariable("vacation-num") Long vacationNum, @Valid @RequestBody ApplyVacationForm applyVacationForm){
        int isUpdated=service.updateMyVacationInfo(vacationNum, applyVacationForm);

        if(isUpdated==400){
            return new HttpMessage("fail", "이미 승인 절차가 진행중인 휴가 내역입니다");
        }else if(isUpdated==9999){
            return new HttpMessage("fail", "휴가 신청 내역 수정실패");
        }
        return new HttpMessage("success", "휴가 신청 내역 수정완료");
    }

    @DeleteMapping("/{vacation-num}")
    @ApiOperation(value = "휴가 일정 취소")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미 승인 절차가 진행중인 휴가 내역입니다 or 휴가 신청 내역 삭제실패 or 휴가 신청 내역 삭제완료")
    })
    public HttpMessage removeMyVacation(@PathVariable("vacation-num") Long vacationNum){
        int isDeleted=service.removeMyVacation(vacationNum);

        if(isDeleted==400){
            return new HttpMessage("fail", "이미 승인 절차가 진행중인 휴가 내역입니다");
        }else if(isDeleted==9999){
            return new HttpMessage("fail", "휴가 신청 내역 삭제실패");
        }
        return new HttpMessage("success", "휴가 신청 내역 삭제완료");
    }

    @GetMapping("/request-history/{staff-num}")
    @ApiOperation(value = "휴가 요청 관리 내역 조회", notes = "부서장 권한")
    public List<Vacation> requestHistory(@ApiParam(value = "로그인한 부서장의 직원번호") @PathVariable("staff-num") Long staffNum,
                                         @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return service.requestHistory(staffNum, pageRequest);
    }

    @PutMapping("/manager/approve-vacation")
    @ApiOperation(value = "부서장의 휴가 승인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청승인 실패 or 요청승인 성공")
    })
    public HttpMessage approveVacationOfManager(@RequestBody List<Vacation> vacationList){
        int isApproved=service.approveVacationOfManager(vacationList);

        if(isApproved==9999){
            return new HttpMessage("fail", "요청승인 실패");
        }
        return new HttpMessage("success", "요청승인 성공");
    }

    @PutMapping("/manager/companion-vacation")
    @ApiOperation(value = "부서장의 휴가 반려")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청반려 실패 or 요청반려 성공")
    })
    public HttpMessage rejectVacationOfManager(@RequestBody List<Vacation> vacationList){
        int isRejected=service.rejectVacationOfManager(vacationList);

        if(isRejected==9999){
            return new HttpMessage("fail", "요청반려 실패");
        }
        return new HttpMessage("success", "요청반려 성공");
    }

    @PutMapping("/admin/final-approve-vacation")
    @ApiOperation(value = "관리자 휴가 최종 승인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청승인 실패 or 요청승인 성공")
    })
    public HttpMessage approveVacationOfAdmin(@RequestBody List<Vacation> vacationList){
        int isApproved=service.approveVacationOfAdmin(vacationList);

        if(isApproved==9999){
            return new HttpMessage("fail", "요청승인 실패");
        }
        return new HttpMessage("success", "요청승인 성공");
    }

    @PutMapping("/admin/companion-vacation")
    @ApiOperation(value = "관리자의 휴가 반려")
    @ApiResponses({
            @ApiResponse(code = 200, message = "요청반려 실패 or 요청반려 성공")
    })
    public HttpMessage rejectVacationOfAdmin(@RequestBody List<Vacation> vacationList){
        int isRejected=service.rejectVacationOfAdmin(vacationList);

        if(isRejected==9999){
            return new HttpMessage("fail", "요청반려 실패");
        }
        return new HttpMessage("success", "요청반려 성공");
    }

    @PutMapping("/list/{start-date}/{end-date}")
    @ApiOperation(value = "승인된 월단위 전체 휴가목록 조회", notes = "조회하고자 하는 부서 목록 body로 전달( {departmentList : [부서목록]} ), 전체 조회의 경우 null 전달")
    public List<Vacation> getApprovedVacationList(@ApiParam(value = "yyyy-MM-dd") @PathVariable("start-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                  @ApiParam(value = "yyyy-MM-dd") @PathVariable("end-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                  @RequestBody HashMap<String, List<String>> departmentList){
        return service.getApprovedVacationList(startDate, endDate, departmentList);
    }

}
