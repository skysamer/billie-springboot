package com.lab.smartmobility.billie.vacation.service;

import com.lab.smartmobility.billie.vacation.dto.MyVacationDTO;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationCalculateService {
    private final StaffRepository staffRepository;
    private final Log log;

    /*나의 남은 휴가 개수, 전체 개수, 사용개수 및 소진기한*/
    public MyVacationDTO getMyVacationInfo(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        if(staff == null){
            return null;
        }

        double totalVacationCount = calculateTotalVacationCount(staff);
        if(totalVacationCount > 25){
            totalVacationCount = 25;
        }

        int baseYear = calculateBaseYear(staff);

        return MyVacationDTO.builder()
                .remainingVacationCount(staff.getVacationCount())
                .totalVacationCount(totalVacationCount)
                .numberOfUses(totalVacationCount - staff.getVacationCount())
                .startDate(LocalDate.of(baseYear, staff.getHireDate().get(ChronoField.MONTH_OF_YEAR), staff.getHireDate().get(ChronoField.DAY_OF_MONTH)))
                .endDate(LocalDate.of(baseYear + 1, staff.getHireDate().get(ChronoField.MONTH_OF_YEAR), staff.getHireDate().get(ChronoField.DAY_OF_MONTH)).minusDays(1))
                .build();
    }

    /*직원 별 총 휴가개수 계산*/
    public double calculateTotalVacationCount(Staff staff){
        long yearsOfService = ChronoUnit.YEARS.between(staff.getHireDate(), LocalDate.now());

        if(yearsOfService < 1){
            return 11;
        }else if(yearsOfService < 3){
            return 15;
        }else{
            return 15 + (int)((yearsOfService - 3) / 2) + 1;
        }
    }

    private int calculateBaseYear(Staff staff){
        return ((LocalDate.of(LocalDate.now().get(ChronoField.YEAR),
                staff.getHireDate().get(ChronoField.MONTH_OF_YEAR),
                staff.getHireDate().get(ChronoField.DAY_OF_MONTH))).isAfter(LocalDate.now()))
                ? LocalDate.now().minusYears(1).getYear() : LocalDate.now().getYear();
    }

    /*소진된 휴가계산 (건별)*/
    public double calculateVacationCount(String vacationType, int period){
        if(vacationType.contains("반차")){
            return 0.5;
        }else if(vacationType.equals("경조") || vacationType.equals("공가")){
            return 0;
        }
        return period + 1;
    }

    public void restoreVacationCount(Staff applicant, String vacationType, int period){
        if(vacationType.contains("반차")){
            applicant.restoreVacationCount(0.5);
        }else if(vacationType.equals("경조") || vacationType.equals("공가")){
            applicant.restoreVacationCount(0);
        }else{
            applicant.restoreVacationCount(period + 1);
        }
    }

}
