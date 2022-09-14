package com.lab.smartmobility.billie.corporation.repository;

import com.lab.smartmobility.billie.corporation.domain.ExpenseCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ExpenseCaseRepository extends JpaRepository<ExpenseCase, Long> {
}
