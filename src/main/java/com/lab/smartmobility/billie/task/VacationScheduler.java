package com.lab.smartmobility.billie.task;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.user.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VacationScheduler {
    private final StaffRepository staffRepository;
    private final Log log= LogFactory.getLog(getClass());

    @Scheduled(cron = "0 0 1 * * *")
    private void updateVacationCount(){
        List<Staff> staffList=staffRepository.findAll();

        for(Staff staff : staffList){
            long yearPeriod = ChronoUnit.YEARS.between(staff.getHireDate(), LocalDate.now());
            long monthPeriod = ChronoUnit.MONTHS.between(staff.getHireDate(), LocalDate.now());
            long dayPeriod = ChronoUnit.DAYS.between(staff.getHireDate(), LocalDate.now());

            if(monthPeriod == 1 && (dayPeriod == 30 || dayPeriod == 31)){
                staff.setVacationCount(11);
                staffRepository.save(staff);
            }else if(yearPeriod < 3 && (dayPeriod % 365 == 0)){
                staff.setVacationCount(15);
                staffRepository.save(staff);
            }else if(yearPeriod > 3 && (dayPeriod % 365 == 0)){
                int vacationCount = 15 + (int)((yearPeriod-3) / 2) + 1;
                staff.setVacationCount(vacationCount);
                staffRepository.save(staff);
            }
        }
        log.info("직원 휴가개수 업데이트 완료");
    }

}
