package com.lab.smartmobility.billie.vacation.service;

import com.lab.smartmobility.billie.vacation.dto.VacationCalendarForm;
import com.lab.smartmobility.billie.vacation.repository.VacationCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationCalendarService {
    private final VacationCalendarRepository calendarRepository;

    /*승인된 휴가 내역 월별 조회*/
    public List<VacationCalendarForm> getCalendarList(LocalDate startDate, LocalDate endDate){
        return calendarRepository.getCalendarList(startDate, endDate);
    }
}
