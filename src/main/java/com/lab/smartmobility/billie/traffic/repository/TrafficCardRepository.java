package com.lab.smartmobility.billie.traffic.repository;

import com.lab.smartmobility.billie.traffic.domain.TrafficCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TrafficCardRepository extends JpaRepository<TrafficCard, Long> {
    TrafficCard findByCardNum(Long cardNum);
    void deleteByCardNum(Long cardNum);
}
