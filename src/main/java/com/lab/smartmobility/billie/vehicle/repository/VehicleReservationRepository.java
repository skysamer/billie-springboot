package com.lab.smartmobility.billie.vehicle.repository;

import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.vehicle.domain.VehicleReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface VehicleReservationRepository extends JpaRepository<VehicleReservation, Long> {
    VehicleReservation findByRentNum(Long rentNum);
    VehicleReservation findByRentNumAndReturnStatusCode(Long rentNum, int returnStatusCode);
    List<VehicleReservation> findAllByRentedAt(LocalDateTime today);
    List<VehicleReservation> findByStaffAndReturnStatusCodeOrderByRentedAt(Staff staff, int returnStatusCode);
    void deleteByRentNum(Long rentNum);
}
