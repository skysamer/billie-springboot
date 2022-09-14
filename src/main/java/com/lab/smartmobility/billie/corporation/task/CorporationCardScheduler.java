package com.lab.smartmobility.billie.corporation.task;

import com.lab.smartmobility.billie.corporation.domain.Application;
import com.lab.smartmobility.billie.corporation.domain.CorporationCard;
import com.lab.smartmobility.billie.corporation.repository.ApplicationRepository;
import com.lab.smartmobility.billie.corporation.repository.CorporationCardRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CorporationCardScheduler {
    private final ApplicationRepository applicationRepository;
    private final CorporationCardRepository cardRepository;
    private final Log log;

    @Scheduled(cron = "0 00,30 * * * *")
    private void updateCardRentalStatus(){
        LocalDateTime now=LocalDateTime.now();

        LocalDate nowDate=LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth());
        LocalTime nowTime=LocalTime.of(now.getHour(), now.getMinute(), 0);

        List<Application> applicationList=applicationRepository.findAllByStartDateAndStartTimeAndCorporationCardNotNull(nowDate, nowTime);
        if(applicationList.size()==0){
            log.info("현재시간에 해당하는 법인카드 예약이 존재하지 않음");
            return;
        }

        for(Application application : applicationList){
            CorporationCard corporationCard=application.getCorporationCard();
            corporationCard.setRentalStatus(1);
            cardRepository.save(corporationCard);
        }
        log.info("법인카드 예약상태 변경 완료");
    }

}
