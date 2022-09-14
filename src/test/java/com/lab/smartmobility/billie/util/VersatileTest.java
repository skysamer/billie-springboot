package com.lab.smartmobility.billie.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Period;

public class VersatileTest {

    @Test
    @DisplayName("다용도 테스트")
    void versatile(){
        Period period = Period.between(LocalDate.of(2022, 8, 30), LocalDate.of(2022, 9, 2));
        System.out.println(period.getDays());
    }
}
