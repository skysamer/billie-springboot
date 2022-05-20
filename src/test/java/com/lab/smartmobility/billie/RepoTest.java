package com.lab.smartmobility.billie;

import com.lab.smartmobility.billie.repository.VehicleReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepoTest {
    @Autowired
    private VehicleReservationRepository reservationRepository;

    @DisplayName("1. 테스트")
    @Test
    void test(){

    }
}
