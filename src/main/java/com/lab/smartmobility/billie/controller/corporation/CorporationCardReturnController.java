package com.lab.smartmobility.billie.controller.corporation;

import com.lab.smartmobility.billie.dto.TotalCount;
import com.lab.smartmobility.billie.dto.corporation.CorporationHistoryForm;
import com.lab.smartmobility.billie.dto.corporation.CorporationReturnForm;
import com.lab.smartmobility.billie.dto.corporation.ExpenseClaimForm;
import com.lab.smartmobility.billie.dto.corporation.ExpenseClaimHistoryForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.service.CorporationCardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/corporation-card/*")
@Api(tags = {"법인카드 승인 api"})
@RequiredArgsConstructor
public class CorporationCardReturnController {
    private final CorporationCardService service;

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
