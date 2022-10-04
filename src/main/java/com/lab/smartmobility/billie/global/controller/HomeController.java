package com.lab.smartmobility.billie.global.controller;

import com.lab.smartmobility.billie.staff.repository.StaffOvertimeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"서버 통신 테스트를 위한 api"})
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    @ApiOperation(value = "서버 통신 테스트")
    public String index(){
        return "hello billie";
    }
}
