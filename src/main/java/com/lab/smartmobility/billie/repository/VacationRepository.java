package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.Vacation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long> {
    List<Vacation> findAllByStaffAndStartDateBetweenOrderByStartDateDesc(Staff staff, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<Vacation> findAllByStaffAndApprovalStatusAndStartDateBetweenOrderByStartDateDesc(Staff staff, char approvalStatus, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Vacation findByVacationNum(Long vacationNum);
    void deleteByVacationNum(Long VacationNum);
    List<Vacation> findAllByApprovalStatusAndStartDateBetween(char approveStatus, LocalDate startDate, LocalDate endDate);
    List<Vacation> findAllByOrderByStartDateDesc(Pageable pageable);
}
