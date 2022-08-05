package com.lab.smartmobility.billie.controller.vacation;

import com.lab.smartmobility.billie.dto.MyVacationDTO;
import com.lab.smartmobility.billie.service.vacation.VacationCalculateService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"휴가 계산 api"})
@RequiredArgsConstructor
@RequestMapping("/vacation/*")
@RestController
public class VacationCalculateController {
    private final VacationCalculateService service;
    private final Log log;

    @ApiOperation(value = "나의 남은 휴가 개수, 전체 개수, 사용 개수 및 소진기한 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상요청"),
            @ApiResponse(code = 404, message = "직원정보없음")
    })
    @GetMapping("/user/calculate/{staff-num}")
    public ResponseEntity<MyVacationDTO> getMyVacation(@PathVariable("staff-num") Long staffNum){
        MyVacationDTO myVacationDTO = service.getMyVacationInfo(staffNum);
        if(myVacationDTO == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(myVacationDTO, HttpStatus.OK);
    }

}
