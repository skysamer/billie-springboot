package com.lab.smartmobility.billie.overtime.task;

import com.lab.smartmobility.billie.staff.repository.StaffOvertimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class OvertimeHourInitializer {
    private StaffOvertimeRepository staffOvertimeRepository;

    @Scheduled(cron = "0 0 3 1 * *")
    public void initialize(){
        staffOvertimeRepository.initializeOvertimeHour();
    }
}
