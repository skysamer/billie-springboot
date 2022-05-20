package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
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
    List<TrafficCardReservation> findAllByReturnStatusOrderByReturnedAtDesc(int returnStatus, Pageable pageable);
    List<TrafficCardReservation> findAllByRentedAt(LocalDateTime now);
    List<TrafficCardReservation> findAllByReturnStatus(int returnStatus);
    List<TrafficCardReservation> findByStaffAndReturnStatusOrderByRentedAt(Staff staff, int returnStatus);
    int countByReturnStatus(int returnStatus);
    void deleteByReservationNum(Long reservationNum);

    //@Query(value = "SELECT v FROM TrafficCardReservation v WHERE  date_of_rental <= :today and expected_return_date >= :today and staff_num = :staffNum and return_status= :status")
    //List<TrafficCardReservation> getMyCardReservationList(@Param("today") LocalDate today, @Param("staffNum") Long staffNum, @Param("status") int status);
}
