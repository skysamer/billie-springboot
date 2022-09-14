package com.lab.smartmobility.billie.meeting.repository;

import com.lab.smartmobility.billie.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Transactional(readOnly = true)
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Meeting findByMeetingNum(Long meetingNum);

    void deleteByMeetingNum(Long meetingNum);
    List<Meeting> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Meeting> findByDate(LocalDate date);
    long countByDateAndStartTimeLessThanAndEndTimeGreaterThan(LocalDate date, LocalTime endTime, LocalTime startTime);
    long countByDateAndMeetingNumNotAndStartTimeLessThanAndEndTimeGreaterThan(LocalDate date, Long meetingNum, LocalTime endTime, LocalTime startTime);
}
