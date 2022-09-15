package com.lab.smartmobility.billie.vehicle.service;

import com.lab.smartmobility.billie.vehicle.dto.ApplyRentalVehicleDTO;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.vehicle.domain.Vehicle;
import com.lab.smartmobility.billie.vehicle.domain.VehicleReservation;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vehicle.repository.VehicleRepository;
import com.lab.smartmobility.billie.vehicle.repository.VehicleReservationRepository;
import com.lab.smartmobility.billie.vehicle.repository.VehicleReservationRepositoryImpl;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleReservationService {
    private final VehicleRepository vehicleRepository;
    private final VehicleReservationRepository reservationRepository;
    private final VehicleReservationRepositoryImpl reservationQueryRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    private static final Long IS_INSERT = -1L;

    /*차량 예약 신청*/
    public HttpBodyMessage applyForRent(ApplyRentalVehicleDTO rentalVehicleDTO){
        Staff renderInfo = staffRepository.findByStaffNum(rentalVehicleDTO.getStaffNum());
        Vehicle vehicle = vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(rentalVehicleDTO.getVehicleName()).getVehicleNum());

        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(rentalVehicleDTO.getDateOfRental(), rentalVehicleDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(rentalVehicleDTO.getExpectedReturnDate(), rentalVehicleDTO.getExpectedReturnTime());

        if(LocalDateTime.now().isAfter(rentedAt)){
            return new HttpBodyMessage("fail", "현재 시각보다 과거로 예약할 수 없습니다");
        }
        if(checkReservationIsDuplicate(IS_INSERT, rentedAt, returnedAt, vehicle)){
            return new HttpBodyMessage("fail", "해당 날짜에 차량이 이미 대여중입니다");
        }

        VehicleReservation applicationRentalVehicle = modelMapper.map(rentalVehicleDTO, VehicleReservation.class);
        applicationRentalVehicle.insert(vehicle, renderInfo, rentedAt, returnedAt);
        reservationRepository.save(applicationRentalVehicle);
        return new HttpBodyMessage("success", "대여 성공");
    }

    /*예약 신청 날짜 및 시간이 기존예약괴 겹치는지 체크*/
    private boolean checkReservationIsDuplicate(Long rentNum, LocalDateTime rentedAt, LocalDateTime returnedAt, Vehicle vehicle){
        return reservationQueryRepository.checkTimeIsDuplicated(rentNum, rentedAt, returnedAt, vehicle) > 0;
    }

    /*월단위 차량 예약 목록 조회*/
    public List<VehicleReservation> reservationList(LocalDate startDate, LocalDate endDate){
        LocalDateTime startDateTime = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(),
                0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(),
                23, 59, 59);
        return reservationQueryRepository.findAllByMonthly(startDateTime, endDateTime);
    }

    /*개별 차량 예약 조회*/
    public VehicleReservation getReservation(Long rentNum){
        return reservationRepository.findByRentNum(rentNum);
    }

    /*차량 예약 정보 수정*/
    public HttpBodyMessage modifyVehicleReservation(Long rentNum, ApplyRentalVehicleDTO applyRentalVehicleDTO){
        VehicleReservation reservationInfo = reservationRepository.findByRentNum(rentNum);
        Vehicle vehicle = vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(applyRentalVehicleDTO.getVehicleName()).getVehicleNum());

        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(applyRentalVehicleDTO.getDateOfRental(), applyRentalVehicleDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(applyRentalVehicleDTO.getExpectedReturnDate(), applyRentalVehicleDTO.getExpectedReturnTime());

        if(LocalDateTime.now().isAfter(rentedAt)){
            return new HttpBodyMessage("fail", "현재 시각보다 과거로 예약할 수 없습니다");
        }else if(!applyRentalVehicleDTO.getStaffNum().equals(reservationInfo.getStaff().getStaffNum())){
            return new HttpBodyMessage("fail", "대여자 정보가 일치하지 않습니다");
        }else if(LocalDateTime.now().isAfter(reservationInfo.getRentedAt())){
            return new HttpBodyMessage("fail", "대여시작시간 이후에는 변경할 수 없습니다");
        }

        if(checkReservationIsDuplicate(rentNum, rentedAt, returnedAt,  vehicle)){
            return new HttpBodyMessage("fail", "해당 날짜에 차량이 이미 대여중입니다");
        }

        modelMapper.map(applyRentalVehicleDTO, reservationInfo);
        reservationInfo.modifyInfo(vehicle, rentedAt, returnedAt);
        return new HttpBodyMessage("success", "예약 수정 성공");
    }

    /*차량 예약 삭제*/
    public HttpBodyMessage removeReservationInfo(Long rentNum){
        VehicleReservation vehicleReservation = reservationRepository.findByRentNum(rentNum);
        if(vehicleReservation.getRentedAt().isAfter(LocalDateTime.now())){
            reservationRepository.deleteByRentNum(rentNum);
            return new HttpBodyMessage("success", "삭제 성공");
        }

        return new HttpBodyMessage("fail", "삭제 불가");
    }

    /*관리자의 차량 예약 삭제*/
    public HttpBodyMessage removeReservationByAdmin(Long rentNum){
        VehicleReservation vehicleReservation = reservationRepository.findByRentNum(rentNum);
        if(vehicleReservation.getReturnStatusCode() == 1){
            return new HttpBodyMessage("fail", "refund-processing-is-in-progress");
        }

        reservationRepository.delete(vehicleReservation);
        return new HttpBodyMessage("success", "success-remove");
    }

    /*관리자의 차량 예약 수정*/
    public HttpBodyMessage modifyRentInfoByAdmin(Long rentNum, ApplyRentalVehicleDTO rentalVehicleDTO){
        VehicleReservation vehicleReservation = reservationRepository.findByRentNum(rentNum);
        if(vehicleReservation.getReturnStatusCode() == 1){
            return new HttpBodyMessage("fail", "refund-processing-is-in-progress");
        }

        Vehicle vehicle = vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(rentalVehicleDTO.getVehicleName()).getVehicleNum());
        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(rentalVehicleDTO.getDateOfRental(), rentalVehicleDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(rentalVehicleDTO.getExpectedReturnDate(), rentalVehicleDTO.getExpectedReturnTime());

        if(checkReservationIsDuplicate(rentNum, rentedAt, returnedAt, vehicle)){
            return new HttpBodyMessage("fail", "already-reservation");
        }

        modelMapper.map(rentalVehicleDTO, vehicleReservation);
        vehicleReservation.modifyRentInfoByAdmin(vehicle, rentedAt, returnedAt);
        return new HttpBodyMessage("success", "success-modify");
    }


    /*나의 차량 예약 현황 조회*/
    public List<VehicleReservation> getMyReservation(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        return reservationRepository.findByStaffAndReturnStatusCodeOrderByRentedAt(staff, 0);
    }
}
