package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.ImageVehicle;
import com.lab.smartmobility.billie.vehicle.domain.VehicleReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(readOnly = true)
public interface ReturnVehicleImageRepository extends JpaRepository<ImageVehicle, Long> {
    List<ImageVehicle> findAllByVehicleReservation(VehicleReservation vehicleReservation);
}
