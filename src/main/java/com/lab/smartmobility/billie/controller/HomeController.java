package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"서버 통신 테스트를 위한 api"})
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/")
    @ApiOperation(value = "서버 통신 테스트")
    public String index(){
        return "hello billie";
    }

    @GetMapping("/test")
    public void test(){
        homeService.test();
    }
}
