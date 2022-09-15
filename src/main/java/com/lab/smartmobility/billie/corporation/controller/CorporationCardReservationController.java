package com.lab.smartmobility.billie.corporation.controller;

import com.lab.smartmobility.billie.global.dto.TotalCount;
import com.lab.smartmobility.billie.corporation.dto.ApplyCorporationCardForm;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.corporation.domain.Application;
import com.lab.smartmobility.billie.corporation.service.CorporationCardReservationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/corporation-card/*")
@Api(tags = {"법인카드 예약 api"})
@RequiredArgsConstructor
public class CorporationCardReservationController {
    private final CorporationCardReservationService service;

    @ApiOperation(value = "법인카드 예약 요청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "cannot-reservation-earlier-day // fail-application // success-application")
    })
    @PostMapping("/rent")
    public HttpBodyMessage rent(@Valid @RequestBody ApplyCorporationCardForm applyCorporationCardForm) {
        return service.applyCardReservation(applyCorporationCardForm);
    }

    @ApiOperation(value = "후불 경비청구 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fail-application // success-application")
    })
    @PostMapping("/post-expense")
    public HttpBodyMessage postExpenseClaim(@Valid @RequestBody ApplyCorporationCardForm applyCorporationCardForm) {
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
    public HttpBodyMessage modifyApplicationInfo(@PathVariable("application-id") Long applicationId,
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
    public HttpBodyMessage removeApplicationInfo(@PathVariable("application-id") Long applicationId,
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
    public HttpBodyMessage removeApplicationInfoByAdmin(@PathVariable("application-id") Long applicationId){
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
    public HttpBodyMessage modifyApplicationInfoByAdmin(@PathVariable("application-id") Long applicationId,
                                                        @Valid @RequestBody ApplyCorporationCardForm applyCorporationCardForm){
        return service.modifyCardUseApplicationByAdmin(applicationId, applyCorporationCardForm);
    }
}
