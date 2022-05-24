package com.lab.smartmobility.billie.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Calendar;

@Component
public class BaseDateParser {
    public LocalDateTime getStartDateTime(String baseDate){
        Calendar cal=Calendar.getInstance();
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);
        int end=cal.getActualMaximum(Calendar.DATE);

        return LocalDateTime.of(year, month, 1, 0, 0, 0);
    }

    public LocalDateTime getEndDateTime(String baseDate){
        Calendar cal=Calendar.getInstance();
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);
        int end=cal.getActualMaximum(Calendar.DATE);

        return LocalDateTime.of(year, month, end, 23, 59, 59);
    }
}
