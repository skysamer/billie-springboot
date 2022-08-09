package com.lab.smartmobility.billie.controller.traffic;

import com.lab.smartmobility.billie.dto.traffic.ReturnTrafficCardDTO;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
import com.lab.smartmobility.billie.service.traffic.TrafficCardReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"교통카드 반납 api"})
@RequestMapping("/traffic-card")
public class TrafficCardReturnController {
    private final TrafficCardReturnService service;
    private final Log log= LogFactory.getLog(getClass());

    @PostMapping("/apply-return")
    @ApiOperation(value = "교통카드 반납 신청")
    @ApiResponses({
            @ApiResponse(code = 200, message = "반납 신청 실패 or 반납 신청 완료")
    })
    public HttpBodyMessage applyCardReturn(@Valid @RequestBody ReturnTrafficCardDTO returnTrafficCard){
        if(service.applyCardReturn(returnTrafficCard)==9999){
            return new HttpBodyMessage("fail", "반납 신청 실패");
        }
        return new HttpBodyMessage("success", "반납 신청 완료");
    }

    @GetMapping("/return-list/{disposal-info}/{card-num}/{base-date}")
    @ApiOperation(value = "교통카드 반납 목록 조회", notes = "전체 교통카드 조회의 경우 -1 // 폐기정보(0:미포함, 1:포함) // base-date : yyyy-MM")
    public List<TrafficCardReservation> getCardReturnList(@PathVariable("disposal-info") int disposalInfo,
                                                          @PathVariable("card-num") Long cardNum,
                                                          @PathVariable("base-date") String baseDate,
                                                          @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return service.getCardReturnList(disposalInfo, cardNum, baseDate, pageRequest);
    }

    @GetMapping("/return/{reservation-num}")
    @ApiOperation(value = "교통카드 반납 이력 상세 조회")
    public TrafficCardReservation getCardReturnHistory(@PathVariable("reservation-num") Long reservationNum){
        return service.getCardReturn(reservationNum);
    }

    @GetMapping("/return-count/{disposal-info}/{card-num}/{base-date}")
    @ApiOperation(value = "교통카드 반납 이력 개수 조회", notes = "전체 교통카드 조회의 경우 -1 // 폐기정보(0:미포함, 1:포함) // base-date : yyyy-MM")
    public HttpBodyMessage getReturnCount(@PathVariable("disposal-info") int disposalInfo,
                                          @PathVariable("card-num") Long cardNum,
                                          @PathVariable("base-date") String baseDate){
        return new HttpBodyMessage("count", service.getReturnCount(disposalInfo, cardNum, baseDate));
    }

    @GetMapping("/excel/{disposal-info}/{card-num}/{base-date}")
    @ApiOperation(value = "교통카드 반납 이력 엑셀 다운로드", notes = "전체 교통카드 조회의 경우 -1 // 폐기정보(0:미포함, 1:포함) // base-date : yyyy-MM")
    public void excelDownload(@PathVariable("disposal-info") int disposalInfo,
                              @PathVariable("card-num") Long cardNum,
                              @PathVariable("base-date") String baseDate, HttpServletResponse response) throws IOException {
        Workbook wb=service.excelDownload(disposalInfo, cardNum, baseDate);

        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename="+baseDate+"_traffic_card_history.xlsx");

        wb.write(response.getOutputStream());
        wb.close();
    }
}
