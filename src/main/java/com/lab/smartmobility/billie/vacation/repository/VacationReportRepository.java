package com.lab.smartmobility.billie.vacation.repository;

import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.vacation.domain.VacationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional(readOnly = true)
public interface VacationReportRepository extends JpaRepository<VacationReport, Long> {
    VacationReport findByStartDateAndEndDateAndStaff(LocalDate startDate, LocalDate endDate, Staff staff);
}
