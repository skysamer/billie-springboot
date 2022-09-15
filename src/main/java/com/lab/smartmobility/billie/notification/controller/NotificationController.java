package com.lab.smartmobility.billie.notification.controller;

import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.notification.domain.Notification;
import com.lab.smartmobility.billie.notification.service.NotificationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = {"알림 api"})
@RequestMapping("/notification/*")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{staff-num}")
    @ApiOperation(value = "나의 알림 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 404, message = "조건에 맞는 데이터 없음")
    })
    public ResponseEntity<List<Notification>> getMyNotificationList(@PathVariable(value = "staff-num") Long staffNum){
        List<Notification> notificationList = notificationService.getMyNotificationList(staffNum);
        if(notificationList.size() == 0){
            return new ResponseEntity<>(notificationList, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }

    @DeleteMapping("/get/{id}")
    @ApiOperation(value = "개별 알림 확인 (확인 즉시 삭제됨)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "알림 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "알림 확인 및 삭제 성공"),
            @ApiResponse(code = 404, message = "알림 데이터를 찾을 수 없음")
    })
    public ResponseEntity<HttpBodyMessage> readAndDelete(@PathVariable Long id){
        HttpBodyMessage bodyMessage = notificationService.readAndDelete(id);
        if(bodyMessage.getCode().equals("fail")){
            return new ResponseEntity<>(bodyMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bodyMessage, HttpStatus.OK);
    }

    @DeleteMapping("/{staff-num}")
    @ApiOperation(value = "나에게 수신된 전체 알림 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "staff-num", value = "직원 고유 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "전체 알림 삭제 완료"),
    })
    public ResponseEntity<HttpBodyMessage> removeAll(@PathVariable("staff-num") Long staffNum){
        HttpBodyMessage bodyMessage = notificationService.removeAll(staffNum);
        return new ResponseEntity<>(bodyMessage, HttpStatus.OK);
    }
}
