package com.lab.smartmobility.billie;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.TrafficCard;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.TrafficCardRepository;
import com.lab.smartmobility.billie.repository.TrafficCardReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SchedulerTest {
    /*@Autowired
    private TrafficCardReservationRepository reservationRepository;

    @Autowired
    private TrafficCardRepository cardRepository;

    @Autowired
    StaffRepository staffRepository;

    @DisplayName("교통카드 스케줄링 테스트")
    @Test
    void test1(){
        LocalDate today=LocalDate.now();

        List<TrafficCardReservation> todayCardReservationList=reservationRepository.findAllByDateOfRental(today);

        List<TrafficCard> todayCardList=new ArrayList<>();
        for(TrafficCardReservation todayCardReservation : todayCardReservationList){
            todayCardList.add(todayCardReservation.getTrafficCard());
        }

        try {
            for(TrafficCard todayCard : todayCardList){
                cardRepository.changeRentalStatus(1, todayCard.getCardNum());
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("교통카드 대여상태 변경 실패");
        }
        System.out.println("금일 교통카드 대여상태 변경 완료");
    }

    @Test
    void updateVacationCount(){
        Staff staff=staffRepository.findByStaffNum(11L);

        System.out.println(LocalDate.now().isAfter(staff.getHireDate().plusYears(1)));
    }*/
}
