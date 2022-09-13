package com.lab.smartmobility.billie.meeting.service;

import com.lab.smartmobility.billie.meeting.dto.ApplyMeetingForm;
import com.lab.smartmobility.billie.meeting.repository.MeetingQueryRepository;
import com.lab.smartmobility.billie.meeting.repository.MeetingRepository;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.Meeting;
import com.lab.smartmobility.billie.user.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingQueryRepository meetingQueryRepository;
    private final ModelMapper modelMapper;
    private final StaffRepository staffRepository;
    private final Log log;

    /*회의 등록*/
    public HttpBodyMessage insertMeeting(ApplyMeetingForm applyMeetingForm){
        boolean isDuplicated = meetingQueryRepository.checkIsDuplicate(-1L, applyMeetingForm.getDate(),
                applyMeetingForm.getEndTime(), applyMeetingForm.getStartTime());
        if(isDuplicated){
            return new HttpBodyMessage("fail", "The time has already been reserved");
        }

        Meeting newMeeting = modelMapper.map(applyMeetingForm, Meeting.class);
        newMeeting.insertRenderInfo(staffRepository.findByStaffNum(applyMeetingForm.getStaffNum()));
        meetingRepository.save(newMeeting);
        return new HttpBodyMessage("success", "reservation complete");
    }

    /*개별 회의 조회*/
    public Meeting getMeeting(Long meetingNum){
        return meetingRepository.findByMeetingNum(meetingNum);
    }

    /*회의실 예약 수정*/
    public HttpBodyMessage updateMeeting(Long meetingNum, ApplyMeetingForm applyMeetingForm){
        boolean isDuplicated = meetingQueryRepository.checkIsDuplicate(-1L, applyMeetingForm.getDate(),
                applyMeetingForm.getEndTime(), applyMeetingForm.getStartTime());
        if(isDuplicated){
            return new HttpBodyMessage("fail", "The time has already been reserved");
        }

        Meeting modifiedMeeting = meetingRepository.findByMeetingNum(meetingNum);
        modelMapper.map(applyMeetingForm, modifiedMeeting);
        return new HttpBodyMessage("success", "reservation complete");
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
