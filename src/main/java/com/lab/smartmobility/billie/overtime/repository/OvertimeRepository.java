package com.lab.smartmobility.billie.overtime.repository;

import com.lab.smartmobility.billie.overtime.domain.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface OvertimeRepository extends JpaRepository<Overtime, Long> {
}
