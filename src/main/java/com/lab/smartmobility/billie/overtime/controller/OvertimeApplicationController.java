package com.lab.smartmobility.billie.overtime.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApplicationListForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApplyForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeConfirmationForm;
import com.lab.smartmobility.billie.overtime.service.OvertimeApplicationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"추가근무 신청 api"})
@RestController
@RequestMapping("/overtime")
@RequiredArgsConstructor
public class OvertimeApplicationController {
    private final OvertimeApplicationService service;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "추가근무 신청")
    @ApiResponses({
            @ApiResponse(code = 201, message = "추가근무 신청 성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않은 경우"),
    })
    @PostMapping("/application/user")
    public ResponseEntity<HttpBodyMessage> apply(@RequestHeader("X-AUTH-TOKEN") String token,
                                                 @Valid @RequestBody OvertimeApplyForm applyForm){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        HttpBodyMessage result = service.apply(email, applyForm);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @ApiOperation(value = "나의 추가근무 신청 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base-date", value = "기준연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "page", value = "페이지 번호"),
            @ApiImplicitParam(name = "size", value = "페이지 당 데이터 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "추가근무 신청 성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않는 경우"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음"),
    })
    @GetMapping("/application/user/{base-date}/{page}/{size}")
    public ResponseEntity<PageResult<OvertimeApplicationListForm>> getApplicationList(@RequestHeader("X-AUTH-TOKEN") String token,
                                                              @PathVariable("base-date") String baseDate,
                                                              @PathVariable Integer page,
                                                              @PathVariable Integer size){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        PageResult<OvertimeApplicationListForm> result = service.getApplicationList(email, baseDate, PageRequest.of(page, size));
        if(result.getCount() == 0){
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "추가근무 신청 내역 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "추가근무 고유 번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제성공"),
            @ApiResponse(code = 400, message = "이미 확정된 추가근무내역")
    })
    @DeleteMapping("/application/user/{id}")
    public ResponseEntity<HttpBodyMessage> remove(@PathVariable Long id){
        HttpBodyMessage result = service.remove(id);
        if(result.getCode().equals("fail")){
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "근무확정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "추가근무 고유 번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제성공")
    })
    @PutMapping("/application/user/{id}")
    public ResponseEntity<HttpBodyMessage> confirm(@PathVariable Long id,
                                                   @RequestBody OvertimeConfirmationForm confirmationForm){
        HttpBodyMessage result = service.confirm(id, confirmationForm);
        if(result.getCode().equals("fail")){
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
