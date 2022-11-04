package com.lab.smartmobility.billie.traffic.service;

import com.lab.smartmobility.billie.traffic.dto.ReturnTrafficCardDTO;
import com.lab.smartmobility.billie.traffic.domain.TrafficCard;
import com.lab.smartmobility.billie.traffic.domain.TrafficCardReservation;
import com.lab.smartmobility.billie.traffic.repository.TrafficCardRepository;
import com.lab.smartmobility.billie.traffic.repository.TrafficCardReservationRepository;
import com.lab.smartmobility.billie.traffic.repository.TrafficCardReservationRepositoryImpl;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
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
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TrafficCardReturnService {
    private final TrafficCardRepository cardRepository;
    private final TrafficCardReservationRepository reservationRepository;
    private final ModelMapper modelMapper;
    private final TrafficCardReservationRepositoryImpl reservationRepositoryImpl;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    /*교통카드 반납 신청*/
    public int applyCardReturn(ReturnTrafficCardDTO returnTrafficCard){
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(returnTrafficCard.getDateOfReturn(), returnTrafficCard.getTimeOfReturn());
        try{
            if(cardRepository.findByCardNum(returnTrafficCard.getCardNum()) == null){
                throw new AccessDeniedException("error");
            }

            returnCard(returnTrafficCard);
            modifyCardReservationInfo(returnTrafficCard, returnedAt);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
    }

    private void returnCard(ReturnTrafficCardDTO returnTrafficCard){
        TrafficCard usedTrafficCard = cardRepository.findByCardNum(returnTrafficCard.getCardNum());
        usedTrafficCard.returnCard(returnTrafficCard.getBalance());
    }

    private void modifyCardReservationInfo(ReturnTrafficCardDTO returnTrafficCard, LocalDateTime returnedAt){
        TrafficCardReservation updatedCardReservationInfo = reservationRepository.findByReservationNum(returnTrafficCard.getReservationNum());
        modelMapper.map(returnTrafficCard, updatedCardReservationInfo);
        updatedCardReservationInfo.update(1, returnedAt, returnTrafficCard.getBalance());
    }

    /*교통카드 반납 목록 조회*/
    public List<TrafficCardReservation> getCardReturnList(int disposalInfo, Long cardNum, String baseDate, PageRequest pageRequest){
        TrafficCard trafficCard = cardRepository.findByCardNum(cardNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.findAll(trafficCard, disposalInfo, pageRequest);
        }

        LocalDateTime startDateTime = dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime = dateTimeUtil.getEndDateTime(baseDate);
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

        LocalDateTime startDateTime = dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime = dateTimeUtil.getEndDateTime(baseDate);
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
        Row row;
        Cell cell;
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
            LocalDate startDate = LocalDate.of(reservation.getRentedAt().getYear(), reservation.getRentedAt().getMonth(),
                    reservation.getRentedAt().getDayOfMonth());
            LocalTime startTime = LocalTime.of(reservation.getRentedAt().getHour(), reservation.getRentedAt().getMinute(), 0);
            LocalDate endDate = LocalDate.of(reservation.getReturnedAt().getYear(), reservation.getReturnedAt().getMonth(),
                    reservation.getReturnedAt().getDayOfMonth());
            LocalTime endTime = LocalTime.of(reservation.getReturnedAt().getHour(), reservation.getReturnedAt().getMinute(), 0);

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
