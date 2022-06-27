package com.lab.smartmobility.billie.repository.traffic;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.TrafficCard;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
import com.lab.smartmobility.billie.entity.Vehicle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrafficCardReservationRepository extends JpaRepository<TrafficCardReservation, Long> {
    List<TrafficCardReservation> findAllByRentedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    TrafficCardReservation findByReservationNum(Long reservationNum);
    List<TrafficCardReservation> findAllByRentedAt(LocalDateTime now);
    List<TrafficCardReservation> findByStaffAndReturnStatusOrderByRentedAt(Staff staff, int returnStatus);
    void deleteByReservationNum(Long reservationNum);
    long countByTrafficCardAndReturnStatusAndRentedAtLessThanAndReturnedAtGreaterThan(TrafficCard card, int returnStatus, LocalDateTime rentedAt, LocalDateTime returnedAt);
    long countByReservationNumNotAndReturnStatusAndTrafficCardAndRentedAtLessThanAndReturnedAtGreaterThan(Long reservationNum, int returnStatus, TrafficCard card, LocalDateTime rentedAt, LocalDateTime returnedAt);
}
