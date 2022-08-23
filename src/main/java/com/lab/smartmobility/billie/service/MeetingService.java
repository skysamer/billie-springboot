package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.ApplyMeetingForm;
import com.lab.smartmobility.billie.entity.Meeting;
import com.lab.smartmobility.billie.repository.MeetingRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final ModelMapper modelMapper;
    private final StaffRepository staffRepository;
    private final Log log;

    /*회의 등록*/
    public int insertMeeting(ApplyMeetingForm applyMeetingForm){
        if(checkIsDuplicate(-1L, applyMeetingForm.getDate(), applyMeetingForm.getEndTime(), applyMeetingForm.getStartTime())){
            return 500;
        }

        Meeting newMeeting = modelMapper.map(applyMeetingForm, Meeting.class);
        newMeeting.insertRenderInfo(staffRepository.findByStaffNum(applyMeetingForm.getStaffNum()));
        meetingRepository.save(newMeeting);
        return 0;
    }

    /*예약시간이 겹치는지 체크*/
    public boolean checkIsDuplicate(Long meetingNum, LocalDate date, LocalTime endTime, LocalTime startTime){
        if(meetingNum == -1L){
            return meetingRepository.countByDateAndStartTimeLessThanAndEndTimeGreaterThan(date, endTime, startTime) == 1;
        }
        return meetingRepository.countByDateAndMeetingNumNotAndStartTimeLessThanAndEndTimeGreaterThan(date, meetingNum, endTime, startTime) == 1;
    }

    /*개별 회의 조회*/
    public Meeting getMeeting(Long meetingNum){
        return meetingRepository.findByMeetingNum(meetingNum);
    }

    /*회의실 예약 수정*/
    public int updateMeeting(Long meetingNum, ApplyMeetingForm applyMeetingForm){
        try{
            if(checkIsDuplicate(meetingNum, applyMeetingForm.getDate(), applyMeetingForm.getEndTime(), applyMeetingForm.getStartTime())){
                return 500;
            }

            Meeting modifiedMeeting = meetingRepository.findByMeetingNum(meetingNum);
            modelMapper.map(applyMeetingForm, modifiedMeeting);
        }catch (Exception e){
            log.error(e);
            return 9999;
        }
        return 0;
    }

    // 이번주 회의 목록 조회(일요일 ~ 토요일)
    public List<Meeting> getMeetingList(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c=Calendar.getInstance();

        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        String sunday=sdf.format(c.getTime());
        LocalDate startDate=LocalDate.parse(sunday, DateTimeFormatter.ISO_DATE);

        c.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
        String saturday=sdf.format(c.getTime());
        LocalDate endDate=LocalDate.parse(saturday, DateTimeFormatter.ISO_DATE);

        return meetingRepository.findByDateBetween(startDate, endDate);
    }

    // 월단위 회의실 예약 목록
    public List<Meeting> getMonthMeetingList(LocalDate startDate, LocalDate endDate){
        return meetingRepository.findByDateBetween(startDate, endDate);
    }

    // 회의 삭제
    public int removeMeeting(Long meetingNum){
        meetingRepository.deleteByMeetingNum(meetingNum);
        if(meetingRepository.findByMeetingNum(meetingNum) == null){
            return 0;
        }
        return 1;
    }

    // 오늘과 내일의 회의 목록 조회(디폴트 조회)
    public List<List<Meeting>> todayTomorrowMeetingList(){
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Meeting> todayList = meetingRepository.findByDate(today);
        List<Meeting> tomorrowList = meetingRepository.findByDate(tomorrow);

        List<List<Meeting>> todayTomorrowList = new ArrayList<>();

        todayTomorrowList.add(todayList);
        todayTomorrowList.add(tomorrowList);

        return todayTomorrowList;
    }

}
