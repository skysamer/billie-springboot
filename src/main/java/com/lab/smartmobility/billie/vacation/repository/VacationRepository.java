package com.lab.smartmobility.billie.vacation.repository;

import com.lab.smartmobility.billie.vacation.domain.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface VacationRepository extends JpaRepository<Vacation, Long> {
    Vacation findByVacationId(Long vacationId);
    List<Vacation> findByVacationIdIn(List<Long> vacationIdList);
}
