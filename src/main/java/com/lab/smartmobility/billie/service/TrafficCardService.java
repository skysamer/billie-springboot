package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.traffic.ReturnTrafficCardDTO;
import com.lab.smartmobility.billie.dto.traffic.TrafficCardApplyDTO;
import com.lab.smartmobility.billie.dto.traffic.TrafficCardForm;
import com.lab.smartmobility.billie.entity.*;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.traffic.TrafficCardRepository;
import com.lab.smartmobility.billie.repository.traffic.TrafficCardReservationRepository;
import com.lab.smartmobility.billie.repository.traffic.TrafficCardReservationRepositoryImpl;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TrafficCardService {
    private final TrafficCardRepository cardRepository;
    private final TrafficCardReservationRepository reservationRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final TrafficCardReservationRepositoryImpl reservationRepositoryImpl;
    private final DateTimeUtil dateTimeUtil;
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
            LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getDateOfRental(), trafficCardApplyDTO.getTimeOfRental());
            LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(trafficCardApplyDTO.getExpectedReturnDate(), trafficCardApplyDTO.getExpectedReturnTime());

            if(LocalDateTime.now().isAfter(rentedAt)){
                return 400;
            }
            List<TrafficCardReservation> reservationList=reservationRepository.findAllByReturnStatus(0);
            for(TrafficCardReservation reservation : reservationList){
                if(((reservation.getRentedAt().isBefore(rentedAt) || reservation.getRentedAt().isEqual(rentedAt)) &&
                        (reservation.getReturnedAt().isAfter(rentedAt)))
                        && trafficCard.getCardNum().equals(reservation.getTrafficCard().getCardNum())){
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
            List<TrafficCardReservation> reservationList=reservationRepository.findAllByReturnStatus(0);
            reservationList.remove(reservationRepository.findByReservationNum(reservationNum));
            for(TrafficCardReservation reservation : reservationList){
                if(((reservation.getRentedAt().isBefore(rentedAt) || reservation.getRentedAt().isEqual(rentedAt)) &&
                        (reservation.getReturnedAt().isAfter(rentedAt)))
                        && trafficCard.getCardNum().equals(reservation.getTrafficCard().getCardNum())){
                    return 500;
                }
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

        List<TrafficCardReservation> reservationList=reservationRepository.findAllByReturnStatus(0);
        reservationList.remove(reservationRepository.findByReservationNum(reservationNum));
        for(TrafficCardReservation reservation : reservationList){
            if(((reservation.getRentedAt().isBefore(rentedAt) || reservation.getRentedAt().isEqual(rentedAt)) &&
                    (reservation.getReturnedAt().isAfter(rentedAt)))
                    && trafficCard.getCardNum().equals(reservation.getTrafficCard().getCardNum())){
                return new HttpMessage("fail", "already-reservation");
            }
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
    public List<TrafficCardReservation> getCardReturnList(int disposalInfo, Long cardNum, String baseDate, PageRequest pageRequest){
        TrafficCard trafficCard=cardRepository.findByCardNum(cardNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.findAll(trafficCard, disposalInfo, pageRequest);
        }

        LocalDateTime startDateTime= dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime= dateTimeUtil.getEndDateTime(baseDate);
        return reservationRepositoryImpl.findAll(trafficCard, startDateTime, endDateTime, disposalInfo, pageRequest);
    }

    /*교통카드 반납 이력 상세 조회*/
    public TrafficCardReservation getCardReturn(Long reservationNum){
        return reservationRepository.findByReservationNum(reservationNum);
    }

    /*교통카드 반납 이력 전체 개수 조회*/
    public Long getReturnCount(int disposalInfo, Long cardNum, String baseDate){
        TrafficCard trafficCard=cardRepository.findByCardNum(cardNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.countByReturnStatus(trafficCard, disposalInfo);
        }

        LocalDateTime startDateTime= dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime= dateTimeUtil.getEndDateTime(baseDate);
        return reservationRepositoryImpl.countByReturnStatus(trafficCard, startDateTime, endDateTime, disposalInfo);
    }

    /*반납 이력 엑셀 다운로드*/
    public Workbook excelDownload(int disposalInfo, Long cardNum, String baseDate){
        TrafficCard trafficCard=cardRepository.findByCardNum(cardNum);
        List<TrafficCardReservation> reservationList;
        if(baseDate.equals("all")){
            reservationList = new ArrayList<>(reservationRepositoryImpl.findAll(trafficCard, disposalInfo));
        }else{
            LocalDateTime startDateTime= dateTimeUtil.getStartDateTime(baseDate);
            LocalDateTime endDateTime= dateTimeUtil.getEndDateTime(baseDate);
            reservationList= new ArrayList<>(reservationRepositoryImpl.findAll(trafficCard, startDateTime, endDateTime, disposalInfo));
        }

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(baseDate);
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("카드번호");
        cell = row.createCell(1);
        cell.setCellValue("잔액");
        cell = row.createCell(2);
        cell.setCellValue("대여일");
        cell = row.createCell(3);
        cell.setCellValue("대여시작 시간");
        cell = row.createCell(4);
        cell.setCellValue("반납일");
        cell = row.createCell(5);
        cell.setCellValue("대여종료 시간");
        cell = row.createCell(6);
        cell.setCellValue("내용(사유)");

        for (TrafficCardReservation reservation : reservationList) {
            row = sheet.createRow(rowNum++);
            LocalDate startDate=LocalDate.of(reservation.getRentedAt().getYear(), reservation.getRentedAt().getMonth(),
                    reservation.getRentedAt().getDayOfMonth());
            LocalTime startTime=LocalTime.of(reservation.getRentedAt().getHour(), reservation.getRentedAt().getMinute(), 0);
            LocalDate endDate=LocalDate.of(reservation.getReturnedAt().getYear(), reservation.getReturnedAt().getMonth(),
                    reservation.getReturnedAt().getDayOfMonth());
            LocalTime endTime=LocalTime.of(reservation.getReturnedAt().getHour(), reservation.getReturnedAt().getMinute(), 0);

            cell = row.createCell(0);
            cell.setCellValue(reservation.getTrafficCard().getCardNum());
            cell = row.createCell(1);
            cell.setCellValue(reservation.getBalanceHistory());
            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(startDate));
            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(startTime));
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(endDate));
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(endTime));
            cell = row.createCell(6);
            cell.setCellValue(reservation.getContent());
        }
        return wb;
    }

}
