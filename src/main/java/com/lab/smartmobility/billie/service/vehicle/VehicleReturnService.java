package com.lab.smartmobility.billie.service.vehicle;

import com.lab.smartmobility.billie.dto.vehicle.VehicleReturnDTO;
import com.lab.smartmobility.billie.entity.ImageVehicle;
import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.lab.smartmobility.billie.repository.ReturnVehicleImageRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    /*?????? ?????? ??????*/
    public int returnVehicle(VehicleReturnDTO vehicleReturnDTO){
        try{
            changeVehicleInfo(vehicleReturnDTO);
            updateReturnInfo(vehicleReturnDTO);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    @Deprecated
    private List<String> saveImageFile(List<MultipartFile> images) {
        List<String> imageInformation=new ArrayList<>();

        //String uploadFolder="C:\\vehicle";  //?????? ????????????
        String uploadFolder="";

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
                return new ArrayList<>();
            }
        }
        return imageInformation;
    }

    private String getFolder() {
        LocalDate now=LocalDate.now();
        String str=String.valueOf(now);
        return str.replace("-", File.separator);
    }

    /*?????? ??? ?????? ?????? ????????????*/
    private void changeVehicleInfo(VehicleReturnDTO vehicleReturnDTO) {
        Vehicle vehicle = vehicleRepository.findByVehicleNum(vehicleRepository.findByVehicleName(vehicleReturnDTO.getVehicleName()).getVehicleNum());
        vehicle.update(0, vehicleReturnDTO.getParkingLoc(), vehicleReturnDTO.getDistanceDriven());
        vehicleRepository.save(vehicle);
    }

    /*?????? ?????? ????????????*/
    private void updateReturnInfo(VehicleReturnDTO vehicleReturnDTO) {
        VehicleReservation updatedReturnInfo=reservationRepository.findByRentNum(vehicleReturnDTO.getRentNum());
        LocalDateTime returnedAt = dateTimeUtil.combineDateAndTime(vehicleReturnDTO.getDateOfReturn(), vehicleReturnDTO.getTimeOfReturn());

        modelMapper.map(vehicleReturnDTO, updatedReturnInfo);
        updatedReturnInfo.update(returnedAt);
        reservationRepository.save(updatedReturnInfo);
    }

    /*?????? ?????? ?????? ??????*/
    public List<VehicleReservation> getReturnList(int disposalInfo, Long vehicleNum, String baseDate, PageRequest pageRequest){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.findAll(vehicle, disposalInfo, pageRequest);
        }

        LocalDateTime startDateTime= dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime= dateTimeUtil.getEndDateTime(baseDate);
        return reservationRepositoryImpl.findAll(vehicle, startDateTime, endDateTime, disposalInfo, pageRequest);
    }

    /*?????? ?????? ?????? ?????? ??????*/
    public Long getReturnCount(int disposalInfo, Long vehicleNum, String baseDate){
        Vehicle vehicle=vehicleRepository.findByVehicleNum(vehicleNum);
        if(baseDate.equals("all")){
            return reservationRepositoryImpl.countByReturnStatus(vehicle, disposalInfo);
        }
        LocalDateTime startDateTime= dateTimeUtil.getStartDateTime(baseDate);
        LocalDateTime endDateTime= dateTimeUtil.getEndDateTime(baseDate);

        return reservationRepositoryImpl.countByReturnStatus(vehicle, startDateTime, endDateTime, disposalInfo);
    }

    /*?????? ?????? ??? ?????? ??????*/
    public VehicleReservation getReturn(Long rentNum){
        return reservationRepository.findByRentNumAndReturnStatusCode(rentNum, 1);
    }

    /*?????? ?????? ??? ????????? ?????? ??????*/
    public List<byte[]> getReturnImages(Long rentNum){
        VehicleReservation returnVehicle=reservationRepository.findByRentNum(rentNum);

        List<ImageVehicle> imageVehicles=imageRepository.findAllByVehicleReservation(returnVehicle);
        List<String> imageOriginPaths=new ArrayList<>();

        for(ImageVehicle image : imageVehicles){
            //imageOriginPaths.add(image.getImagePath()+"\\"+image.getOriginImageName()); // ????????? ?????????
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

    /*?????? ?????? ?????? ????????????*/
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

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(baseDate);
        Row row;
        Cell cell;
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("??????");
        cell = row.createCell(1);
        cell.setCellValue("?????????");
        cell = row.createCell(2);
        cell.setCellValue("???????????? ??????");
        cell = row.createCell(3);
        cell.setCellValue("?????????");
        cell = row.createCell(4);
        cell.setCellValue("???????????? ??????");

        cell = row.createCell(5);
        cell.setCellValue("??? ?????? ??????");
        cell = row.createCell(6);
        cell.setCellValue("?????????");
        cell = row.createCell(7);
        cell.setCellValue("??????(??????)");
        cell = row.createCell(8);
        cell.setCellValue("?????? ??? ?????????");
        cell = row.createCell(9);
        cell.setCellValue("????????????");

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
