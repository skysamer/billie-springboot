package com.lab.smartmobility.billie.repository.corporation;

import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.corporation.CorporationCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findByStaffAndApplicationId(Staff staff, Long applicationId);
    Application findByApplicationId(Long applicationId);
    List<Application> findAllByStartDateAndStartTimeAndCorporationCardNotNull(LocalDate startDate, LocalTime startTime);
    List<Application> findAllByApprovalStatusAndStartDateBetween(char approvalStatus, LocalDate startDate, LocalDate endDate);
    List<Application> findAllByStaffAndIsReturned(Staff staff, int isReturned);
    List<Application> findAllByCorporationCardAndIsReturned(CorporationCard card, int isReturned);
}
