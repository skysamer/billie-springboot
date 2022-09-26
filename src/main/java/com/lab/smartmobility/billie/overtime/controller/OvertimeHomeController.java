package com.lab.smartmobility.billie.overtime.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.overtime.dto.OvertimeHourDTO;
import com.lab.smartmobility.billie.overtime.service.OvertimeHomeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"추가근무 홈 api"})
@RestController
@RequestMapping("/overtime")
@RequiredArgsConstructor
public class OvertimeHomeController {
    private final OvertimeHomeService service;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "부서장의 휴가승인 요청 목록 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않은 경우")
    })
    @GetMapping("/home/user")
    public ResponseEntity<OvertimeHourDTO> getApproveListByManager(@RequestHeader("X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);
        OvertimeHourDTO overtimeHourDTO = service.getMyOvertimeHour(email);
        return new ResponseEntity<>(overtimeHourDTO, HttpStatus.OK);
    }
}
