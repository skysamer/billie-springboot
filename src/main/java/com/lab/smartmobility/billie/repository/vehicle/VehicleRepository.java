package com.lab.smartmobility.billie.repository.vehicle;

import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.mapping.VehicleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByVehicleNum(Long vehicleNum);
    VehicleMapping findByVehicleName(String vehicleName);
    void deleteByVehicleNum(Long vehicleNum);
}
