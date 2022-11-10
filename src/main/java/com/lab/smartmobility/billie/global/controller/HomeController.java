package com.lab.smartmobility.billie.global.controller;

import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffOvertimeRepository;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vacation.service.VacationCalculateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Api(tags = {"서버 통신 테스트를 위한 api"})
@RequiredArgsConstructor
public class HomeController {
    private final StaffRepository staffRepository;
    private final VacationCalculateService calculateService;
    private final Log log = LogFactory.getLog(getClass());

    @GetMapping("/")
    @ApiOperation(value = "서버 통신 테스트")
    public String index(){
        return "hello billie";
    }

    @GetMapping("/util-test")
    @ApiOperation(value = "서버 통신 테스트")
    public String utilTest(){
        List<Staff> staffList = staffRepository.findAll();

        for(Staff staff : staffList){
            LocalDate today = LocalDate.of(2022,11,4);
            double totalVacationCount = calculateService.calculateTotalVacationCount(staff);

            if(staff.getHireDate().getMonth().equals(today.getMonth()) && staff.getHireDate().getDayOfMonth() == today.getDayOfMonth()){
                log.info("휴가개수가 업데이트된 직원 이름 = " + staff.getName());
                staff.giveVacation(totalVacationCount);
                staffRepository.save(staff);
            }
        }
        log.info("직원 휴가개수 업데이트 완료");
        return "testComplete";
    }
}
