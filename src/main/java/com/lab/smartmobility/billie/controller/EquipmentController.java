package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.entity.Equipment;
import com.lab.smartmobility.billie.service.EquipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/equipment/*")
@Api(tags = {"기자재 관리 api"})
@RestController
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping("/list")
    @ApiOperation(value = "부서별 기자재 관리 목록 조회")
    public List<Equipment> getEquipmentList(@RequestBody Map<String, String> param){
        return equipmentService.getEquipmentList(param.get("department"));
    }
}
