package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Meeting findByMeetingNum(Long meetingNum);

    void deleteByMeetingNum(Long meetingNum);

    List<Meeting> findByDateBetween(LocalDate startDate, LocalDate endDate);

    Meeting save(Meeting meeting);

    List<Meeting> findByDate(LocalDate date);
}
