package com.lab.smartmobility.billie.traffic.service;

import com.lab.smartmobility.billie.traffic.dto.TrafficCardApplyDTO;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.traffic.domain.TrafficCard;
import com.lab.smartmobility.billie.traffic.domain.TrafficCardReservation;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.traffic.repository.TrafficCardRepository;
import com.lab.smartmobility.billie.traffic.repository.TrafficCardReservationRepository;
import com.lab.smartmobility.billie.traffic.repository.TrafficCardReservationRepositoryImpl;
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
@Transactional
@RequiredArgsConstructor
public class TrafficCardReservationService {
    private final TrafficCardRepository cardRepository;
    private final TrafficCardReservationRepository reservationRepository;
    private final TrafficCardReservationRepositoryImpl reservationQueryRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    /*교통카드 대여 신청*/
    public HttpBodyMessage applyCardRental(TrafficCardApplyDTO trafficCardApplyDTO){
        TrafficCard trafficCard = cardRepository.findByCardNum(trafficCardApplyDTO.getCardNum());
        Staff renderInfo = staffRepository.findByStaffNum(trafficCardApplyDTO.getStaffNum());

        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getDateOfRental(), trafficCardApplyDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getExpectedReturnDate(), trafficCardApplyDTO.getExpectedReturnTime());

        if(LocalDateTime.now().isAfter(rentedAt)){
            return new HttpBodyMessage("fail", "이전 날짜로 대여할 수 없습니다");
        }
        if(checkReservationIsDuplicate(-1L, rentedAt, returnedAt, trafficCard)){
            return new HttpBodyMessage("fail", "해당 날짜에 이미 대여중인 카드입니다");
        }

        TrafficCardReservation trafficCardReservation=new TrafficCardReservation();
        modelMapper.map(trafficCardApplyDTO, trafficCardReservation);
        trafficCardReservation.insert(trafficCard, renderInfo, rentedAt, returnedAt);
        reservationRepository.save(trafficCardReservation);
        return new HttpBodyMessage("success", "대여신청 성공");
    }

    /*신규 예약이 기존 예약 날짜 및 시간과 겹치는지 체크*/
    private boolean checkReservationIsDuplicate(Long reservationNum, LocalDateTime rentedAt, LocalDateTime returnedAt, TrafficCard card){
        return reservationQueryRepository.checkTimeIsDuplicated(reservationNum, rentedAt, returnedAt, card) > 0;
    }

    /*교통카드 대여 목록 조회*/
    @Transactional(readOnly = true)
    public List<TrafficCardReservation> getCardRentalList(LocalDate startDate, LocalDate endDate){
        LocalDateTime startDateTime=LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(),
                0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(),
                23, 59, 59);
        return reservationRepository.findAllByRentedAtBetween(startDateTime, endDateTime);
    }

    /*교통카드 상세 대여 정보 조회*/
    @Transactional(readOnly = true)
    public TrafficCardReservation getCardRentalInfo(Long reservationNum){
        return reservationRepository.findByReservationNum(reservationNum);
    }

    /*교통카드 대여 정보 수정*/
    public HttpBodyMessage modifyCardReservation(Long reservationNum, TrafficCardApplyDTO trafficCardApplyDTO){
        TrafficCardReservation reservationInfo = reservationRepository.findByReservationNum(reservationNum);
        TrafficCard trafficCard = cardRepository.findByCardNum(trafficCardApplyDTO.getCardNum());

        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getDateOfRental(), trafficCardApplyDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getExpectedReturnDate(), trafficCardApplyDTO.getExpectedReturnTime());

        if(LocalDateTime.now().isAfter(rentedAt)){
            return new HttpBodyMessage("fail", "이전 날짜로 대여할 수 없습니다");
        }else if(!trafficCardApplyDTO.getStaffNum().equals(reservationInfo.getStaff().getStaffNum())){
            return new HttpBodyMessage("fail", "대여자 정보가 일치하지 않습니다");
        }else if(LocalDateTime.now().isAfter(reservationInfo.getRentedAt())){
            return new HttpBodyMessage("fail", "대여시작시간 이후에는 수정할 수 없습니다");
        }

        if(checkReservationIsDuplicate(reservationNum, rentedAt, returnedAt, trafficCard)){
            return new HttpBodyMessage("fail", "이미 대여중인 카드입니다");
        }

        modelMapper.map(trafficCardApplyDTO, reservationInfo);
        reservationInfo.updateReservationInfo(trafficCard, rentedAt, returnedAt);
        return new HttpBodyMessage("success", "대여정보 수정 성공");
    }

    /*교통 카드 대여 요청 삭제*/
    public HttpBodyMessage removeCardReservationInfo(Long reservationNum){
        TrafficCardReservation vehicleReservation = reservationRepository.findByReservationNum(reservationNum);
        if(vehicleReservation.getRentedAt().isAfter(LocalDateTime.now())){
            reservationRepository.deleteByReservationNum(reservationNum);
            return new HttpBodyMessage("success", "대여 정보 삭제 완료");
        }
        return new HttpBodyMessage("fail", "삭제할 수 없습니다. 반납 처리를 먼저 진행해주세요");
    }

    /*관리자의 대여 신청 삭제*/
    public HttpBodyMessage removeReservationByAdmin(Long reservationNum){
        TrafficCardReservation trafficCardReservation = reservationRepository.findByReservationNum(reservationNum);
        if(trafficCardReservation.getReturnStatus()==1){
            return new HttpBodyMessage("fail", "refund-processing-is-in-progress");
        }

        reservationRepository.delete(trafficCardReservation);
        return new HttpBodyMessage("success", "success-remove");
    }

    /*관리자의 대여 신청 내역 수정*/
    public HttpBodyMessage modifyReservationInfoByAdmin(Long reservationNum, TrafficCardApplyDTO trafficCardApplyDTO){
        TrafficCardReservation trafficCardReservation = reservationRepository.findByReservationNum(reservationNum);
        if(trafficCardReservation.getReturnStatus()==1){
            return new HttpBodyMessage("fail", "refund-processing-is-in-progress");
        }

        TrafficCard trafficCard=cardRepository.findByCardNum(trafficCardApplyDTO.getCardNum());

        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getDateOfRental(), trafficCardApplyDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getExpectedReturnDate(), trafficCardApplyDTO.getExpectedReturnTime());

        if(checkReservationIsDuplicate(reservationNum, rentedAt, returnedAt, trafficCard)){
            return new HttpBodyMessage("fail", "already-reservation");
        }

        modelMapper.map(trafficCardApplyDTO, trafficCardReservation);
        trafficCardReservation.updateReservationInfo(trafficCard, rentedAt, returnedAt);
        reservationRepository.save(trafficCardReservation);
        return new HttpBodyMessage("success", "success-modify");
    }

    /*금일 나의 교통카드*/
    public List<TrafficCardReservation> getMyCardReservation(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        return reservationRepository.findByStaffAndReturnStatusOrderByRentedAt(staff, 0);
    }
}
