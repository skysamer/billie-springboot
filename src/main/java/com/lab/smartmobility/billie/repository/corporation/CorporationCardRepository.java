package com.lab.smartmobility.billie.repository.corporation;

import com.lab.smartmobility.billie.entity.corporation.CorporationCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CorporationCardRepository extends JpaRepository<CorporationCard, Long> {
    CorporationCard findByCardId(Long cardId);
    CorporationCard findByCardNameAndCompany(String cardName, String company);
}
