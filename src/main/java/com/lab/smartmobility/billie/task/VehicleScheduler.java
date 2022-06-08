package com.lab.smartmobility.billie.task;

import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.lab.smartmobility.billie.repository.vehicle.VehicleReservationRepository;
import com.lab.smartmobility.billie.repository.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VehicleScheduler {
    private final VehicleRepository vehicleRepository;
    private final VehicleReservationRepository reservationRepository;
    private final Log log = LogFactory.getLog(getClass());

    /*매일 자정에 현재 날짜에 예약되어 있는 차량을 조회하고 있다면 대여상태를 변경*/
    @Scheduled(cron = "0 00,30 * * * *")
    private void rentalStatusUpdate(){
        LocalDate date=LocalDate.now();
        LocalTime time=LocalTime.now();
        LocalDateTime now=LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                time.getHour(), time.getMinute(), 0);
        try{
            List<VehicleReservation> todayReservations = reservationRepository.findAllByRentedAt(now);
            if(todayReservations.size()==0){
                log.info("현재 시각에 해당하는 예약 없음");
                return;
            }

            for(VehicleReservation todayReservation : todayReservations){
                Vehicle rentedVehicle=todayReservation.getVehicle();
                rentedVehicle.setRentalStatus(1);
                vehicleRepository.save(rentedVehicle);
                log.info("차량 대여상태 변경 완료");
            }
            log.info("finished......");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
