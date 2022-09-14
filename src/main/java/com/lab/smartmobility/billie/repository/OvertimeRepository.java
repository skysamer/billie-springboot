package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Overtime;
import com.lab.smartmobility.billie.staff.domain.Staff;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
public interface OvertimeRepository extends JpaRepository<Overtime, Long> {
    List<Overtime> findAllByStaffAndDateOfOvertimeBetweenOrderByOvertimeNumDesc(Staff staff, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<Overtime> findAllByStaffAndApprovalStatusAndDateOfOvertimeBetweenOrderByOvertimeNumDesc(Staff staff, char approvalStatus, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Overtime findByOvertimeNum(Long overtimeNum);
}
