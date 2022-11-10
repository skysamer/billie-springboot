package com.lab.smartmobility.billie.util;

import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vacation.service.VacationCalculateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class SchedulerTest {
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    VacationCalculateService calculateService;

    @Test
    void test(){
        List<Staff> staffList = staffRepository.findAll();

        for(Staff staff : staffList){
            LocalDate today = LocalDate.of(2022, 11, 8);
            double totalVacationCount = calculateService.calculateTotalVacationCount(staff);

            if(staff.getHireDate().getMonth().equals(today.getMonth()) && staff.getHireDate().getDayOfMonth() == today.getDayOfMonth()){
                System.out.println("휴가개수가 업데이트된 직원 이름 = " + staff.getName());
                staff.giveVacation(totalVacationCount);
                staffRepository.save(staff);
            }
        }
        System.out.println("직원 휴가개수 업데이트 완료");
    }
}
