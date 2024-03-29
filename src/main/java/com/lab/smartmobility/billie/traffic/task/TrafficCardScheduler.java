package com.lab.smartmobility.billie.traffic.task;

import com.lab.smartmobility.billie.traffic.domain.TrafficCard;
import com.lab.smartmobility.billie.traffic.domain.TrafficCardReservation;
import com.lab.smartmobility.billie.traffic.repository.TrafficCardRepository;
import com.lab.smartmobility.billie.traffic.repository.TrafficCardReservationRepository;
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
public class TrafficCardScheduler {
    private final TrafficCardRepository cardRepository;
    private final TrafficCardReservationRepository reservationRepository;
    private final Log log= LogFactory.getLog(getClass());

    @Scheduled(cron = "0 00,30 * * * *")
    private void updateCardRentalStatus(){
        LocalDate date=LocalDate.now();
        LocalTime time=LocalTime.now();
        LocalDateTime now=LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                time.getHour(), time.getMinute(), 0);
        try{
            List<TrafficCardReservation> todayReservations = reservationRepository.findAllByRentedAt(now);
            if(todayReservations.size()==0){
                log.info("현재 시각에 해당하는 교통카드 예약 없음");
                return;
            }

            for(TrafficCardReservation todayReservation : todayReservations){
                TrafficCard rentedCard=todayReservation.getTrafficCard();
                rentedCard.rent(1);
                cardRepository.save(rentedCard);
                log.info("교통카드 대여상태 변경 완료");
            }
            log.info("finished......");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
