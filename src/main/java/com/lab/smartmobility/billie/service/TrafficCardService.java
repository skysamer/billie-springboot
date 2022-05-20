package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.ReturnTrafficCardDTO;
import com.lab.smartmobility.billie.dto.TrafficCardApplyDTO;
import com.lab.smartmobility.billie.dto.TrafficCardForm;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.TrafficCard;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.TrafficCardRepository;
import com.lab.smartmobility.billie.repository.TrafficCardReservationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class TrafficCardService {
    private final TrafficCardRepository cardRepository;
    private final TrafficCardReservationRepository reservationRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final Log log= LogFactory.getLog(getClass());

    /*보유 교통카드 목록 조회*/
    public List<TrafficCard> getPossessCardList(){
        try {
            return cardRepository.findAll();
        }catch (Exception e){
            return null;
        }
    }

    /*신규 교통카드 등록*/
    public int registerCard(TrafficCardForm trafficCardForm){
        try {
            TrafficCard trafficCard=new TrafficCard();
            modelMapper.map(trafficCardForm, trafficCard);
            cardRepository.save(trafficCard);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
    }

    /*교통카드 개별 상세정보 조회*/
    public TrafficCard getCardInfo(Long cardNum){
        return cardRepository.findByCardNum(cardNum);
    }

    /*교통카드 등록 정보 수정*/
    public int updateCardInfo(TrafficCardForm trafficCardForm){
        try{
            TrafficCard updatedCardInfo=cardRepository.findByCardNum(trafficCardForm.getCardNum());
            modelMapper.map(trafficCardForm, updatedCardInfo);
            cardRepository.save(updatedCardInfo);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*교통카드 폐기*/
    public int discardCard(Long cardNum, HashMap<String, String> reason){
        log.info(reason.get("reason"));
        TrafficCard trafficCard=cardRepository.findByCardNum(cardNum);
        if(trafficCard.getRentalStatus()==99){
            return 500;
        }
        trafficCard.setRentalStatus(99);
        trafficCard.setDiscardReason(reason.get("reason"));
        cardRepository.save(trafficCard);

        return 0;
    }

    /*교통카드 정보 삭제*/
    public int removeCardInfo(Long cardNum){
        try{
            cardRepository.deleteByCardNum(cardNum);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*교통카드 대여 신청*/
    public int applyCardRental(TrafficCardApplyDTO trafficCardApplyDTO){
        try {
            TrafficCard trafficCard=cardRepository.findByCardNum(trafficCardApplyDTO.getCardNum());
            log.info(trafficCard.toString());
            Staff renderInfo=staffRepository.findByStaffNum(trafficCardApplyDTO.getStaffNum());
            LocalDateTime rentedAt=LocalDateTime.of(trafficCardApplyDTO.getDateOfRental().getYear(),
                    trafficCardApplyDTO.getDateOfRental().getMonth(),
                    trafficCardApplyDTO.getDateOfRental().getDayOfMonth(),
                    trafficCardApplyDTO.getTimeOfRental().getHour(),
                    trafficCardApplyDTO.getTimeOfRental().getMinute(), 0);
            LocalDateTime returnedAt=LocalDateTime.of(trafficCardApplyDTO.getExpectedReturnDate().getYear(),
                    trafficCardApplyDTO.getExpectedReturnDate().getMonth(),
                    trafficCardApplyDTO.getExpectedReturnDate().getDayOfMonth(),
                    trafficCardApplyDTO.getExpectedReturnTime().getHour(),
                    trafficCardApplyDTO.getExpectedReturnTime().getMinute(), 0);

            if(LocalDateTime.now().isAfter(rentedAt)){
                return 400;
            }
            List<TrafficCardReservation> reservationList=reservationRepository.findAllByReturnStatus(0);
            for(TrafficCardReservation reservation : reservationList){
                if(((reservation.getRentedAt().isBefore(rentedAt) || reservation.getRentedAt().isEqual(rentedAt)) &&
                        (reservation.getReturnedAt().isAfter(rentedAt) || reservation.getReturnedAt().isEqual(rentedAt)))
                        && trafficCard.getCardNum().equals(reservation.getTrafficCard().getCardNum())){
                    log.info(reservation.getRentedAt());
                    log.info(reservation.getReturnedAt());
                    return 500;
                }
            }

            TrafficCardReservation trafficCardReservation=new TrafficCardReservation();
            modelMapper.map(trafficCardApplyDTO, trafficCardReservation);
            trafficCardReservation.setTrafficCard(trafficCard);
            trafficCardReservation.setStaff(renderInfo);
            trafficCardReservation.setRentedAt(rentedAt);
            trafficCardReservation.setReturnedAt(returnedAt);
            reservationRepository.save(trafficCardReservation);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
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
        TrafficCard trafficCard=cardRepository.findByCardNum(trafficCardApplyDTO.getCardNum());
        LocalDateTime rentedAt=LocalDateTime.of(trafficCardApplyDTO.getDateOfRental().getYear(),
                trafficCardApplyDTO.getDateOfRental().getMonth(), trafficCardApplyDTO.getDateOfRental().getDayOfMonth(),
                trafficCardApplyDTO.getTimeOfRental().getHour(),
                trafficCardApplyDTO.getTimeOfRental().getMinute(), 0);
        LocalDateTime returnedAt=LocalDateTime.of(trafficCardApplyDTO.getExpectedReturnDate().getYear(),
                trafficCardApplyDTO.getExpectedReturnDate().getMonth(), trafficCardApplyDTO.getExpectedReturnDate().getDayOfMonth(),
                trafficCardApplyDTO.getExpectedReturnTime().getHour(),
                trafficCardApplyDTO.getExpectedReturnTime().getMinute(), 0);

        try {
            if(LocalDateTime.now().isAfter(rentedAt)){
                return 400;
            } else if(rentedAt.isAfter(reservationInfo.getRentedAt()) || rentedAt.isEqual(reservationInfo.getRentedAt())){
                return 300;
            }
            List<TrafficCardReservation> reservationList=reservationRepository.findAllByReturnStatus(0);
            for(TrafficCardReservation reservation : reservationList){
                if(((reservation.getRentedAt().isBefore(rentedAt) || reservation.getRentedAt().isEqual(rentedAt)) &&
                        (reservation.getReturnedAt().isAfter(rentedAt) || reservation.getReturnedAt().isEqual(rentedAt)))
                        && trafficCard.getCardNum().equals(reservation.getTrafficCard().getCardNum())){
                    return 500;
                }
            }

            Staff renderInfo=staffRepository.findByStaffNum(trafficCardApplyDTO.getStaffNum());
            modelMapper.map(trafficCardApplyDTO, reservationInfo);
            reservationInfo.setTrafficCard(trafficCard);
            reservationInfo.setStaff(renderInfo);
            reservationInfo.setRentedAt(rentedAt);
            reservationInfo.setReturnedAt(returnedAt);
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

    /*금일 나의 교통카드*/
    public List<TrafficCardReservation> getMyTodayCardReservation(LocalDate today, Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        List<TrafficCardReservation> myReservationList = reservationRepository.findByStaffAndReturnStatusOrderByRentedAt(staff, 0);

        List<TrafficCardReservation> currentlyOnRental=new ArrayList<>();
        for(TrafficCardReservation myReservation : myReservationList){
            if(myReservation.getTrafficCard().getRentalStatus()==1){
                currentlyOnRental.add(myReservation);
            }
        }
        return currentlyOnRental;
    }

    /*교통카드 반납 신청*/
    public int applyCardReturn(ReturnTrafficCardDTO returnTrafficCard){
        LocalDateTime returnedAt=LocalDateTime.of(returnTrafficCard.getDateOfReturn().getYear(),
                returnTrafficCard.getDateOfReturn().getMonth(), returnTrafficCard.getDateOfReturn().getDayOfMonth(),
                returnTrafficCard.getTimeOfReturn().getHour(),
                returnTrafficCard.getTimeOfReturn().getMinute(), 0);

        try{
            if(cardRepository.findByCardNum(returnTrafficCard.getCardNum())==null){
                throw new AccessDeniedException("error");
            }

            cardRepository.changeRentalStatus(0, returnTrafficCard.getCardNum());
            cardRepository.changeBalance(returnTrafficCard.getBalance(), returnTrafficCard.getCardNum());

            TrafficCardReservation updatedCardReservationInfo=reservationRepository.findByReservationNum(returnTrafficCard.getReservationNum());
            modelMapper.map(returnTrafficCard, updatedCardReservationInfo);
            updatedCardReservationInfo.setReturnedAt(returnedAt);
            updatedCardReservationInfo.setReturnStatus(1);
            updatedCardReservationInfo.setBalanceHistory(returnTrafficCard.getBalance());
            reservationRepository.save(updatedCardReservationInfo);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
    }

    /*교통카드 반납 목록 조회*/
    public List<TrafficCardReservation> getCardReturnList(PageRequest pageRequest){
        return reservationRepository.findAllByReturnStatusOrderByReturnedAtDesc(1, pageRequest);
    }

    /*교통카드 반납 이력 상세 조회*/
    public TrafficCardReservation getCardReturn(Long reservationNum){
        return reservationRepository.findByReservationNum(reservationNum);
    }

    /*교통카드 반납 이력 전체 개수 조회*/
    public int getReturnCount(){
        return reservationRepository.countByReturnStatus(1);
    }
}
