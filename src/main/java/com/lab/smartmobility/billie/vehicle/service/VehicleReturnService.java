package com.lab.smartmobility.billie.vehicle.service;

import com.lab.smartmobility.billie.vehicle.dto.VehicleReturnDTO;
import com.lab.smartmobility.billie.entity.ImageVehicle;
import com.lab.smartmobility.billie.vehicle.domain.Vehicle;
import com.lab.smartmobility.billie.vehicle.domain.VehicleReservation;
import com.lab.smartmobility.billie.repository.ReturnVehicleImageRepository;
import com.lab.smartmobility.billie.vehicle.repository.VehicleRepository;
import com.lab.smartmobility.billie.vehicle.repository.VehicleReservationRepository;
import com.lab.smartmobility.billie.vehicle.repository.VehicleReservationRepositoryImpl;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleReturnService {
    private final VehicleRepository vehicleRepository;
    private final VehicleReservationRepository reservationRepository;
    private final ReturnVehicleImageRepository imageRepository;
    private final VehicleReservationRepositoryImpl reservationRepositoryImpl;
    private final ModelMapper modelMapper;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    /*차량 반납 신청*/
    public int returnVehicle(VehicleReturnDTO vehicleReturnDTO){
        try{
            modifyVehicleInfo(vehicleReturnDTO);
            modifyReservationInfo(vehicleReturnDTO);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*반납 시 차량 정보 업데이트*/
    private void modifyVehicleInfo(VehicleReturnDTO vehicleReturnDTO) {
        Vehicle vehicle = vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(vehicleReturnDTO.getVehicleName()).getVehicleNum());
        vehicle.update(0, vehicleReturnDTO.getParkingLoc(), vehicleReturnDTO.getDistanceDriven());
    }

    /*예약 정보 반납 상태로 업데이트*/
    private void modifyReservationInfo(VehicleReturnDTO vehicleReturnDTO) {
        VehicleReservation updatedReturnInfo = reservationRepository.findByRentNum(vehicleReturnDTO.getRentNum());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(vehicleReturnDTO.getDateOfReturn(), vehicleReturnDTO.getTimeOfReturn());

        modelMapper.map(vehicleReturnDTO, updatedReturnInfo);
        updatedReturnInfo.update(returnedAt);
    }

    /*반납 이력 전체 조회*/
    public List<VehicleReservation> getReturnList(int disposalInfo, Long vehicleNum, String baseDate, PageRequest pageRequest){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.findAll(vehicle, disposalInfo, pageRequest);
        }

        LocalDateTime startDateTime = dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime = dateTimeUtil.getEndDateTime(baseDate);
        return reservationRepositoryImpl.findAll(vehicle, startDateTime, endDateTime, disposalInfo, pageRequest);
    }

    /*반납 이력 전체 개수 조회*/
    public Long getReturnCount(int disposalInfo, Long vehicleNum, String baseDate){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.countByReturnStatus(vehicle, disposalInfo);
        }
        LocalDateTime startDateTime = dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime = dateTimeUtil.getEndDateTime(baseDate);

        return reservationRepositoryImpl.countByReturnStatus(vehicle, startDateTime, endDateTime, disposalInfo);
    }

    /*반납 이력 별 상세 조회*/
    public VehicleReservation getReturn(Long rentNum){
        return reservationRepository.findByRentNumAndReturnStatusCode(rentNum, 1);
    }

    /*반납 이력 별 이미지 파일 조회*/
    public List<byte[]> getReturnImages(Long rentNum){
        VehicleReservation returnVehicle=reservationRepository.findByRentNum(rentNum);

        List<ImageVehicle> imageVehicles=imageRepository.findAllByVehicleReservation(returnVehicle);
        List<String> imageOriginPaths=new ArrayList<>();

        for(ImageVehicle image : imageVehicles){
            //imageOriginPaths.add(image.getImagePath()+"\\"+image.getOriginImageName()); // 윈도우 로컬용
            imageOriginPaths.add(image.getImagePath()+"/"+image.getOriginImageName());
        }

        List<byte[]> imageFiles=new ArrayList<>();
        for(String imageOriginPath : imageOriginPaths){
            try{
                InputStream imageStream=new FileInputStream(imageOriginPath);
                byte[] imageByteArray= IOUtils.toByteArray(imageStream);
                imageStream.close();
                imageFiles.add(imageByteArray);
            }catch (IOException e){
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return imageFiles;
    }

    /*반납 이력 엑셀 다운로드*/
    public Workbook excelDownload(int disposalInfo, Long vehicleNum, String baseDate){
        Vehicle vehicle = vehicleRepository.findByVehicleNum(vehicleNum);
        List<VehicleReservation> returnHistoryList;
        if(baseDate.equals("all")){
            returnHistoryList = new ArrayList<>(reservationRepositoryImpl.findAll(vehicle, disposalInfo));
        }else{
            LocalDateTime startDateTime = dateTimeUtil.getStartDateTime(baseDate);
            LocalDateTime endDateTime = dateTimeUtil.getEndDateTime(baseDate);
            returnHistoryList = new ArrayList<>(reservationRepositoryImpl.findAll(vehicle, startDateTime, endDateTime, disposalInfo));
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(baseDate);
        Row row;
        Cell cell;
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("차량");
        cell = row.createCell(1);
        cell.setCellValue("대여일");
        cell = row.createCell(2);
        cell.setCellValue("대여시작 시간");
        cell = row.createCell(3);
        cell.setCellValue("반납일");
        cell = row.createCell(4);
        cell.setCellValue("대여종료 시간");
        cell = row.createCell(5);
        cell.setCellValue("동승자");
        cell = row.createCell(6);
        cell.setCellValue("내용(장소)");
        cell = row.createCell(7);
        cell.setCellValue("주행 후 계기판");
        cell = row.createCell(8);
        cell.setCellValue("주차위치");

        for (VehicleReservation reservation : returnHistoryList) {
            row = sheet.createRow(rowNum++);
            LocalDate startDate = LocalDate.of(reservation.getRentedAt().getYear(), reservation.getRentedAt().getMonth(),
                    reservation.getRentedAt().getDayOfMonth());
            LocalTime startTime = LocalTime.of(reservation.getRentedAt().getHour(), reservation.getRentedAt().getMinute(), 0);
            LocalDate endDate = LocalDate.of(reservation.getReturnedAt().getYear(), reservation.getReturnedAt().getMonth(),
                    reservation.getReturnedAt().getDayOfMonth());
            LocalTime endTime = LocalTime.of(reservation.getReturnedAt().getHour(), reservation.getReturnedAt().getMinute(), 0);

            cell = row.createCell(0);
            cell.setCellValue(reservation.getVehicle().getVehicleName());
            cell = row.createCell(1);
            cell.setCellValue(String.valueOf(startDate));
            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(startTime));
            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(endDate));
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(endTime));
            cell = row.createCell(5);
            cell.setCellValue(reservation.getPassenger());
            cell = row.createCell(6);
            cell.setCellValue(reservation.getContent());
            cell = row.createCell(7);
            cell.setCellValue(reservation.getDistanceDriven());
            cell = row.createCell(8);
            cell.setCellValue(reservation.getParkingLoc());
        }
        return workbook;
    }
}
