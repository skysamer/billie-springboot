package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface VehicleReservationRepository extends JpaRepository<VehicleReservation, Long> {
    List<VehicleReservation> findAllByRentedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<VehicleReservation> findAllByReturnStatusCode(int returnStatusCode);
    Set<VehicleReservation> findAllByVehicle(Vehicle vehicle);
    VehicleReservation findByRentNum(Long rentNum);
    VehicleReservation findByRentNumAndReturnStatusCode(Long rentNum, int returnStatusCode);
    List<VehicleReservation> findAllByRentedAt(LocalDateTime today);
    int countByReturnStatusCode(int returnStatusCode);
    List<VehicleReservation> findByStaffAndReturnStatusCodeOrderByRentedAt(Staff staff, int returnStatusCode);
    void deleteByRentNum(Long rentNum);
}
