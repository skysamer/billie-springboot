package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.ApplyRentalVehicleDTO;
import com.lab.smartmobility.billie.dto.VehicleDTO;
import com.lab.smartmobility.billie.dto.VehicleReturnDTO;
import com.lab.smartmobility.billie.entity.*;
import com.lab.smartmobility.billie.repository.*;
import com.lab.smartmobility.billie.util.BaseDateParser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
    private final BaseDateParser baseDateParser;
    private final Log log = LogFactory.getLog(getClass());

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
            Set<VehicleReservation> reservationSet = reservationRepository.findAllByVehicle(vehicle);

            for(VehicleReservation reservation : reservationSet){
                List<ImageVehicle> imageVehicleList=imageRepository.findAllByVehicleReservation(reservation);
                for(ImageVehicle imageVehicle : imageVehicleList){
                    imageRepository.delete(imageVehicle);
                }
            }
            vehicleRepository.deleteByVehicleNum(vehicleNum);
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
    }

    /*차량 폐기*/
    public int discardVehicle(Long vehicleNum, HashMap<String, String> reason){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(vehicle.getRentalStatus()==99){
            return 500;
        }

        vehicle.setRentalStatus(99);
        vehicle.setDiscardReason(reason.get("reason"));
        vehicleRepository.save(vehicle);
        return 0;
    }

    /*차량 예약 신청*/
    public int applyForRent(ApplyRentalVehicleDTO rentalVehicleDTO){
        try {
            Staff renderInfo=staffRepository.findByStaffNum(rentalVehicleDTO.getStaffNum());
            Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(rentalVehicleDTO.getVehicleName()).getVehicleNum());
            LocalDateTime rentedAt=LocalDateTime.of(rentalVehicleDTO.getDateOfRental().getYear(),
                    rentalVehicleDTO.getDateOfRental().getMonth(),
                    rentalVehicleDTO.getDateOfRental().getDayOfMonth(),
                    rentalVehicleDTO.getTimeOfRental().getHour(),
                    rentalVehicleDTO.getTimeOfRental().getMinute(), 0);
            LocalDateTime returnedAt=LocalDateTime.of(rentalVehicleDTO.getExpectedReturnDate().getYear(),
                    rentalVehicleDTO.getExpectedReturnDate().getMonth(),
                    rentalVehicleDTO.getExpectedReturnDate().getDayOfMonth(),
                    rentalVehicleDTO.getExpectedReturnTime().getHour(),
                    rentalVehicleDTO.getExpectedReturnTime().getMinute(), 0);

            if(LocalDateTime.now().isAfter(rentedAt)){
                return 400;
            }
            List<VehicleReservation> reservationList=reservationRepository.findAllByReturnStatusCode(0);
            for(VehicleReservation reservation : reservationList){
                if(((reservation.getRentedAt().isBefore(rentedAt) || reservation.getRentedAt().isEqual(rentedAt)) &&
                        (reservation.getReturnedAt().isAfter(rentedAt)))
                        && vehicle.getVehicleNum().equals(reservation.getVehicle().getVehicleNum())){
                    return 500;
                }
            }

            VehicleReservation applicationRentalVehicle=new VehicleReservation();
            modelMapper.map(rentalVehicleDTO, applicationRentalVehicle);
            applicationRentalVehicle.setVehicle(vehicle);
            applicationRentalVehicle.setStaff(renderInfo);
            applicationRentalVehicle.setRentedAt(rentedAt);
            applicationRentalVehicle.setReturnedAt(returnedAt);
            reservationRepository.save(applicationRentalVehicle);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
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

    /*차량 예약 수정*/
    public int modifyVehicleReservation(Long rentNum, ApplyRentalVehicleDTO applyRentalVehicleDTO){
        VehicleReservation reservationInfo=reservationRepository.findByRentNum(rentNum);
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(applyRentalVehicleDTO.getVehicleName()).getVehicleNum());
        LocalDateTime rentedAt=LocalDateTime.of(applyRentalVehicleDTO.getDateOfRental().getYear(),
                applyRentalVehicleDTO.getDateOfRental().getMonth(), applyRentalVehicleDTO.getDateOfRental().getDayOfMonth(),
                applyRentalVehicleDTO.getTimeOfRental().getHour(),
                applyRentalVehicleDTO.getTimeOfRental().getMinute(), 0);
        LocalDateTime returnedAt=LocalDateTime.of(applyRentalVehicleDTO.getExpectedReturnDate().getYear(),
                applyRentalVehicleDTO.getExpectedReturnDate().getMonth(), applyRentalVehicleDTO.getExpectedReturnDate().getDayOfMonth(),
                applyRentalVehicleDTO.getExpectedReturnTime().getHour(),
                applyRentalVehicleDTO.getExpectedReturnTime().getMinute(), 0);
        try{
            if(LocalDateTime.now().isAfter(rentedAt)){
                return 400;
            }else if(!applyRentalVehicleDTO.getStaffNum().equals(reservationInfo.getStaff().getStaffNum())){
                return 300;
            }else if(LocalDateTime.now().isAfter(reservationInfo.getRentedAt())){
                return 303;
            }
            List<VehicleReservation> reservationList=reservationRepository.findAllByReturnStatusCode(0);
            for(VehicleReservation reservation : reservationList){
                if(((reservation.getRentedAt().isBefore(rentedAt) || reservation.getRentedAt().isEqual(rentedAt)) &&
                        (reservation.getReturnedAt().isAfter(rentedAt)))
                        && vehicle.getVehicleNum().equals(reservation.getVehicle().getVehicleNum())){
                    return 500;
                }
            }

            modelMapper.map(applyRentalVehicleDTO, reservationInfo);
            reservationInfo.setVehicle(vehicle);
            reservationInfo.setRentedAt(rentedAt);
            reservationInfo.setReturnedAt(returnedAt);
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

    /*나의 차량 예약 현황 조회*/
    public List<VehicleReservation> getMyReservation(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        List<VehicleReservation> myReservationList = reservationRepository.findByStaffAndReturnStatusCodeOrderByRentedAt(staff, 0);

        List<VehicleReservation> currentlyOnRental=new ArrayList<>();
        for(VehicleReservation myReservation : myReservationList){
            if(myReservation.getVehicle().getRentalStatus()==1){
                currentlyOnRental.add(myReservation);
            }
        }
        return currentlyOnRental;
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

    private void changeVehicleInfo(VehicleReturnDTO vehicleReturnDTO) throws Exception{
        vehicleRepository.changeRentalStatus(0, vehicleReturnDTO.getVehicleName());
        vehicleRepository.changeParkingLoc(vehicleReturnDTO.getParkingLoc(), vehicleReturnDTO.getVehicleName());
        vehicleRepository.changeDistanceDriven(vehicleReturnDTO.getDistanceDriven(), vehicleReturnDTO.getVehicleName());
    }

    private void updateReturnInfo(VehicleReturnDTO vehicleReturnDTO) throws Exception{
        VehicleReservation updatedReturnInfo=reservationRepository.findByRentNum(vehicleReturnDTO.getRentNum());
        LocalDateTime returnedAt=LocalDateTime.of(vehicleReturnDTO.getDateOfReturn().getYear(),
                vehicleReturnDTO.getDateOfReturn().getMonth(), vehicleReturnDTO.getDateOfReturn().getDayOfMonth(),
                vehicleReturnDTO.getTimeOfReturn().getHour(),
                vehicleReturnDTO.getTimeOfReturn().getMinute(), vehicleReturnDTO.getTimeOfReturn().getSecond());
        Duration duration = Duration.between(updatedReturnInfo.getReturnedAt(), returnedAt);
        String totalDrivingTime=String.valueOf(duration).replace("PT", "").replace("H", "시간").replace("M", "분");

        modelMapper.map(vehicleReturnDTO, updatedReturnInfo);
        updatedReturnInfo.setReturnStatusCode(1);
        updatedReturnInfo.setReturnedAt(returnedAt);
        updatedReturnInfo.setTotalDrivingTime(totalDrivingTime);
        reservationRepository.save(updatedReturnInfo);
    }

    /*반납 이력 전체 조회*/
    public List<VehicleReservation> getReturnList(int disposalInfo, Long vehicleNum, String baseDate, PageRequest pageRequest){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.findAll(vehicle, disposalInfo, pageRequest);
        }

        LocalDateTime startDateTime=baseDateParser.getStartDateTime(baseDate);
        LocalDateTime endDateTime=baseDateParser.getEndDateTime(baseDate);
        return reservationRepositoryImpl.findAll(vehicle, startDateTime, endDateTime, disposalInfo, pageRequest);
    }

    /*반납 이력 전체 개수 조회*/
    public Long getReturnCount(int disposalInfo, Long vehicleNum, String baseDate){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.countByReturnStatus(vehicle, disposalInfo);
        }
        LocalDateTime startDateTime=baseDateParser.getStartDateTime(baseDate);
        LocalDateTime endDateTime=baseDateParser.getEndDateTime(baseDate);

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
                return null;
            }
        }
        return imageFiles;
    }

    /*반납 이력 엑셀 다운로드*/
    public Workbook excelDownload(int disposalInfo, Long vehicleNum, String baseDate){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        List<VehicleReservation> reservationList;
        if(baseDate.equals("all")){
            reservationList = new ArrayList<>(reservationRepositoryImpl.findAll(vehicle, disposalInfo));
        }

        LocalDateTime startDateTime=baseDateParser.getStartDateTime(baseDate);
        LocalDateTime endDateTime=baseDateParser.getEndDateTime(baseDate);
        reservationList= new ArrayList<>(reservationRepositoryImpl.findAll(vehicle, startDateTime, endDateTime, disposalInfo));

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(baseDate);
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        // Header
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


        // Body
        for (VehicleReservation reservation : reservationList) {
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
        return wb;
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
}
