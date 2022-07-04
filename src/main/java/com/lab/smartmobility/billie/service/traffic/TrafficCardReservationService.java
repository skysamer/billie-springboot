package com.lab.smartmobility.billie.service.traffic;

import com.lab.smartmobility.billie.dto.traffic.TrafficCardApplyDTO;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.TrafficCard;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.traffic.TrafficCardRepository;
import com.lab.smartmobility.billie.repository.traffic.TrafficCardReservationRepository;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TrafficCardReservationService {
    private final TrafficCardRepository cardRepository;
    private final TrafficCardReservationRepository reservationRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    /*교통카드 대여 신청*/
    public int applyCardRental(TrafficCardApplyDTO trafficCardApplyDTO){
        try {
            TrafficCard trafficCard=cardRepository.findByCardNum(trafficCardApplyDTO.getCardNum());
            log.info(trafficCard.toString());
            Staff renderInfo=staffRepository.findByStaffNum(trafficCardApplyDTO.getStaffNum());
            LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getDateOfRental(), trafficCardApplyDTO.getTimeOfRental());
            LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getExpectedReturnDate(), trafficCardApplyDTO.getExpectedReturnTime());

            if(LocalDateTime.now().isAfter(rentedAt)){
                return 400;
            }

            if(checkReservationIsDuplicate(-1L, rentedAt, returnedAt, trafficCard)){
                return 500;
            }

            TrafficCardReservation trafficCardReservation=new TrafficCardReservation();
            modelMapper.map(trafficCardApplyDTO, trafficCardReservation);
            trafficCardReservation.insert(trafficCard, renderInfo, rentedAt, returnedAt);
            reservationRepository.save(trafficCardReservation);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*신규 예약이 기존 예약 날짜 및 시간과 겹치는지 체크*/
    private boolean checkReservationIsDuplicate(Long reservationNum, LocalDateTime rentedAt, LocalDateTime returnedAt, TrafficCard card){
        if(reservationNum == -1L){
            return reservationRepository.countByTrafficCardAndReturnStatusAndRentedAtLessThanAndReturnedAtGreaterThan(card, 0, returnedAt, rentedAt) == 1;
        }
        return reservationRepository.countByReservationNumNotAndReturnStatusAndTrafficCardAndRentedAtLessThanAndReturnedAtGreaterThan(reservationNum, 0, card, returnedAt, rentedAt) == 1;
    }

    /*교통카드 대여 목록 조회*/
    @Transactional(readOnly = true)
    public List<TrafficCardReservation> getCardRentalList(LocalDate startDate, LocalDate endDate){
        LocalDateTime startDateTime=LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(),
                0, 0, 0);
        LocalDateTime endDateTime=LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(),
                23, 59, 59);
        return reservationRepository.findAllByRentedAtBetween(startDateTime, endDateTime);
    }

    /*교통카드 상세 대여 정보 조회*/
    @Transactional(readOnly = true)
    public TrafficCardReservation getCardRentalInfo(Long reservationNum){
        return reservationRepository.findByReservationNum(reservationNum);
    }

    /*교통카드 대여 정보 수정*/
    public int modifyCardReservation(Long reservationNum, TrafficCardApplyDTO trafficCardApplyDTO){
        TrafficCardReservation reservationInfo = reservationRepository.findByReservationNum(reservationNum);
        TrafficCard trafficCard = cardRepository.findByCardNum(trafficCardApplyDTO.getCardNum());

        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getDateOfRental(), trafficCardApplyDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getExpectedReturnDate(), trafficCardApplyDTO.getExpectedReturnTime());
        try {
            if(LocalDateTime.now().isAfter(rentedAt)){
                return 400;
            }else if(!trafficCardApplyDTO.getStaffNum().equals(reservationInfo.getStaff().getStaffNum())){
                return 300;
            }else if(LocalDateTime.now().isAfter(reservationInfo.getRentedAt())){
                return 303;
            }

            if(checkReservationIsDuplicate(reservationNum, rentedAt, returnedAt, trafficCard)){
                return 500;
            }

            modelMapper.map(trafficCardApplyDTO, reservationInfo);
            reservationInfo.updateReservationInfo(trafficCard, rentedAt, returnedAt);
            reservationRepository.save(reservationInfo);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*교통 카드 대여 요청 삭제*/
    public int removeCardReservationInfo(Long reservationNum){
        TrafficCardReservation vehicleReservation=reservationRepository.findByReservationNum(reservationNum);
        if(vehicleReservation.getRentedAt().isAfter(LocalDateTime.now())){
            reservationRepository.deleteByReservationNum(reservationNum);
            return 0;
        }
        return 9999;
    }

    /*관리자의 대여 신청 삭제*/
    public HttpMessage removeReservationByAdmin(Long reservationNum){
        TrafficCardReservation trafficCardReservation = reservationRepository.findByReservationNum(reservationNum);
        if(trafficCardReservation.getReturnStatus()==1){
            return new HttpMessage("fail", "refund-processing-is-in-progress");
        }

        reservationRepository.delete(trafficCardReservation);
        return new HttpMessage("success", "success-remove");
    }

    /*관리자의 대여 신청 내역 수정*/
    public HttpMessage modifyReservationInfoByAdmin(Long reservationNum, TrafficCardApplyDTO trafficCardApplyDTO){
        TrafficCardReservation trafficCardReservation = reservationRepository.findByReservationNum(reservationNum);
        if(trafficCardReservation.getReturnStatus()==1){
            return new HttpMessage("fail", "refund-processing-is-in-progress");
        }

        TrafficCard trafficCard=cardRepository.findByCardNum(trafficCardApplyDTO.getCardNum());

        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getDateOfRental(), trafficCardApplyDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getExpectedReturnDate(), trafficCardApplyDTO.getExpectedReturnTime());

        if(checkReservationIsDuplicate(reservationNum, rentedAt, returnedAt, trafficCard)){
            return new HttpMessage("fail", "already-reservation");
        }

        modelMapper.map(trafficCardApplyDTO, trafficCardReservation);
        trafficCardReservation.updateReservationInfo(trafficCard, rentedAt, returnedAt);
        reservationRepository.save(trafficCardReservation);
        return new HttpMessage("success", "success-modify");
    }

    /*금일 나의 교통카드*/
    public List<TrafficCardReservation> getMyCardReservation(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        return reservationRepository.findByStaffAndReturnStatusOrderByRentedAt(staff, 0);
    }
}
