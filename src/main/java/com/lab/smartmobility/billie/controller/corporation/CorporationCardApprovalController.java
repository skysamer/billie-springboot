package com.lab.smartmobility.billie.controller.corporation;

import com.lab.smartmobility.billie.global.dto.TotalCount;
import com.lab.smartmobility.billie.dto.corporation.ApprovalCardUseForm;
import com.lab.smartmobility.billie.dto.corporation.CompanionCardUseForm;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.service.corporation.CorporationCardApprovalService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/corporation-card/*")
@Api(tags = {"법인카드 승인 api"})
@RequiredArgsConstructor
public class CorporationCardApprovalController {
    private final CorporationCardApprovalService service;

    @ApiOperation(value = "부서장의 카드 사용승인 요청 목록 조회", notes = "부서장 권한만 이용 가능")
    @GetMapping("/request-list-manager/{manager-num}/{card-name}/{base-year}/{disposal-info}/{page}/{size}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "manager-num", value = "부서장 직원 고유번호"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수", dataType = "int")
    })
    public List<Application> getListOfApprovalsRequestByManager(@PathVariable("manager-num") Long managerNum,
                                                                @PathVariable("card-name") String cardName,
                                                                @PathVariable("base-year") String baseYear,
                                                                @PathVariable("disposal-info") int disposalInfo,
                                                                @PathVariable("page") Integer page,
                                                                @PathVariable("size") Integer size){
        return service.getListOfApprovalsRequestByManager(managerNum, cardName, baseYear, disposalInfo, PageRequest.of(page,size));
    }

    @ApiOperation(value = "부서장의 카드 사용승인 요청 목록 개수 조회", notes = "부서장 권한만 이용 가능")
    @GetMapping("/request-count-manager/{manager-num}/{card-name}/{base-year}/{disposal-info}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "manager-num", value = "부서장 직원 고유번호"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)")
    })
    public TotalCount getListOfApprovalsRequestByManager(@PathVariable("manager-num") Long managerNum,
                                                         @PathVariable("card-name") String cardName,
                                                         @PathVariable("base-year") String baseYear,
                                                         @PathVariable("disposal-info") int disposalInfo){
        return service.getCountOfApprovalsRequestByManager(managerNum, cardName, baseYear, disposalInfo);
    }

    @ApiOperation(value = "부서장의 카드 사용 일괄 승인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-approve // success-approve")
    })
    @PutMapping("/approve/manager")
    public HttpBodyMessage approveCardUseByManager(@RequestBody List<ApprovalCardUseForm> approvalCardUseFormList){
        return service.approveCardUseByManager(approvalCardUseFormList);
    }

    @ApiOperation(value = "카드 사용 반려")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-reject // success-reject")
    })
    @PutMapping("/companion/card-use")
    public HttpBodyMessage rejectCardUse(@RequestBody List<CompanionCardUseForm> companionCardUseForms){
        return service.rejectCardUse(companionCardUseForms);
    }

    @ApiOperation(value = "관리자의 카드 사용승인 요청 목록 조회")
    @GetMapping("/request-list-admin/{card-name}/{base-year}/{disposal-info}/{page}/{size}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수", dataType = "int")
    })
    public List<Application> getListOfApprovalsRequestByManager(@PathVariable("card-name") String cardName,
                                                                @PathVariable("base-year") String baseYear,
                                                                @PathVariable("disposal-info") int disposalInfo,
                                                                @PathVariable("page") Integer page,
                                                                @PathVariable("size") Integer size){
        return service.getListOfApprovalsRequestByAdmin(cardName, baseYear, disposalInfo, PageRequest.of(page,size));
    }

    @ApiOperation(value = "관리자의 카드 사용승인 요청 목록 개수 조회")
    @GetMapping("/request-count-admin/{card-name}/{base-year}/{disposal-info}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)")
    })
    public TotalCount getListOfApprovalsRequestByManager(@PathVariable("card-name") String cardName,
                                                         @PathVariable("base-year") String baseYear,
                                                         @PathVariable("disposal-info") int disposalInfo){
        return service.getCountOfApprovalsRequestByAdmin(cardName, baseYear, disposalInfo);
    }

    @ApiOperation(value = "관리부 최종 카드 사용 일괄 승인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "this-card-is-already-used // success-final-approve")
    })
    @PutMapping("/approve/admin")
    public HttpBodyMessage approveCardUseByAdmin(@RequestBody List<ApprovalCardUseForm> approvalCardUseFormList){
        try{
            return service.approveCardUseByAdmin(approvalCardUseFormList);
        }catch (Exception e){
            return new HttpBodyMessage("fail", "this-card-is-already-rented");
        }
    }

    @ApiOperation(value = "승인된 카드 신청 내역 월별 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start-date", value = "시작일자 (yyyy-MM-dd)"),
            @ApiImplicitParam(name = "end-date", value = "종료일자 (yyyy-MM-dd)")
    })
    @GetMapping("/approved/monthly/{start-date}/{end-date}")
    public List<Application> getApprovedApplicationListMonthly(@DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("start-date") LocalDate startDate,
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("end-date") LocalDate endDate){
        return service.getApprovedApplicationListMonthly(startDate, endDate);
    }

    @ApiOperation(value = "승인된 법인카드 신청 내역 상세 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "application-id", value = "신청 내역 고유 시퀀스")
    })
    @GetMapping("/approved/get/{application-id}")
    public Application getApprovedApplication(@PathVariable("application-id") Long applicationId){
        return service.getApprovedApplication(applicationId);
    }

    @ApiOperation(value = "내가 사용중인 법인카드 목록 및 승인대기목록 조회 (경비청구 포함)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value =  "직원 고유번호")
    })
    @GetMapping("/approve/my/{staff-num}")
    public List<Application> getMyCorporationCard(@PathVariable("staff-num") Long staffNum){
        return service.getMyCorporationCard(staffNum);
    }
}
