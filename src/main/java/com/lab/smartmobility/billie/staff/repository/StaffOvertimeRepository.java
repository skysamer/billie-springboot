package com.lab.smartmobility.billie.staff.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.lab.smartmobility.billie.staff.domain.QStaff.staff;

@Repository
@Transactional
@RequiredArgsConstructor
public class StaffOvertimeRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public void initializeOvertimeHour(){
        jpaQueryFactory
                .update(staff)
                .set(staff.overtimeHour, 0.0)
                .execute();
    }
}
