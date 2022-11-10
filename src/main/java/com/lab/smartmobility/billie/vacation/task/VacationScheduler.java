package com.lab.smartmobility.billie.vacation.task;

import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vacation.service.VacationCalculateService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VacationScheduler {
    private final StaffRepository staffRepository;
    private final VacationCalculateService calculateService;
    private final Log log = LogFactory.getLog(getClass());

    @Scheduled(cron = "0 0 1 * * *")

    private void updateVacationCount(){
        List<Staff> staffList = staffRepository.findAll();

        for(Staff staff : staffList){
            LocalDate today = LocalDate.now();
            double totalVacationCount = calculateService.calculateTotalVacationCount(staff);

            if(staff.getHireDate().getMonth().equals(today.getMonth()) && staff.getHireDate().getDayOfMonth() == today.getDayOfMonth()){
                log.info("휴가개수가 업데이트된 직원 이름 = " + staff.getName());
                staff.giveVacation(totalVacationCount);
                staffRepository.save(staff);
            }
        }
        log.info("직원 휴가개수 업데이트 완료");
    }
}
