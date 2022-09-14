package com.lab.smartmobility.billie.corporation.repository;

import com.lab.smartmobility.billie.corporation.domain.CorporationCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CorporationCardRepository extends JpaRepository<CorporationCard, Long> {
    CorporationCard findByCardId(Long cardId);
    CorporationCard findByCardNameAndCompany(String cardName, String company);
}
