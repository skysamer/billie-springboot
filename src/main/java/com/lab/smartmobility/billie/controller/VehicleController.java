package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.dto.vehicle.ApplyRentalVehicleDTO;
import com.lab.smartmobility.billie.dto.vehicle.VehicleDTO;
import com.lab.smartmobility.billie.dto.vehicle.VehicleReturnDTO;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.lab.smartmobility.billie.service.VehicleService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Api(tags = {"차량 예약 및 등록 관련 api"})
@RequestMapping("/vehicle")
@RestController
public class VehicleController {
    private final VehicleService service;
    private final Log log = LogFactory.getLog(getClass());

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
    public HttpMessage modifyVehicleInfo(@RequestBody Vehicle vehicle){
        if(service.modifyVehicleInfo(vehicle)==9999){
            return new HttpMessage("fail", "수정 실패");
        }
        return new HttpMessage("success", "수정 성공");
    }

    @DeleteMapping("/{vehicle-num}")
    @ApiOperation(value = "차량 정보 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 성공 or 삭제 실패")
    })
    public HttpMessage removeVehicleInfo(@PathVariable("vehicle-num") Long vehicleNum){
        if(service.removeVehicleInfo(vehicleNum)==9999){
            return new HttpMessage("fail", "삭제 실패");
        }
        return new HttpMessage("success", "삭제 성공");
    }

    @PutMapping("/discard/{vehicle-num}")
    @ApiOperation(value = "차량 폐기", notes = "body 값에  'reason' : '폐기 사유'로 매핑")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이미 폐기된 차량입니다. or 폐기 성공")
    })
    public HttpMessage discardVehicle(@PathVariable("vehicle-num") Long vehicleNum,
                                      @ApiParam(value = "'reason' : '폐기 사유'") @RequestBody HashMap<String, String> reason){
        if(service.discardVehicle(vehicleNum, reason)==500){
            return new HttpMessage("fail", "이미 폐기된 차량입니다.");
        }
        return new HttpMessage("success", "폐기 성공");
    }

    @PostMapping("/apply-rental")
    @ApiOperation(value = "차량 이용 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "차량 대여 신청 실패 or 해당 날짜에 차량이 이미 대여중입니다 or 현재 시각보다 과거로 예약할 수 없습니다 or 대여 성공")
    })
    public HttpMessage applyForRent(@Valid @RequestBody ApplyRentalVehicleDTO rentalVehicleDTO){
        int checkApplyRental=service.applyForRent(rentalVehicleDTO);

        if(checkApplyRental==9999){
            return new HttpMessage("fail", "차량 대여 신청 실패");
        } else if(checkApplyRental==500){
            return new HttpMessage("fail", "해당 날짜에 차량이 이미 대여중입니다");
        } else if(checkApplyRental==400){
            return new HttpMessage("fail", "현재 시각보다 과거로 예약할 수 없습니다");
        }
        return new HttpMessage("success", "대여 성공");
    }

    @GetMapping("/reservation-list/{startDate}/{endDate}")
    @ApiOperation(value = "월 단위 차량 예약 목록 조회")
    public List<VehicleReservation> reservationList(@ApiParam(value = "yyyy-MM-dd") @PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                    @ApiParam(value = "yyyy-MM-dd") @PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return service.reservationList(startDate, endDate);
    }

    @GetMapping("/reservation/{rent-num}")
    @ApiOperation(value = "개별 차량 예약 상세조회")
    public VehicleReservation getReservate(@PathVariable("rent-num") Long rentNum){
        return service.getReservation(rentNum);
    }

    @PutMapping("/modify/{rent-num}")
    @ApiOperation(value = "개별 차량 예약 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "현재 시각보다 과거로 예약할 수 없습니다 or 해당 날짜에 차량이 이미 대여중입니다 or 대여자 정보가 일치하지 않습니다 or 대여시작시간 이후에는 변경할 수 없습니다 or 예약 수정 실패 or 예약 수정 성공")
    })
    public HttpMessage modify(@PathVariable("rent-num") Long rentNum, @RequestBody ApplyRentalVehicleDTO applyRentalVehicleDTO){
        int isUpdated=service.modifyVehicleReservation(rentNum, applyRentalVehicleDTO);

        if(isUpdated==400){
            return new HttpMessage("fail", "현재 시각보다 과거로 예약할 수 없습니다");
        }else if(isUpdated==500){
            return new HttpMessage("fail", "해당 날짜에 차량이 이미 대여중입니다");
        }else if(isUpdated==300){
            return new HttpMessage("fail", "대여자 정보가 일치하지 않습니다");
        }else if(isUpdated==303){
            return new HttpMessage("fail", "대여시작시간 이후에는 변경할 수 없습니다");
        }else if(isUpdated==9999){
            return new HttpMessage("fail", "예약 수정 실패");
        }
        return new HttpMessage("success", "예약 수정 성공");
    }

    @DeleteMapping("/remove/{rent-num}")
    @ApiOperation(value = "개별 차량 예약 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 실패 or 삭제 성공 or 삭제 불가")
    })
    public HttpMessage remove(@PathVariable("rent-num") Long rentNum){
        int isDeleted=service.removeReservationInfo(rentNum);

        if(isDeleted==9999){
            return new HttpMessage("fail", "삭제 실패");
        }else if(isDeleted==500){
            return new HttpMessage("fail", "삭제 불가");
        }
        return new HttpMessage("success", "삭제 성공");
    }

    @GetMapping("/my/{staff-num}")
    @ApiOperation(value = "나의 차량 예약 현황 조회", notes = "대여이력이 없을경우, 공백의 list 반환")
    public List<VehicleReservation> getMyReservationList(@PathVariable("staff-num") Long staffNum){
        return service.getMyReservation(staffNum);
    }

    @PostMapping(value = "/insert-return", consumes = {"multipart/form-data"})
    @ApiOperation(value = "차량 반납 신청 및 이미지 파일 등록", notes = "반납 신청 퐁 데이터는 returnVehicle, 이미지 파일은 imageFiles을 key로 하여 전달")
    @ApiResponses({
            @ApiResponse(code = 200, message = "저장 실패 or 반납 이력 저장 완료")
    })
    public HttpMessage returnVehicle(@ApiParam(value = "반납 신청 폼 데이터") @Valid @RequestPart(value = "returnVehicle") VehicleReturnDTO vehicleReturnDTO,
                                     @ApiParam(value = "이미지 파일") @PathVariable("imageFiles") List<MultipartFile> imageFiles){
        if(service.returnVehicle(vehicleReturnDTO, imageFiles)==9999){
            return new HttpMessage("fail", "저장 실패");
        }
        return new HttpMessage("success", "반납 이력 저장 완료");
    }

    @GetMapping("/return-list/{disposal-info}/{vehicle-num}/{base-date}")
    @ApiOperation(value = "차량 반납 이력 전체 조회", notes = "전체 차량 조회의 경우 -1 // 폐기정보(0:미포함, 1:포함) // base-date : yyyy-MM")
    public List<VehicleReservation> getReturnList(@PathVariable("disposal-info") int disposalInfo,
            @PathVariable("vehicle-num") Long vehicleNum,
            @PathVariable("base-date") String baseDate,
            @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return service.getReturnList(disposalInfo, vehicleNum, baseDate, pageRequest);
    }

    @GetMapping("/return-count/{disposal-info}/{vehicle-num}/{base-date}")
    @ApiOperation(value = "반납이력 전체 개수 조회", notes = "전체 차량 조회의 경우 -1 // 폐기정보(0:미포함, 1:포함) // base-date : yyyy-MM")
    public HttpMessage getReturnCount(@PathVariable("disposal-info") int disposalInfo,
                                      @PathVariable("vehicle-num") Long vehicleNum,
                                      @PathVariable("base-date") String baseDate){
        return new HttpMessage("count", service.getReturnCount(disposalInfo, vehicleNum, baseDate));
    }

    @GetMapping("/return/{rent-num}")
    @ApiOperation(value = "차량 반납 이력 상세 조회", notes = "{count : 전체개수}")
    public VehicleReservation getReturn(@PathVariable("rent-num") Long rentNum){
        return service.getReturn(rentNum);
    }

    @GetMapping("/return-image/{rent-num}")
    @ApiOperation(value = "반납 이력 별 이미지 파일 조회", notes = "각각의 이미지 파일이 byte 배열로 변환되어 리턴")
    public List<byte[]> returnImage(@PathVariable("rent-num") Long rentNum){
        return service.getReturnImages(rentNum);
    }

    @GetMapping("/excel/{disposal-info}/{vehicle-num}/{base-date}")
    @ApiOperation(value = "차량 반납 이력 엑셀 다운로드", notes = "전체 차량 조회의 경우 -1 // 폐기정보(0:미포함, 1:포함) // base-date : yyyy-MM", response = byte.class)
    public void excelDownload(@PathVariable("disposal-info") int disposalInfo,
                              @PathVariable("vehicle-num") Long vehicleNum,
                              @PathVariable("base-date") String baseDate, HttpServletResponse response) throws IOException {
        Workbook wb=service.excelDownload(disposalInfo, vehicleNum, baseDate);

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename="+baseDate+"_vehicle_history.xlsx");

        wb.write(response.getOutputStream());
        wb.close();
    }
}
