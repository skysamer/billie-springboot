package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.dto.TotalCount;
import com.lab.smartmobility.billie.dto.corporation.*;
import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.entity.corporation.CorporationCard;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.service.CorporationCardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/corporation-card/*")
@Api(tags = {"법인카드 api"})
@RequiredArgsConstructor
public class CorporationCardController {
    private final CorporationCardService service;

    @ApiOperation(value = "신규 법인카드 등록")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-insert // success-insert")
    })
    @PostMapping("/insert")
    public HttpMessage createNewCard(@RequestBody CorporationCardForm corporationCardForm){
        int isInserted= service.createCard(corporationCardForm);
        if(isInserted==9999){
            return new HttpMessage("fail", "fail-insert");
        }
        return new HttpMessage("success", "success-insert");
    }

    @ApiOperation(value = "보유 법인카드 목록 조회")
    @ApiImplicitParam(name = "disposal-info", value = "폐기정보 포함:1, 미포함:0")
    @GetMapping("/list/{disposal-info}")
    public List<CorporationCard> getCardList(@PathVariable("disposal-info") int disposalInfo){
        return service.getCardList(disposalInfo);
    }

    @ApiOperation(value = "개별 법인카드 정보 상세 조회")
    @ApiImplicitParam(
            name = "card-id",
            value = "카드 고유 시퀀스"
    )
    @GetMapping("/{card-id}")
    public CorporationCard getCardInfo(@PathVariable("card-id") Long cardId){
        return service.getCardInfo(cardId);
    }

    @ApiOperation(value = "법인카드 정보 수정")
    @ApiImplicitParam(name = "card-id", value = "카드 고유 시퀀스")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-modify // success-modify")
    })
    @PutMapping("/modify/{card-id}")
    public HttpMessage modifyCardInfo(@PathVariable("card-id") Long cardId, @RequestBody CorporationCardForm corporationCardForm){
        int isModified= service.modifyCardInfo(cardId, corporationCardForm);
        if(isModified==9999){
            return new HttpMessage("fail", "fail-modify");
        }
        return new HttpMessage("success", "success-modify");
    }

    @ApiOperation(value = "법인카드 폐기")
    @ApiImplicitParam(name = "card-id", value = "카드 고유 시퀀스")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-discard // success-discard")
    })
    @PatchMapping("/disposal/{card-id}")
    public HttpMessage abrogate(@PathVariable("card-id") Long cardId, @RequestBody DisposalForm disposalForm){
        int isDiscarded= service.abrogate(cardId, disposalForm);
        if(isDiscarded==9999){
            return new HttpMessage("fail", "fail-discard");
        }
        return new HttpMessage("success", "success-discard");
    }

    @ApiOperation(value = "법인카드 정보 삭제")
    @ApiImplicitParam(name = "card-id", value = "카드 고유 시퀀스")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-remove // success-remove // not-exist-card-info")
    })
    @DeleteMapping("/remove/{card-id}")
    public HttpMessage remove(@PathVariable("card-id") Long cardId){
        return service.remove(cardId);
    }

    @ApiOperation(value = "법인카드 예약 요청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "cannot-reservation-earlier-day // fail-application // success-application")
    })
    @PostMapping("/rent")
    public HttpMessage rent(@Valid @RequestBody ApplyCorporationCardForm applyCorporationCardForm) {
        return service.applyCardReservation(applyCorporationCardForm);
    }

    @ApiOperation(value = "후불 경비청구 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-application // success-application")
    })
    @PostMapping("/post-expense")
    public HttpMessage postExpenseClaim(@Valid @RequestBody ApplyCorporationCardForm applyCorporationCardForm) {
        return service.applyPostExpenseClaim(applyCorporationCardForm);
    }

    @ApiOperation(value = "나의 법인카드 사용 신청 내역 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유번호"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수")
    })
    @GetMapping("/my-application-list/{staff-num}/{card-name}/{base-year}/{page}/{size}")
    public List<Application> myApplicationList(@PathVariable("staff-num") Long staffNum,
                                                       @PathVariable("card-name") String cardName,
                                                       @PathVariable("base-year") String baseYear,
                                                       @PathVariable("page") Integer page,
                                                       @PathVariable("size") Integer size){
        return service.myApplicationList(staffNum, cardName, baseYear, PageRequest.of(page, size));
    }

    @ApiOperation(value = "나의 법인카드 사용 신청 내역 조건별 개수 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유번호"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/my-application-count/{staff-num}/{card-name}/{base-year}")
    public TotalCount myApplicationList(@PathVariable("staff-num") Long staffNum,
                                        @PathVariable("card-name") String cardName,
                                        @PathVariable("base-year") String baseYear){
        return service.myApplicationCount(staffNum, cardName, baseYear);
    }

    @ApiOperation(value = "나의 법인카드 사용 신청 내역 상세 조회")
    @GetMapping("/my-application/{staff-num}/{application-id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유번호"),
            @ApiImplicitParam(name = "application-id", value = "카드 사용 신청 고유 시퀀스")
    })
    public Application myApplication(@PathVariable("staff-num") Long staffNum,
                                     @PathVariable("application-id") Long applicationId){
        return service.myApplication(staffNum, applicationId);
    }

    @ApiOperation(value = "법인카드 사용 신청 내역 수정")
    @PutMapping("/modify/application/{application-id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "application-id", value = "카드 신청 고유 시퀀스"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "cannot-reservation-earlier-day // this-application-is-approved // not-the-employee // modify-application")
    })
    public HttpMessage modifyApplicationInfo(@PathVariable("application-id") Long applicationId,
                                             @Valid @RequestBody ApplyCorporationCardForm applyCorporationCardForm){
        return service.modifyCardUseApplicationInfo(applicationId, applyCorporationCardForm);
    }

    @ApiOperation(value = "법인카드 사용 신청 내역 삭제")
    @DeleteMapping("/remove/application/{application-id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "application-id", value = "카드 신청 고유 시퀀스"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "this-application-is-approved // remove-application")
    })
    public HttpMessage removeApplicationInfo(@PathVariable("application-id") Long applicationId,
                                             @Valid @RequestBody ApplyCorporationCardForm applyCorporationCardForm){
        return service.removeCardUseApplicationInfo(applicationId);
    }

    @ApiOperation(value = "관리자의 법인카드 사용 신청 내역 삭제")
    @DeleteMapping("/admin/remove/application/{application-id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "application-id", value = "카드 신청 고유 시퀀스"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "remove-application // not-exist-info")
    })
    public HttpMessage removeApplicationInfoByAdmin(@PathVariable("application-id") Long applicationId){
        return service.removeCardUseApplicationByAdmin(applicationId);
    }

    @ApiOperation(value = "관리자의 법인카드 사용 신청 내역 수정")
    @PutMapping("/admin/modify/application/{application-id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "application-id", value = "카드 신청 고유 시퀀스"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "modify-application // not-exist-info")
    })
    public HttpMessage modifyApplicationInfoByAdmin(@PathVariable("application-id") Long applicationId,
                                                    @Valid @RequestBody ApplyCorporationCardForm applyCorporationCardForm){
        return service.modifyCardUseApplicationByAdmin(applicationId, applyCorporationCardForm);
    }

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
    public HttpMessage approveCardUseByManager(@RequestBody List<ApprovalCardUseForm> approvalCardUseFormList){
        return service.approveCardUseByManager(approvalCardUseFormList);
    }

    @ApiOperation(value = "카드 사용 반려")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-reject // success-reject")
    })
    @PutMapping("/companion/card-use")
    public HttpMessage rejectCardUse(@RequestBody List<CompanionCardUseForm> companionCardUseForms){
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
    public HttpMessage approveCardUseByAdmin(@RequestBody List<ApprovalCardUseForm> approvalCardUseFormList){
        try{
            return service.approveCardUseByAdmin(approvalCardUseFormList);
        }catch (Exception e){
            return new HttpMessage("fail", "this-card-is-already-rented");
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

    @ApiOperation(value = "법인카드 반납 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "not-approved-application // fail-card-return // success-card-return")
    })
    @PostMapping("/return")
    public HttpMessage ApplyReturnCard(@RequestBody CorporationReturnForm corporationReturnForm){
        return service.returnCorporationCard(corporationReturnForm);
    }

    @ApiOperation(value = "개인 경비청구 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "not-approved-application // fail-expense-claim // success-expense-claim")
    })
    @PostMapping("/expense-claim")
    public HttpMessage ApplyReturnCard(@RequestBody ExpenseClaimForm expenseClaimForm){
        return service.chargeForExpenses(expenseClaimForm);
    }

    @ApiOperation(value = "법인카드 반납 이력 상세 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "return-id", value = "법인카드 반납 고유 시퀀스")
    })
    @GetMapping("/return-history/{return-id}")
    public CorporationHistoryForm getCorporationHistoryInfo(@PathVariable("return-id") Long returnId){
        return service.getCorporationHistory(returnId);
    }

    @ApiOperation(value = "경비청구 이력 상세 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expense-id", value = "경비청구 고유 시퀀스")
    })
    @GetMapping("/expense-history/{expense-id}")
    public ExpenseClaimHistoryForm getExpenseClaimHistory(@PathVariable("expense-id") Long expenseId){
        return service.getClaimHistoryInfo(expenseId);
    }

    @ApiOperation(value = "나의 법인카드 반납 이력 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유번호"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수")
    })
    @GetMapping("/my-return-history/{staff-num}/{card-name}/{base-year}/{page}/{size}")
    public List<CorporationHistoryForm> getMyReturnHistoryList(@PathVariable("staff-num") Long staffNum,
                                                               @PathVariable("card-name") String cardName,
                                                               @PathVariable("base-year") String baseYear,
                                                               @PathVariable("page") Integer page,
                                                               @PathVariable("size") Integer size){
        return service.getMyReturnHistory(staffNum, cardName, baseYear, PageRequest.of(page, size));
    }

    @ApiOperation(value = "나의 법인카드 반납 이력 조건별 개수 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유번호"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/count/my-return-history/{staff-num}/{card-name}/{base-year}")
    public TotalCount getMyReturnHistoryCount(@PathVariable("staff-num") Long staffNum,
                                                               @PathVariable("card-name") String cardName,
                                                               @PathVariable("base-year") String baseYear){
        return service.getMyReturnHistoryCount(staffNum, cardName, baseYear);
    }

    @ApiOperation(value = "나의 경비청구 이력 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유번호"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수")
    })
    @GetMapping("/my-expense-history/{staff-num}/{base-year}/{page}/{size}")
    public List<ExpenseClaimHistoryForm> getMyExpenseHistory(@PathVariable("staff-num") Long staffNum,
                                                                 @PathVariable("base-year") String baseYear,
                                                                 @PathVariable("page") Integer page,
                                                                 @PathVariable("size") Integer size){
        return service.getMyExpenseClaimHistoryList(staffNum, baseYear, PageRequest.of(page, size));
    }

    @ApiOperation(value = "나의 경비청구 이력 조건별 개수")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유번호"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/count/my-expense-history/{staff-num}/{base-year}")
    public TotalCount getMyExpenseHistory(@PathVariable("staff-num") Long staffNum,
                                                             @PathVariable("base-year") String baseYear){
        return service.getMyExpenseHistoryCount(staffNum, baseYear);
    }

    @ApiOperation(value = "부서장의 법인카드 반납 이력 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "manager-num", value = "부서장 직원 고유번호"),
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수", dataType = "int")
    })
    @GetMapping("/manager/return-history/{manager-num}/{disposal-info}/{card-name}/{base-year}/{page}/{size}")
    public List<CorporationHistoryForm> getCardReturnHistoryListByManager(@PathVariable("manager-num") Long managerNum,
                                                                          @PathVariable("card-name") String cardName,
                                                                          @PathVariable("base-year") String baseYear,
                                                                          @PathVariable("disposal-info") int disposalInfo,
                                                                          @PathVariable("page") Integer page,
                                                                          @PathVariable("size") Integer size){
        return service.getCardReturnHistoryListByManager(managerNum, disposalInfo, cardName, baseYear, PageRequest.of(page, size));
    }

    @ApiOperation(value = "부서장의 법인카드 반납 이력 조건별 개수")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "manager-num", value = "부서장 직원 고유번호"),
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/count/manager/return-history/{manager-num}/{disposal-info}/{card-name}/{base-year}")
    public TotalCount getCardReturnHistoryListByManager(@PathVariable("manager-num") Long managerNum,
                                                        @PathVariable("card-name") String cardName,
                                                        @PathVariable("base-year") String baseYear,
                                                        @PathVariable("disposal-info") int disposalInfo){
        return service.getCardReturnHistoryCountByManager(managerNum, disposalInfo, cardName, baseYear);
    }

    @ApiOperation(value = "부서장의 경비청구 이력 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "manager-num", value = "부서장 직원 고유번호"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수", dataType = "int")
    })
    @GetMapping("/manager/expense-history/{manager-num}/{base-year}/{page}/{size}")
    public List<ExpenseClaimHistoryForm> getExpenseClaimHistoryListByManager(@PathVariable("manager-num") Long managerNum,
                                                                             @PathVariable("base-year") String baseYear,
                                                                             @PathVariable("page") Integer page,
                                                                             @PathVariable("size") Integer size){
        return service.getExpenseClaimHistoryListByManager(managerNum, baseYear, PageRequest.of(page, size));
    }

    @ApiOperation(value = "부서장의 경비청구 이력 조건별 개수")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "manager-num", value = "부서장 직원 고유번호"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/count/manager/expense-history/{manager-num}/{base-year}")
    public TotalCount getExpenseClaimHistoryListByManager(@PathVariable("manager-num") Long managerNum,
                                                          @PathVariable("base-year") String baseYear){
        return service.getExpenseClaimHistoryCountByManager(managerNum, baseYear);
    }

    @ApiOperation(value = "관리자 법인카드 반납 이력 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수", dataType = "int")
    })
    @GetMapping("/admin/return-history/{disposal-info}/{card-name}/{base-year}/{page}/{size}")
    public List<CorporationHistoryForm> getCardReturnHistoryListByAdmin(@PathVariable("card-name") String cardName,
                                                                          @PathVariable("base-year") String baseYear,
                                                                          @PathVariable("disposal-info") int disposalInfo,
                                                                          @PathVariable("page") Integer page,
                                                                          @PathVariable("size") Integer size){
        return service.getCardReturnHistoryListByAdmin(disposalInfo, cardName, baseYear, PageRequest.of(page, size));
    }

    @ApiOperation(value = "관리자 법인카드 반납 이력 조건별 개수")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/count/admin/return-history/{disposal-info}/{card-name}/{base-year}")
    public TotalCount getCardReturnHistoryListByAdmin(@PathVariable("card-name") String cardName,
                                                                        @PathVariable("base-year") String baseYear,
                                                                        @PathVariable("disposal-info") int disposalInfo){
        return service.getCardReturnHistoryCountByAdmin(disposalInfo, cardName, baseYear);
    }

    @ApiOperation(value = "법인카드 반납 이력 엑셀 다운로드", response = byte.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "disposal-info", value = "폐기정보 (0:미포함, 1:포함)"),
            @ApiImplicitParam(name = "card-name", value = "카드이름 (카드사 끝번호4자리, 전체는 all)"),
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/excel/return-history/{disposal-info}/{card-name}/{base-year}")
    public void excelDownload(@PathVariable("disposal-info") int disposalInfo,
                              @PathVariable("card-name") String cardName,
                              @PathVariable("base-year") String baseYear, HttpServletResponse response) throws IOException {
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename="+baseYear+"_corporation_history.xlsx");

        Workbook workbook=service.excelDownloadReturnHistory(disposalInfo, cardName, baseYear);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @ApiOperation(value = "관리자 경비청구 이력 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수", dataType = "int")
    })
    @GetMapping("/admin/expense-history/{base-year}/{page}/{size}")
    public List<ExpenseClaimHistoryForm> getCardReturnHistoryListByAdmin(@PathVariable("base-year") String baseYear,
                                                                        @PathVariable("page") Integer page,
                                                                        @PathVariable("size") Integer size){
        return service.getExpenseClaimHistoryListByAdmin(baseYear, PageRequest.of(page, size));
    }

    @ApiOperation(value = "관리자 경비청구 이력 조건별 개수")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/count/admin/expense-history/{base-year}")
    public TotalCount getCardReturnHistoryListByAdmin(@PathVariable("base-year") String baseYear){
        return service.getExpenseClaimHistoryCountByAdmin(baseYear);
    }

    @ApiOperation(value = "경비청구 이력 엑셀 다운로드", response = byte.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-year", value = "yyyy-MM (전체는 all)")
    })
    @GetMapping("/excel/expense-history/{base-year}")
    public void excelDownload(@PathVariable("base-year") String baseYear, HttpServletResponse response) throws IOException {
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename="+baseYear+"_expense_history.xlsx");

        Workbook workbook=service.excelDownloadExpenseClaimHistory(baseYear);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
