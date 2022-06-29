package com.lab.smartmobility.billie.repository.traffic;

import com.lab.smartmobility.billie.entity.TrafficCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TrafficCardRepository extends JpaRepository<TrafficCard, Long> {
    TrafficCard findByCardNum(Long cardNum);
    void deleteByCardNum(Long cardNum);

    @Modifying
    @Query(value = "UPDATE TrafficCard set balance = :balance where card_num = :cardNum")
    void changeBalance(@Param("balance") int balance, @Param("cardNum") Long cardNum) throws Exception;

    @Modifying
    @Query(value = "UPDATE TrafficCard set rental_status = :rentalStatus where card_num = :cardNum")
    void changeRentalStatus(@Param("rentalStatus") int rentalStatus, @Param("cardNum") Long cardNum) throws Exception;
}
