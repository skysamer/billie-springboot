package com.lab.smartmobility.billie.vehicle.repository;

import com.lab.smartmobility.billie.vehicle.domain.Vehicle;
import com.lab.smartmobility.billie.vehicle.mapping.VehicleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByVehicleNum(Long vehicleNum);
    VehicleMapping findByVehicleName(String vehicleName);
    void deleteByVehicleNum(Long vehicleNum);
}
