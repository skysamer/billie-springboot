package com.lab.smartmobility.billie.repository.corporation;

import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findByStaffAndApplicationId(Staff staff, Long applicationId);
    Application findByApplicationId(Long applicationId);
    List<Application> findAllByStartDateAndStartTimeAndCorporationCardNotNull(LocalDate startDate, LocalTime startTime);
    List<Application> findAllByApprovalStatusAndStartDateBetween(char approvalStatus, LocalDate startDate, LocalDate endDate);
    List<Application> findAllByStaffAndApprovalStatus(Staff staff, char approvalStatus);
}
