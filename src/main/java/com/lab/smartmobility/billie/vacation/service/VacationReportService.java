package com.lab.smartmobility.billie.vacation.service;

import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.vacation.domain.Vacation;
import com.lab.smartmobility.billie.vacation.domain.VacationReport;
import com.lab.smartmobility.billie.vacation.dto.VacationReportForm;
import com.lab.smartmobility.billie.vacation.repository.VacationReportQueryRepository;
import com.lab.smartmobility.billie.vacation.repository.VacationReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationReportService {
    private final VacationReportRepository reportRepository;
    private final VacationReportQueryRepository reportQueryRepository;

    /*휴가 리포트 엔티티에 등록*/
    public void record(double count, Staff staff, Vacation vacation){
            VacationReport report = VacationReport.builder()
                    .count(count)
                    .startDate(vacation.getStartDate()).endDate(vacation.getEndDate())
                    .note(vacation.getVacationType()).staff(staff).reason(vacation.getReason())
                    .build();
            reportRepository.save(report);
    }

    /*휴가 내역 월별 리포트 조회*/
    public List<VacationReportForm> getReport(String baseDate, String department, String name){
        return reportQueryRepository.getReport(baseDate, department, name);
    }

    /*개인별 휴가데이터 월별 조회 (출력용)*/
    public List<VacationReportForm> getVacation(String baseDate, String name){
        return reportQueryRepository.getVacation(baseDate, name);
    }
}
