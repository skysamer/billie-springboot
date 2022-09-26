package com.lab.smartmobility.billie.util;

import com.lab.smartmobility.billie.vacation.domain.Vacation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class DateUtilTest {
    @Test
    @DisplayName("날짜 계산")
    void calculateDateTime(){
        LocalDate date1 = LocalDate.of(2021, 6, 1);
        LocalDate date2 = LocalDate.of(2022, 5, 31);

        long period = ChronoUnit.DAYS.between(date1, date2);
        System.out.println("period = " + period);
    }

    @Test
    @DisplayName("문자열 추출")
    void getClassNameToString(){
        Vacation vacation = new Vacation();
        System.out.println(vacation.getClass().getSimpleName());
    }

    @Test
    @DisplayName("스레드 테스트")
    void thread() throws InterruptedException {
        System.out.println("hello");
        System.out.println(Thread.activeCount());
        System.out.println("bye");
    }

    @Test
    @DisplayName("휴가계산 테스트")
    void vacationCount() {
        LocalDate start = LocalDate.of(2022,8,1);
        LocalDate end = LocalDate.of(2022,10, 30);

        Period period = Period.between(start, end);
//        System.out.println("period = " + period.getMonths());
        System.out.println("period = " + (double) ChronoUnit.MONTHS.between(start, end));
    }

    @Test
    @DisplayName("추가근무 계산 테스트")
    void overtimeCount() {
        LocalTime start = LocalTime.of(18, 0);
        LocalTime end = LocalTime.of(20, 30);

        Duration duration = Duration.between(start, end);
        System.out.println("duration = " + (double) duration.getSeconds() / (60 * 60));
    }
}
