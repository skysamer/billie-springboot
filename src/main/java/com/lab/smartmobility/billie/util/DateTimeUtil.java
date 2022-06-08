package com.lab.smartmobility.billie.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class DateTimeUtil {
    private final Calendar cal=Calendar.getInstance();

    public LocalDateTime getStartDateTime(String baseDate){
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);

        return LocalDateTime.of(year, month, 1, 0, 0, 0);
    }

    public LocalDateTime getEndDateTime(String baseDate){
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);
        int end=cal.getActualMaximum(Calendar.DATE);

        return LocalDateTime.of(year, month, end, 23, 59, 59);
    }

    public List<LocalDate> getStartDateAndEndDate(LocalDate baseDate){
        int year=baseDate.getYear();
        int month=baseDate.getMonthValue();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);
        int end=cal.getActualMaximum(Calendar.DATE);
        LocalDate startDate=LocalDate.of(year, month, 1);
        LocalDate endDate=LocalDate.of(year, month, end);

        return new ArrayList<>(List.of(startDate, endDate));
    }

    public LocalDate getStartDate(String baseDate){
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);

        return LocalDate.of(year, month, 1);
    }

    public LocalDate getEndDate(String baseDate){
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);
        int end=cal.getActualMaximum(Calendar.DATE);

        return LocalDate.of(year, month, end);
    }
}
