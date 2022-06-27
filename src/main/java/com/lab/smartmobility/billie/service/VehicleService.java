package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.vehicle.ApplyRentalVehicleDTO;
import com.lab.smartmobility.billie.dto.vehicle.VehicleDTO;
import com.lab.smartmobility.billie.dto.vehicle.VehicleReturnDTO;
import com.lab.smartmobility.billie.entity.*;
import com.lab.smartmobility.billie.repository.*;
import com.lab.smartmobility.billie.repository.vehicle.VehicleRepository;
import com.lab.smartmobility.billie.repository.vehicle.VehicleReservationRepository;
import com.lab.smartmobility.billie.repository.vehicle.VehicleReservationRepositoryImpl;
import com.lab.smartmobility.billie.util.DateTimeUtil;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleReservationRepository reservationRepository;
    private final ReturnVehicleImageRepository imageRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final VehicleReservationRepositoryImpl reservationRepositoryImpl;
    private final DateTimeUtil dateTimeUtil;
    private final Workbook workbook;
    private final Log log;

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
            Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
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

    /*차량 예약 신청*/
    public HttpMessage applyForRent(ApplyRentalVehicleDTO rentalVehicleDTO){
        try {
            Staff renderInfo=staffRepository.findByStaffNum(rentalVehicleDTO.getStaffNum());
            Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(rentalVehicleDTO.getVehicleName()).getVehicleNum());

            LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(rentalVehicleDTO.getDateOfRental(), rentalVehicleDTO.getTimeOfRental());
            LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(rentalVehicleDTO.getExpectedReturnDate(), rentalVehicleDTO.getExpectedReturnTime());
            if(LocalDateTime.now().isAfter(rentedAt)){
                return new HttpMessage("fail", "현재 시각보다 과거로 예약할 수 없습니다");
            }

            if(checkReservationIsDuplicate(-1L, rentedAt, returnedAt, vehicle)){
                return new HttpMessage("fail", "해당 날짜에 차량이 이미 대여중입니다");
            }

            VehicleReservation applicationRentalVehicle=new VehicleReservation();
            modelMapper.map(rentalVehicleDTO, applicationRentalVehicle);
            applicationRentalVehicle.insert(vehicle, renderInfo, rentedAt, returnedAt);
            reservationRepository.save(applicationRentalVehicle);
        }catch (Exception e){
            e.printStackTrace();
            return new HttpMessage("fail", "차량 대여 신청 실패");
        }
        return new HttpMessage("success", "대여 성공");
    }

    /*예약 신청 날짜 및 시간이 기존예약괴 겹치는지 체크*/
    private boolean checkReservationIsDuplicate(Long rentNum, LocalDateTime rentedAt, LocalDateTime returnedAt, Vehicle vehicle){
        if(rentNum == -1L){
            return reservationRepository.countByVehicleAndReturnStatusCodeAndRentedAtLessThanAndReturnedAtGreaterThan(vehicle, 0, returnedAt, rentedAt) == 1;
        }
        return reservationRepository.countByRentNumNotAndVehicleAndReturnStatusCodeAndRentedAtLessThanAndReturnedAtGreaterThan(rentNum, vehicle, 0, returnedAt, rentedAt) == 1;
    }

    /*월단위 차량 예약 목록 조회*/
    public List<VehicleReservation> reservationList(LocalDate startDate, LocalDate endDate){
        LocalDateTime startDateTime=LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(),
                0, 0, 0);
        LocalDateTime endDateTime=LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(),
                23, 59, 59);
        return reservationRepository.findAllByRentedAtBetween(startDateTime, endDateTime);
    }

    /*개별 차량 예약 조회*/
    public VehicleReservation getReservation(Long rentNum){
        return reservationRepository.findByRentNum(rentNum);
    }

    /*차량 예약 정보 수정*/
    public int modifyVehicleReservation(Long rentNum, ApplyRentalVehicleDTO applyRentalVehicleDTO){
        VehicleReservation reservationInfo=reservationRepository.findByRentNum(rentNum);
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(applyRentalVehicleDTO.getVehicleName()).getVehicleNum());

        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(applyRentalVehicleDTO.getDateOfRental(), applyRentalVehicleDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(applyRentalVehicleDTO.getExpectedReturnDate(), applyRentalVehicleDTO.getExpectedReturnTime());
        try{
            if(LocalDateTime.now().isAfter(rentedAt)){
                return 400;
            }else if(!applyRentalVehicleDTO.getStaffNum().equals(reservationInfo.getStaff().getStaffNum())){
                return 300;
            }else if(LocalDateTime.now().isAfter(reservationInfo.getRentedAt())){
                return 303;
            }

            if(checkReservationIsDuplicate(rentNum, rentedAt, returnedAt,  vehicle)){
                return 500;
            }

            modelMapper.map(applyRentalVehicleDTO, reservationInfo);
            reservationInfo.modifyInfo(vehicle, rentedAt, returnedAt);
            reservationRepository.save(reservationInfo);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*차량 예약 삭제*/
    public int removeReservationInfo(Long rentNum){
        try{
            VehicleReservation vehicleReservation=reservationRepository.findByRentNum(rentNum);
            if(vehicleReservation.getRentedAt().isAfter(LocalDateTime.now())){
                reservationRepository.deleteByRentNum(rentNum);
                return 0;
            }
            return 500;
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
    }

    /*관리자의 차량 예약 삭제*/
    public HttpMessage removeReservationByAdmin(Long rentNum){
        VehicleReservation vehicleReservation = reservationRepository.findByRentNum(rentNum);
        if(vehicleReservation.getReturnStatusCode()==1){
            return new HttpMessage("fail", "refund-processing-is-in-progress");
        }

        reservationRepository.delete(vehicleReservation);
        return new HttpMessage("success", "success-remove");
    }

    /*관리자의 차량 예약 수정*/
    public HttpMessage modifyRentInfoByAdmin(Long rentNum, ApplyRentalVehicleDTO rentalVehicleDTO){
        VehicleReservation vehicleReservation = reservationRepository.findByRentNum(rentNum);
        if(vehicleReservation.getReturnStatusCode()==1){
            return new HttpMessage("fail", "refund-processing-is-in-progress");
        }

        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(rentalVehicleDTO.getVehicleName()).getVehicleNum());
        LocalDateTime rentedAt = dateTimeUtil.combineDateAndTime(rentalVehicleDTO.getDateOfRental(), rentalVehicleDTO.getTimeOfRental());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(rentalVehicleDTO.getExpectedReturnDate(), rentalVehicleDTO.getExpectedReturnTime());

        if(checkReservationIsDuplicate(rentNum, rentedAt, returnedAt, vehicle)){
            return new HttpMessage("fail", "already-reservation");
        }

        modelMapper.map(rentalVehicleDTO, vehicleReservation);
        vehicleReservation.modifyRentInfoByAdmin(vehicle, rentedAt, returnedAt);
        reservationRepository.save(vehicleReservation);
        return new HttpMessage("success", "success-modify");
    }


    /*나의 차량 예약 현황 조회*/
    public List<VehicleReservation> getMyReservation(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        return reservationRepository.findByStaffAndReturnStatusCodeOrderByRentedAt(staff, 0);
    }

    /*차량 반납 신청*/
    public int returnVehicle(VehicleReturnDTO vehicleReturnDTO, List<MultipartFile> imageFiles){
        try{
            VehicleReservation vehicleReservation=reservationRepository.findByRentNum(vehicleReturnDTO.getRentNum());
            List<String> imageDataList=saveImageFile(imageFiles);

            for(int i = 1; i< Objects.requireNonNull(imageDataList).size(); i++){
                ImageVehicle imageVehicle=ImageVehicle.builder()
                        .imagePath(imageDataList.get(0))
                        .originImageName(imageDataList.get(i))
                        .vehicleReservation(vehicleReservation)
                        .build();
                imageRepository.save(imageVehicle);
            }
            changeVehicleInfo(vehicleReturnDTO);
            updateReturnInfo(vehicleReturnDTO);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    private List<String> saveImageFile(List<MultipartFile> images) {
        List<String> imageInformation=new ArrayList<>();

        //String uploadFolder="C:\\vehicle";  //로컬 윈도우용
        String uploadFolder="/home/billie/vehicle";

        String uploadFolderPath=getFolder();
        File uploadPath=new File(uploadFolder, uploadFolderPath);
        log.info(uploadPath);
        if(!uploadPath.exists()) {
            boolean mkdirCheck=uploadPath.mkdirs();
            log.info(mkdirCheck);
        }
        imageInformation.add(uploadPath.toString());

        for(MultipartFile image : images){
            UUID uuid=UUID.randomUUID();
            String imageName=uuid+"_"+image.getOriginalFilename();
            log.info(imageName);
            imageInformation.add(imageName);

            try{
                File saveFile=new File(uploadPath, imageName);
                image.transferTo(saveFile);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return imageInformation;
    }

    private String getFolder() {
        LocalDate now=LocalDate.now();
        String str=String.valueOf(now);
        return str.replace("-", File.separator);
    }

    /*반납 시 차량 정보 업데이트*/
    private void changeVehicleInfo(VehicleReturnDTO vehicleReturnDTO) throws Exception{
        Vehicle vehicle = vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(vehicleReturnDTO.getVehicleName()).getVehicleNum());
        vehicle.update(0, vehicleReturnDTO.getParkingLoc(), vehicleReturnDTO.getDistanceDriven());
        vehicleRepository.save(vehicle);
    }

    private void updateReturnInfo(VehicleReturnDTO vehicleReturnDTO) throws Exception{
        VehicleReservation updatedReturnInfo=reservationRepository.findByRentNum(vehicleReturnDTO.getRentNum());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(vehicleReturnDTO.getDateOfReturn(), vehicleReturnDTO.getTimeOfReturn());
        Duration duration = Duration.between(updatedReturnInfo.getReturnedAt(), returnedAt);
        String totalDrivingTime=String.valueOf(duration).replace("PT", "").replace("H", "시간").replace("M", "분");

        modelMapper.map(vehicleReturnDTO, updatedReturnInfo);
        updatedReturnInfo.update(1, returnedAt, totalDrivingTime);
        reservationRepository.save(updatedReturnInfo);
    }

    /*반납 이력 전체 조회*/
    public List<VehicleReservation> getReturnList(int disposalInfo, Long vehicleNum, String baseDate, PageRequest pageRequest){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.findAll(vehicle, disposalInfo, pageRequest);
        }

        LocalDateTime startDateTime= dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime= dateTimeUtil.getEndDateTime(baseDate);
        return reservationRepositoryImpl.findAll(vehicle, startDateTime, endDateTime, disposalInfo, pageRequest);
    }

    /*반납 이력 전체 개수 조회*/
    public Long getReturnCount(int disposalInfo, Long vehicleNum, String baseDate){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.countByReturnStatus(vehicle, disposalInfo);
        }
        LocalDateTime startDateTime= dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime= dateTimeUtil.getEndDateTime(baseDate);

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
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        List<VehicleReservation> returnHistoryList;
        if(baseDate.equals("all")){
            returnHistoryList = new ArrayList<>(reservationRepositoryImpl.findAll(vehicle, disposalInfo));
        }else{
            LocalDateTime startDateTime= dateTimeUtil.getStartDateTime(baseDate);
            LocalDateTime endDateTime= dateTimeUtil.getEndDateTime(baseDate);
            returnHistoryList= new ArrayList<>(reservationRepositoryImpl.findAll(vehicle, startDateTime, endDateTime, disposalInfo));
        }

        Sheet sheet = workbook.createSheet(baseDate);
        Row row = null;
        Cell cell = null;
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
        cell.setCellValue("총 주행 시간");
        cell = row.createCell(6);
        cell.setCellValue("동승자");
        cell = row.createCell(7);
        cell.setCellValue("내용(장소)");
        cell = row.createCell(8);
        cell.setCellValue("주행 후 계기판");
        cell = row.createCell(9);
        cell.setCellValue("주차위치");

        for (VehicleReservation reservation : returnHistoryList) {
            row = sheet.createRow(rowNum++);
            LocalDate startDate=LocalDate.of(reservation.getRentedAt().getYear(), reservation.getRentedAt().getMonth(),
                    reservation.getRentedAt().getDayOfMonth());
            LocalTime startTime=LocalTime.of(reservation.getRentedAt().getHour(), reservation.getRentedAt().getMinute(), 0);
            LocalDate endDate=LocalDate.of(reservation.getReturnedAt().getYear(), reservation.getReturnedAt().getMonth(),
                    reservation.getReturnedAt().getDayOfMonth());
            LocalTime endTime=LocalTime.of(reservation.getReturnedAt().getHour(), reservation.getReturnedAt().getMinute(), 0);

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
            cell.setCellValue(reservation.getTotalDrivingTime());
            cell = row.createCell(6);
            cell.setCellValue(reservation.getPassenger());
            cell = row.createCell(7);
            cell.setCellValue(reservation.getContent());
            cell = row.createCell(8);
            cell.setCellValue(reservation.getDistanceDriven());
            cell = row.createCell(9);
            cell.setCellValue(reservation.getParkingLoc());
        }
        return workbook;
    }

}
