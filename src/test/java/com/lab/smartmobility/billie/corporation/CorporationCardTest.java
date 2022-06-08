package com.lab.smartmobility.billie.corporation;

import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.entity.corporation.CorporationCard;
import com.lab.smartmobility.billie.repository.corporation.ApplicationRepository;
import com.lab.smartmobility.billie.repository.corporation.CorporationCardRepository;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
public class CorporationCardTest {
    /*@Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    Log log;

    @Autowired
    CorporationCardRepository cardRepository;

    @Test
    void test(){
        LocalDateTime now=LocalDateTime.now();

        LocalDate nowDate=LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth());
        LocalTime nowTime=LocalTime.of(now.getHour(), now.getMinute(), 0);

        if(!applicationRepository.existsByStartDateAndStartTimeAndCorporationCardIsNotNull(nowDate, nowTime)){
            log.info("현재시간에 해당하는 법인카드 예약이 존재하지 않음");
            return;
        }
        List<Application> applicationList=applicationRepository.findAllByStartDateAndStartTimeAndCorporationCardNotNull(nowDate, nowTime);
        for(Application application : applicationList){
            CorporationCard corporationCard=application.getCorporationCard();
            corporationCard.setRentalStatus(1);
            cardRepository.save(corporationCard);
        }
        log.info("법인카드 예약상태 변경 완료");
    }*/
}
