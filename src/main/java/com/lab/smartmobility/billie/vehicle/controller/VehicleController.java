package com.lab.smartmobility.billie.vehicle.controller;

import com.lab.smartmobility.billie.vehicle.dto.NonBorrowableVehicle;
import com.lab.smartmobility.billie.vehicle.dto.VehicleDTO;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.vehicle.domain.Vehicle;
import com.lab.smartmobility.billie.vehicle.service.VehicleService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Api(tags = {"차량 CRUD api"})
@RequestMapping("/vehicle")
@RestController
public class VehicleController {
    private final VehicleService service;
    private final Log log;

    @GetMapping("/to-own")
    @ApiOperation(value = "전체 보유 차량 조회", notes = "차량 드롭다운 기능 구현 시에도 이 api를 사용해 주세요")
    public List<VehicleDTO> vehicleList(){
       return service.vehicleList();
    }

    @GetMapping("/{vehicle-num}")
    @ApiOperation(value = "개별 차량 정보 상세 조회")
    public Vehicle getVehicleInfo(@PathVariable("vehicle-num") Long vehicleNum){
        return service.getVehicleInfo(vehicleNum);
    }

    @PostMapping("/register")
    @ApiOperation(value = "신규 차량 등록", notes = "대여상태 및 차량 시퀀스 전송 필요없음")
    public Vehicle register(@RequestBody Vehicle vehicle){
        return service.register(vehicle);
    }

    @PutMapping("/modify")
    @ApiOperation(value = "차량 정보 수정", notes = "대여상태 전송 필요없음")
    @ApiResponses({
            @ApiResponse(code = 200, message = "수정 성공 or 수정 실패")
    })
    public HttpBodyMessage modifyVehicleInfo(@RequestBody Vehicle vehicle){
        if(service.modifyVehicleInfo(vehicle)==9999){
            return new HttpBodyMessage("fail", "수정 실패");
        }
        return new HttpBodyMessage("success", "수정 성공");
    }

    @DeleteMapping("/{vehicle-num}")
    @ApiOperation(value = "차량 정보 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 성공 or 삭제 실패")
    })
    public HttpBodyMessage removeVehicleInfo(@PathVariable("vehicle-num") Long vehicleNum){
        if(service.removeVehicleInfo(vehicleNum) == 9999){
            return new HttpBodyMessage("fail", "삭제 실패");
        }
        return new HttpBodyMessage("success", "삭제 성공");
    }

    @PutMapping("/discard/{vehicle-num}")
    @ApiOperation(value = "차량 폐기", notes = "body 값에  'reason' : '폐기 사유'로 매핑")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미 폐기된 차량입니다. or 폐기 성공")
    })
    public HttpBodyMessage discardVehicle(@PathVariable("vehicle-num") Long vehicleNum,
                                          @ApiParam(value = "'reason' : '폐기 사유'") @RequestBody HashMap<String, String> reason){
        if(service.discardVehicle(vehicleNum, reason) == 500){
            return new HttpBodyMessage("fail", "이미 폐기된 차량입니다.");
        }
        return new HttpBodyMessage("success", "폐기 성공");
    }

    @ApiOperation(value = "해당 예약 날짜에 빌릴수 없는 차량 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rented-at", value = "대여 시작 시간 (yyyy-MM-dd hh:mm)"),
            @ApiImplicitParam(name = "returned-at", value = "대여 종료 시간 (yyyy-MM-dd hh:mm)"),
            @ApiImplicitParam(name = "rent-num", value = "예약 번호 (신규 예약은 -1)")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 404, message = "모든 차량 대여 가능")
    })
    @GetMapping("/user/not-borrow/{rented-at}/{returned-at}/{rent-num}")
    public ResponseEntity<List<NonBorrowableVehicle>> getNonBorrowableVehicleList(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @PathVariable("rented-at") LocalDateTime rentedAt,
                                                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @PathVariable("returned-at") LocalDateTime returnedAt,
                                                                                  @PathVariable("rent-num") Long rentNum){
        List<NonBorrowableVehicle> nonBorrowableVehicleList = service.getBorrowableVehicleList(rentedAt, returnedAt, rentNum);
        if(nonBorrowableVehicleList.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(nonBorrowableVehicleList, HttpStatus.OK);
    }
}
