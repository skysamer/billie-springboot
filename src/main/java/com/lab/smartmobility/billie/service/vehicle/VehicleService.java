package com.lab.smartmobility.billie.service.vehicle;

import com.lab.smartmobility.billie.dto.vehicle.NonBorrowableVehicle;
import com.lab.smartmobility.billie.dto.vehicle.VehicleDTO;
import com.lab.smartmobility.billie.entity.*;
import com.lab.smartmobility.billie.repository.*;
import com.lab.smartmobility.billie.repository.vehicle.VehicleRepository;
import com.lab.smartmobility.billie.repository.vehicle.VehicleRepositoryImpl;
import com.lab.smartmobility.billie.repository.vehicle.VehicleReservationRepository;
import com.lab.smartmobility.billie.repository.vehicle.VehicleReservationRepositoryImpl;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleRepositoryImpl vehicleRepositoryImpl;
    private final VehicleReservationRepository reservationRepository;
    private final ModelMapper modelMapper;
    private final VehicleReservationRepositoryImpl reservationRepositoryImpl;
    private final Log log;

    private static final Long IS_INSERT = -1L;

    /*보유 차량 및 대여 가능 여부 조회*/
    public List<VehicleDTO> vehicleList(){
        List<Vehicle> vehicleList=vehicleRepository.findAll();
        List<VehicleDTO> returnVehicle=new ArrayList<>();

        for(Vehicle vehicle : vehicleList){
            String[] vehicleName=vehicle.getVehicleName().split(" ");

            VehicleDTO vehicleDTO=VehicleDTO.builder()
                    .vehicleNum(vehicle.getVehicleNum())
                    .name(vehicleName[0])
                    .number(vehicleName[1]+" "+vehicleName[2])
                    .parkingLoc(vehicle.getParkingLoc())
                    .rentalStatus(vehicle.getRentalStatus())
                    .build();
            returnVehicle.add(vehicleDTO);
        }
        return returnVehicle;
    }

    /*개별 차량 정보 상세 조회*/
    public Vehicle getVehicleInfo(Long vehicleNum){
        return vehicleRepository.findByVehicleNum(vehicleNum);
    }

    /*신규 차량 등록*/
    public Vehicle register(Vehicle vehicle){
        try{
            return vehicleRepository.save(vehicle);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*차량 정보 수정*/
    public int modifyVehicleInfo(Vehicle vehicle){
        try {
            vehicleRepository.save(vehicle);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
    }

    /*차량 정보 삭제*/
    public int removeVehicleInfo(Long vehicleNum){
        try {
            vehicleRepository.deleteByVehicleNum(vehicleNum);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*차량 폐기*/
    public int discardVehicle(Long vehicleNum, HashMap<String, String> reason){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(vehicle.getRentalStatus()==99){
            return 500;
        }

        vehicle.discard(99, reason.get("reason"));
        vehicleRepository.save(vehicle);
        return 0;
    }

    /*해당 날짜에 예약이 불가능한 차량 목록 조회*/
    public List<NonBorrowableVehicle> getBorrowableVehicleList(LocalDateTime rentedAt, LocalDateTime returnedAt){
        return vehicleRepositoryImpl.getNonBorrowableVehicleList(rentedAt, returnedAt);
    }

}
