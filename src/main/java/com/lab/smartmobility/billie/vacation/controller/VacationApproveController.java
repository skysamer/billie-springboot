package com.lab.smartmobility.billie.vacation.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.vacation.dto.VacationApproveListForm;
import com.lab.smartmobility.billie.vacation.dto.VacationCompanionForm;
import com.lab.smartmobility.billie.vacation.service.VacationApproveService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api(tags = {"휴가 승인 api"})
@RestController
@RequestMapping("/vacation")
@RequiredArgsConstructor
public class VacationApproveController {
    private final VacationApproveService service;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "부서장의 휴가승인 요청 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "연월(yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "keyword", value = "검색어(직원이름, 전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지 번호"),
            @ApiImplicitParam(name = "size", value = "페이지 당 데이터 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않은 경우"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음")
    })
    @GetMapping("/approve/manager/{base-date}/{keyword}/{page}/{size}")
    public ResponseEntity<PageResult<VacationApproveListForm>> getApproveListByManager(@RequestHeader("X-AUTH-TOKEN") String token,
                                                                                       @PathVariable("base-date") String baseDate,
                                                                                       @PathVariable String keyword,
                                                                                       @PathVariable Integer page,
                                                                                       @PathVariable Integer size){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);
        PageResult<VacationApproveListForm> vacationApproveList = service.getApproveListByManager(baseDate, email, keyword, PageRequest.of(page, size));

        if(vacationApproveList.getCount() == 0){
            return new ResponseEntity<>(vacationApproveList, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(vacationApproveList, HttpStatus.OK);
    }

    @ApiOperation(value = "부서장의 휴가승인")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id-list", value = "휴가 id list"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "휴가승인성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않은 경우"),
    })
    @PatchMapping("/approve/manager/{id-list}")
    public ResponseEntity<HttpBodyMessage> approveByManager(@RequestHeader("X-AUTH-TOKEN") String token, @PathVariable("id-list") List<Long> idList){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        HttpBodyMessage result = service.approveByManager(idList, email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "휴가반려")
    @ApiResponses({
            @ApiResponse(code = 200, message = "휴가반려")
    })
    @PatchMapping("/approve/manager")
    public ResponseEntity<HttpBodyMessage> reject(@RequestBody List<VacationCompanionForm> companionFormList){
        HttpBodyMessage result = service.reject(companionFormList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "관리자의 휴가승인 요청 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "keyword", value = "검색어 (직원이름, 전체는 all)"),
            @ApiImplicitParam(name = "department", value = "부서명 (전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지 번호"),
            @ApiImplicitParam(name = "size", value = "페이지 당 데이터 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않은 경우"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음")
    })
    @GetMapping("/approve/admin/{base-date}/{department}/{keyword}/{page}/{size}")
    public ResponseEntity<PageResult<VacationApproveListForm>> getApproveListByAdmin(@PathVariable("base-date") String baseDate,
                                                                                     @PathVariable String department,
                                                                                     @PathVariable String keyword,
                                                                                     @PathVariable Integer page,
                                                                                     @PathVariable Integer size){
        PageResult<VacationApproveListForm> vacationApproveList = service.getApproveListByAdmin(baseDate, department, keyword, PageRequest.of(page, size));

        if(vacationApproveList.getCount() == 0){
            return new ResponseEntity<>(vacationApproveList, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(vacationApproveList, HttpStatus.OK);
    }

    @ApiOperation(value = "관리자의 휴가승인")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id-list", value = "휴가 id list"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "휴가승인성공")
    })
    @PatchMapping("/approve/admin/{id-list}")
    public ResponseEntity<HttpBodyMessage> approveByAdmin(@PathVariable("id-list") List<Long> idList){
        HttpBodyMessage result = service.approveByAdmin(idList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "휴가요청내역 엑셀 다운로드")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "department", value = "부서명 (전체는 all)"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "다운로드 성공")
    })
    @GetMapping("approve/excel/{base-date}/{department}")
    public void downloadExcel(@PathVariable("base-date") String baseDate,
                              @PathVariable String department, HttpServletResponse response) throws IOException {
        Workbook wb = service.downloadExcel(baseDate, department);

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename="+baseDate+"_vacation.xlsx");

        wb.write(response.getOutputStream());
        wb.close();
    }
}
