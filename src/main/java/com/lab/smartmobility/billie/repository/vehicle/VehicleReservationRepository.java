package com.lab.smartmobility.billie.repository.vehicle;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface VehicleReservationRepository extends JpaRepository<VehicleReservation, Long> {
    List<VehicleReservation> findAllByRentedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    VehicleReservation findByRentNum(Long rentNum);
    VehicleReservation findByRentNumAndReturnStatusCode(Long rentNum, int returnStatusCode);
    List<VehicleReservation> findAllByRentedAt(LocalDateTime today);
    List<VehicleReservation> findByStaffAndReturnStatusCodeOrderByRentedAt(Staff staff, int returnStatusCode);
    void deleteByRentNum(Long rentNum);
    long countByVehicleAndReturnStatusCodeAndRentedAtLessThanAndReturnedAtGreaterThan(Vehicle vehicle, int returnStatusCode, LocalDateTime rentedAt, LocalDateTime returnedAt);
    long countByRentNumNotAndVehicleAndReturnStatusCodeAndRentedAtLessThanAndReturnedAtGreaterThan(Long rentNum, Vehicle vehicle, int returnStatusCode, LocalDateTime rentedAt, LocalDateTime returnedAt);
}
