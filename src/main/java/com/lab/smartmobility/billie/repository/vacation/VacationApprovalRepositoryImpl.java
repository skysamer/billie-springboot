package com.lab.smartmobility.billie.repository.vacation;

import com.lab.smartmobility.billie.util.DateTimeUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.lab.smartmobility.billie.entity.QVacation.vacation;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VacationApprovalRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    private BooleanExpression baseYearEq(String baseYear) {
        if(baseYear.equals("all")){
            return null;
        }

        LocalDate startDate=dateTimeUtil.getStartDate(baseYear);
        LocalDate endDate=dateTimeUtil.getEndDate(baseYear);
        return vacation.startDate.between(startDate, endDate);
    }

    private BooleanExpression vacationTypeEq(String vacationType) {
        return vacationType.equals("all") ? null : vacation.vacationType.eq(vacationType);
    }
}
