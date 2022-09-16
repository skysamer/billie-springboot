package com.lab.smartmobility.billie.util;

import com.lab.smartmobility.billie.vacation.domain.Vacation;
import com.lab.smartmobility.billie.vacation.repository.VacationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@SpringBootTest
public class DBTest {
    @Autowired
    VacationRepository vacationRepository;

    @Test
    void test(){
        List<Long> idList = new ArrayList<>();
        idList.add(113L);
        idList.add(115L);
        idList.add(116L);
        List<Vacation> list = vacationRepository.findByVacationIdIn(idList);
        System.out.println(list.toString());
    }
}
