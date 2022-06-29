package com.lab.smartmobility.billie.repository.corporation;

import com.lab.smartmobility.billie.entity.corporation.CorporationCardUseCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CorporationCardUseCaseRepository extends JpaRepository<CorporationCardUseCase, Long> {
}
