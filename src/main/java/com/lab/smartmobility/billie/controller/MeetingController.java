package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.dto.ApplyMeetingForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Meeting;
import com.lab.smartmobility.billie.service.MeetingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Api(tags = {"회의실 예약을 위한 api"})
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/meeting/*")
@RestController
public class MeetingController {
    private final Log log = LogFactory.getLog(getClass());

    private final MeetingService meetingService;

    @PostMapping("/insert")
    @ApiOperation(value = "회의실 예약 등록")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회의 등록 실패 or 회의 등록 완료")
    })
    public HttpMessage insertMeeting(@RequestBody ApplyMeetingForm applyMeetingForm){
        Meeting newMeeting=meetingService.insertMeeting(applyMeetingForm);

        if(newMeeting==null){
            return new HttpMessage("fail", "회의 등록 실패");
        }
        return new HttpMessage("success", "회의 등록 완료");
    }

    @GetMapping("/{meeting-num}")
    @ApiOperation(value = "단일 회의실 예약 조회")
    public Meeting getMeeting(@PathVariable("meeting-num") Long meetingNum){
        return meetingService.getMeeting(meetingNum);
    }

    @GetMapping("/week-list")
    @ApiOperation(value = "주 단위 회의실 예약 목록 조회")
    public List<Meeting> getMeetingList(){
        return meetingService.getMeetingList();
    }

    @PutMapping("/{meeting-num}")
    @ApiOperation(value = "개별 회의실 예약 정보 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "수정 실패 or 수정 성공")
    })
    public HttpMessage modifyMeeting(@RequestBody ApplyMeetingForm applyMeetingForm, @PathVariable("meeting-num") Long meetingNum){
        if(meetingService.updateMeeting(meetingNum, applyMeetingForm)==9999){
            return new HttpMessage("fail", "수정 실패");
        }
        return new HttpMessage("success", "수정 성공");
    }

    @DeleteMapping("/{meeting-num}")
    @ApiOperation(value = "회의실 예약 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제 오류 or 삭제가 완료되었습니다")
    })
    public HttpMessage removeMeeting(@PathVariable("meeting-num") Long meetingNum){
        int deleteConfirm=meetingService.removeMeeting(meetingNum);

        if(deleteConfirm==1){
            return new HttpMessage("fail", "삭제 오류");
        }
        return new HttpMessage("success", "삭제가 완료되었습니다");
    }

    @GetMapping("/today-tomorrow-list")
    @ApiOperation(value = "오늘과 내일의 회의실 예약 목록 조회")
    public List<List<Meeting>> todayTomorrowMeetingList(){
        return meetingService.todayTomorrowMeetingList();
    }

    @GetMapping("/{start-date}/{end-date}")
    @ApiOperation(value = "이번달 회의실 예약 목록 조회")
    public List<Meeting> getMonthMeetingList(@PathVariable("start-date") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startDate,
                                             @PathVariable("end-date") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate endDate){
        return meetingService.getMonthMeetingList(startDate, endDate);
    }

}
