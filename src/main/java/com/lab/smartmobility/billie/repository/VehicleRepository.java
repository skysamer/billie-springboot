package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.mapping.VehicleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByVehicleNum(Long vehicleNum);

    @Modifying
    @Query(value = "UPDATE Vehicle set rental_status = :rentalStatus where vehicle_name = :vehicleName")
    void changeRentalStatus(@Param("rentalStatus") int rentalStatus, @Param("vehicleName") String vehicleName) throws Exception;

    @Modifying
    @Query(value = "UPDATE Vehicle set rental_status = :rentalStatus")
    void changeRentalStatus(@Param("rentalStatus") int rentalStatus);

    @Modifying
    @Query(value = "UPDATE Vehicle set parking_loc = :parkingLoc where vehicle_name = :vehicleName")
    void changeParkingLoc(@Param("parkingLoc") String parkingLoc, @Param("vehicleName") String vehicleName) throws Exception;

    @Modifying
    @Query(value = "UPDATE Vehicle set distance_driven = :distanceDriven where vehicle_name = :vehicleName")
    void changeDistanceDriven(@Param("distanceDriven") int distanceDriven, @Param("vehicleName") String vehicleName) throws Exception;

    VehicleMapping findByVehicleName(String vehicleName);
    void deleteByVehicleNum(Long vehicleNum);
}
