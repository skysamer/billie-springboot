package com.lab.smartmobility.billie.controller.vehicle;

import com.lab.smartmobility.billie.dto.vehicle.VehicleReturnDTO;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.lab.smartmobility.billie.service.vehicle.VehicleReturnService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Api(tags = {"차량 반납 api"})
@RequestMapping("/vehicle")
@RestController
public class VehicleReturnController {
    private final VehicleReturnService service;

    @PostMapping(value = "/insert-return", consumes = {"multipart/form-data"})
    @ApiOperation(value = "차량 반납")
    @ApiResponses({
            @ApiResponse(code = 200, message = "저장 실패 or 반납 이력 저장 완료")
    })
    public HttpBodyMessage returnVehicle(@ApiParam(value = "반납 신청 폼 데이터") @Valid @RequestPart(value = "returnVehicle") VehicleReturnDTO vehicleReturnDTO){
        if(service.returnVehicle(vehicleReturnDTO)==9999){
            return new HttpBodyMessage("fail", "저장 실패");
        }
        return new HttpBodyMessage("success", "반납 이력 저장 완료");
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
    public HttpBodyMessage getReturnCount(@PathVariable("disposal-info") int disposalInfo,
                                          @PathVariable("vehicle-num") Long vehicleNum,
                                          @PathVariable("base-date") String baseDate){
        return new HttpBodyMessage("count", service.getReturnCount(disposalInfo, vehicleNum, baseDate));
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
