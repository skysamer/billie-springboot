package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.ImageVehicle;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(readOnly = true)
public interface ReturnVehicleImageRepository extends JpaRepository<ImageVehicle, Long> {
    List<ImageVehicle> findAllByVehicleReservation(VehicleReservation vehicleReservation);
}
