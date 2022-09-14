package com.lab.smartmobility.billie.repository.vacation;

import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface VacationRepository extends JpaRepository<Vacation, Long> {
    Vacation findByVacationId(Long vacationId);
    List<Vacation> findTop4ByStaffOrderByStartDate(Staff staff);
}
