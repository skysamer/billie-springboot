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
@Transactional
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

            if(staff.getHireDate().equals(today)){
                staff.setVacationCount(totalVacationCount);
            }
        }
        log.info("직원 휴가개수 업데이트 완료");
    }

    @Scheduled(cron = "0 0 2 * * *")
    private void updateNewcomerVacationCount(){
        List<Staff> staffList = staffRepository.findAll();

        List<Staff> newcomerList = new ArrayList<>();
        for(Staff staff : staffList){
            long yearsOfService = ChronoUnit.YEARS.between(staff.getHireDate(), LocalDate.now());
            long period = ChronoUnit.MONTHS.between(staff.getHireDate(), LocalDate.now());

            if(yearsOfService < 1){
                newcomerList.add(staff);
            }
        }

        LocalDate today = LocalDate.now();
        for(Staff staff : newcomerList){
            LocalDate hireDate = staff.getHireDate();
            Period period = Period.between(hireDate, today);
            if(period.getDays() == 0){
                staff.plusVacationCount();
            }
        }
        log.info("신입직원 휴가개수 업데이트 완료");
    }
}
