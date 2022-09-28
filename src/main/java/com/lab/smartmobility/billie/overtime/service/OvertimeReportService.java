package com.lab.smartmobility.billie.overtime.service;

import com.lab.smartmobility.billie.overtime.dto.OvertimeReportForm;
import com.lab.smartmobility.billie.overtime.repository.OvertimeReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OvertimeReportService {
    private final OvertimeReportRepository reportRepository;

    /*추가근무 월별 리포트*/
    public List<OvertimeReportForm> getReport(String baseDate, String department, String name){
        return reportRepository.getReport(baseDate, department, name);
    }
}
