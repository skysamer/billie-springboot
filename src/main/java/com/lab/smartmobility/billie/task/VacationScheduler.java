package com.lab.smartmobility.billie.task;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
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
            if(staff.getHireDate().isEqual(LocalDate.now()) && LocalDate.now().isAfter(staff.getHireDate().plusYears(1))){
                staff.setVacationCount(15);
                staffRepository.save(staff);
            }
        }
        log.info("직원 휴가개수 업데이트 완료");
    }

    @Scheduled(cron = "0 0 2 1 * *")
    private void updateVacationCountForNewRecruit(){
        List<Staff> staffList=staffRepository.findAll();

        for(Staff staff : staffList){
            if(LocalDate.now().isBefore(staff.getHireDate().plusYears(1))){
                double vacationCount=staff.getVacationCount();
                staff.setVacationCount(vacationCount + 1);
                staffRepository.save(staff);
            }
        }
        log.info("1년차 미만 신입 직원 휴가개수 업데이트 완료");
    }

}
