package com.lab.smartmobility.billie.traffic.repository;

import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.traffic.domain.TrafficCard;
import com.lab.smartmobility.billie.traffic.domain.TrafficCardReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface TrafficCardReservationRepository extends JpaRepository<TrafficCardReservation, Long> {
    List<TrafficCardReservation> findAllByRentedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    TrafficCardReservation findByReservationNum(Long reservationNum);
    List<TrafficCardReservation> findAllByRentedAt(LocalDateTime now);
    List<TrafficCardReservation> findByStaffAndReturnStatusOrderByRentedAt(Staff staff, int returnStatus);
    void deleteByReservationNum(Long reservationNum);
    long countByTrafficCardAndReturnStatusAndRentedAtLessThanAndReturnedAtGreaterThan(TrafficCard card, int returnStatus, LocalDateTime rentedAt, LocalDateTime returnedAt);
    long countByReservationNumNotAndReturnStatusAndTrafficCardAndRentedAtLessThanAndReturnedAtGreaterThan(Long reservationNum, int returnStatus, TrafficCard card, LocalDateTime rentedAt, LocalDateTime returnedAt);
}
