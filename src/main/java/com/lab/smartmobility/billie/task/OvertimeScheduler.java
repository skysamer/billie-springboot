package com.lab.smartmobility.billie.task;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OvertimeScheduler {
    private final StaffRepository staffRepository;
    private final Log log= LogFactory.getLog(getClass());

    @Scheduled(cron = "0 0 3 1 * *")
    private void updateOvertimeCount(){
        List<Staff> staffList=staffRepository.findAll();

        for(Staff staff : staffList){
            staff.setOvertimeCount(0);
        }
        log.info("매월 직원 추가근무 시간 업데이트 완료");
    }

}