package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.ApplyRentalVehicleDTO;
import com.lab.smartmobility.billie.dto.VehicleDTO;
import com.lab.smartmobility.billie.dto.VehicleReturnDTO;
import com.lab.smartmobility.billie.dto.VehicleReturnHistoryInfo;
import com.lab.smartmobility.billie.entity.ImageVehicle;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.lab.smartmobility.billie.repository.ReturnVehicleImageRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.VehicleReservationRepository;
import com.lab.smartmobility.billie.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            }else if(vehicle.getRentalStatus()==1){
                return 500;
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
            }else if(vehicle.getRentalStatus()==1 && !reservationInfo.getVehicle().getVehicleName().equals(vehicle.getVehicleName())){
                return 500;
            }else if(!applyRentalVehicleDTO.getStaffNum().equals(reservationInfo.getStaff().getStaffNum())){
                return 300;
            }else if(LocalDateTime.now().isAfter(reservationInfo.getRentedAt())){
                return 303;
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

        modelMapper.map(vehicleReturnDTO, updatedReturnInfo);
        updatedReturnInfo.setReturnStatusCode(1);
        updatedReturnInfo.setReturnStatusCode(1);
        updatedReturnInfo.setReturnStatusCode(1);
        reservationRepository.save(updatedReturnInfo);
    }

    /*반납 이력 전체 조회*/
    public List<VehicleReturnHistoryInfo> getReturnList(PageRequest pageRequest){
        List<VehicleReservation> vehicleReservations=reservationRepository.findAllByReturnStatusCodeOrderByRentNumDesc(1, pageRequest);
        List<VehicleReturnHistoryInfo> ReturnHistoryList=new ArrayList<>();

        for(VehicleReservation vehicleReservation : vehicleReservations){
            VehicleReturnHistoryInfo returnHistoryInfo=new VehicleReturnHistoryInfo();
            returnHistoryInfo.setRender(vehicleReservation.getStaff().getName());
            returnHistoryInfo.setVehicleName(vehicleReservation.getVehicle().getVehicleName());
            modelMapper.map(vehicleReservation, returnHistoryInfo);

            ReturnHistoryList.add(returnHistoryInfo);
        }
       return ReturnHistoryList;
    }

    /*반납 이력 전체 개수 조회*/
    public long getReturnCount(){
        return reservationRepository.countByReturnStatusCode(1);
    }

    /*반납 이력 별 상세 조회*/
    public VehicleReturnHistoryInfo getReturn(Long rentNum){
        VehicleReservation vehicleReservation=reservationRepository.findByRentNumAndReturnStatusCode(rentNum, 1);
        VehicleReturnHistoryInfo returnHistoryInfo=new VehicleReturnHistoryInfo();
        returnHistoryInfo.setRender(vehicleReservation.getStaff().getName());
        returnHistoryInfo.setVehicleName(vehicleReservation.getVehicle().getVehicleName());
        modelMapper.map(vehicleReservation, returnHistoryInfo);

        return returnHistoryInfo;
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
