package com.lab.smartmobility.billie.overtime.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.overtime.dto.OvertimeFinalApproveForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApproveListForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeCompanionForm;
import com.lab.smartmobility.billie.overtime.dto.TotalSubAndAdmitTimeDto;
import com.lab.smartmobility.billie.overtime.service.OvertimeApproveService;
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

@Api(tags = {"추가근무 승인 api"})
@RestController
@RequestMapping("/overtime")
@RequiredArgsConstructor
public class OvertimeApproveController {
    private final OvertimeApproveService service;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "부서장의 추가근무 승인 요청목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "기준연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "name", value = "검색어 (직원이름, 전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지 번호"),
            @ApiImplicitParam(name = "size", value = "페이지 당 데이터 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않는 경우"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음"),
    })
    @GetMapping("/approve/manager/{base-date}/{name}/{page}/{size}")
    public ResponseEntity<PageResult<OvertimeApproveListForm>> getApproveListByManager(@RequestHeader("X-AUTH-TOKEN") String token,
                                                                                       @PathVariable("base-date") String baseDate,
                                                                                       @PathVariable String name,
                                                                                       @PathVariable Integer page,
                                                                                       @PathVariable Integer size){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        PageResult<OvertimeApproveListForm> result = service.getApproveListByManager(baseDate, email, name, PageRequest.of(page, size));
        if(result.getCount() == 0){
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "추가근무 사전승인")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "승인하려는 추가근무 시퀀스 목록")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "추가근무 사전승인"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않는 경우"),
    })
    @PatchMapping("/approve/manager/{ids}")
    public ResponseEntity<HttpBodyMessage> approveByManager(@RequestHeader("X-AUTH-TOKEN") String token,
                                                            @PathVariable List<Long> ids){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        HttpBodyMessage result = service.approveByManager(ids, email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "근무반려")

    @ApiResponses({
            @ApiResponse(code = 200, message = "반려성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않는 경우"),
    })
    @PatchMapping("/approve/manager")
    public ResponseEntity<HttpBodyMessage> reject(@RequestBody List<OvertimeCompanionForm> companionFormList){
        HttpBodyMessage result = service.reject(companionFormList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "관리부의 추가근무 승인요청 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "기준연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "department", value = "부서명 (전체는 all)"),
            @ApiImplicitParam(name = "name", value = "검색어 (직원이름, 전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지 번호"),
            @ApiImplicitParam(name = "size", value = "페이지 당 데이터 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않는 경우"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음"),
    })
    @GetMapping("/approve/admin/{name}/{department}/{base-date}/{page}/{size}")
    public ResponseEntity<PageResult<OvertimeApproveListForm>> getApproveListByAdmin(@PathVariable String name,
                                                                                       @PathVariable("base-date") String baseDate,
                                                                                       @PathVariable String department,
                                                                                       @PathVariable Integer page,
                                                                                       @PathVariable Integer size){
        PageResult<OvertimeApproveListForm> result = service.getApproveListByAdmin(name, department, baseDate, PageRequest.of(page, size));
        if(result.getCount() == 0){
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "직원별 추가근무 총 인정시간 및 제출시간 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "직원이름 (필터링할 경우면 사용)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공")
    })
    @GetMapping("/approve/admin/total/{name}/{base-date}")
    public ResponseEntity<TotalSubAndAdmitTimeDto> getTotalSubAndAdmitTime(@PathVariable String name,
                                                                           @PathVariable("base-date") String baseDate){
        TotalSubAndAdmitTimeDto result = service.getTotalSubAndAdmitTime(name, baseDate);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "최종승인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "최종승인")
    })
    @PatchMapping("/approve/admin")
    public ResponseEntity<HttpBodyMessage> approveByAdmin(@RequestBody OvertimeFinalApproveForm finalApproveForm){
        HttpBodyMessage result = service.approveByAdmin(finalApproveForm);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "추가근무 승인내역 엑셀 다운로드")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "기준연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "department", value = "부서명 (전체는 all)"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "다운로드성공"),
    })
    @GetMapping("/approve/admin/excel/{base-date}/{department}")
    public void downloadExcel(@PathVariable("base-date") String baseDate,
                              @PathVariable String department, HttpServletResponse response) throws IOException {
        Workbook wb = service.downloadExcel(baseDate, department);

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename="+baseDate+"_overtime.xlsx");

        wb.write(response.getOutputStream());
        wb.close();
    }
}
