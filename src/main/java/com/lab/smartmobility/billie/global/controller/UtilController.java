package com.lab.smartmobility.billie.global.controller;

import com.lab.smartmobility.billie.global.dto.NameDropdownForm;
import com.lab.smartmobility.billie.global.service.UtilService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = {"유틸 api (드롭다운 등...)"})
@RequiredArgsConstructor
public class UtilController {
    private final UtilService service;

    @ApiOperation(value = "부서장의 휴가승인 요청 목록 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공")
    })
    @GetMapping("/dropdown/name")
    public ResponseEntity<List<NameDropdownForm>> getNameList(){
        List<NameDropdownForm> nameList = service.getNameList();
        return new ResponseEntity<>(nameList, HttpStatus.OK);
    }
}
