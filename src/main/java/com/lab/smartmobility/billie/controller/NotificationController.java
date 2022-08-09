package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Api(tags = {"알림 api"})
@RequestMapping("/notification/*")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/list/{staff-num}")
    @ApiOperation(value = "나의 알림 목록 조회")
    public List<Notification> getMyNotificationList(@PathVariable(value = "staff-num") Long staffNum){
        return notificationService.getMyNotificationList(staffNum);
    }

    @GetMapping("/get/{notification-num}")
    @ApiOperation(value = "개별 알림 확인")
    public HttpBodyMessage updateIsRead(@PathVariable("notification-num") Long notificationNum){
        int isReadCheck= notificationService.updateIsRead(notificationNum);

        if(isReadCheck==9999){
            return new HttpBodyMessage("fail", "서버오류");
        }
        return new HttpBodyMessage("success", "성공");
    }

}
