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
        LocalDate now = LocalDate.now();
        LocalDate test = LocalDate.of(2010, 12, 5);
        System.out.println(now.getDayOfMonth() == test.getDayOfMonth());
        System.out.println(now.getMonth().equals(test.getMonth()));
    }
}
