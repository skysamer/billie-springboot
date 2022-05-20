package com.lab.smartmobility.billie;

import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.lab.smartmobility.billie.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@SpringBootTest
public class Test1 {
    /*@Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleReservationRepository applyRepository;

    @Autowired
    private ReturnVehicleImageRepository imageRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private VacationRepository vacationRepository;

    @DisplayName("1. 테스트")
    @Test
    void test1(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        String sunday=sdf.format(c.getTime());
        System.out.println(sunday);

        c.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
        String saturday=sdf.format(c.getTime());
        System.out.println(saturday);
    }

    @Test
    @Transactional
    void testeee(){
        System.out.println(vacationRepository.findAll());
    }*/

}
