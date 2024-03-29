package com.lab.smartmobility.billie.staff.controller;

import com.lab.smartmobility.billie.staff.dto.StaffInfoForm;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.staff.service.MyPageService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Api(tags = {"마이페이지 api"})
@RequestMapping("/my-page/*")
@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService service;

    @ApiOperation(value = "전체 직원 정보 조회")
    @GetMapping("/user")
    public List<StaffInfoForm> getStaffInfoList(){
        return service.getStaffInfoList();
    }

    @ApiOperation(value = "직원 정보 상세 조회", notes = "나의 정보 조회시에도 사용")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유 번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
    })
    @GetMapping("/user/{staff-num}")
    public ResponseEntity<StaffInfoForm> getStaffInfo(@PathVariable("staff-num") Long staffNum){
        StaffInfoForm staffInfo = service.getStaffInfo(staffNum);
        return new ResponseEntity<>(staffInfo, HttpStatus.OK);
    }

    @ApiOperation(value = "신규직원 추가", notes = "고유번호와 퇴서여부는 전송x")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success-register")
    })
    @PostMapping("/admin/register")
    public HttpBodyMessage register(@RequestBody StaffInfoForm staffInfoForm){
        return service.registerNewStaff(staffInfoForm);
    }

    @ApiOperation(value = "직원 정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유 번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "success-modify // not-exits-staff-info")
    })
    @PutMapping("/admin/{staff-num}")
    public HttpBodyMessage modify(@PathVariable("staff-num") Long staffNum, @RequestBody StaffInfoForm staffInfoForm){
        return service.modifyStaffInfo(staffNum, staffInfoForm);
    }

    @ApiOperation(value = "퇴사처리")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유 번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "success-resign // not-exits-staff-info")
    })
    @PatchMapping("/admin/{staff-num}")
    public HttpBodyMessage resign(@PathVariable("staff-num") Long staffNum){
        return service.resign(staffNum);
    }

    @ApiOperation(value = "정보삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유 번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "success-remove // not-exits-staff-info")
    })
    @DeleteMapping("/admin/{staff-num}")
    public HttpBodyMessage remove(@PathVariable("staff-num") Long staffNum){
        return service.remove(staffNum);
    }
}
