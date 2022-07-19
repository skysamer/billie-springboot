package com.lab.smartmobility.billie.repository.vacation;

import com.lab.smartmobility.billie.dto.PageResult;
import com.lab.smartmobility.billie.entity.Vacation;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.entity.QVacation.vacation;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VacationApplicationRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    public PageResult<Vacation> getMyApplicationList(Long staffNum, String baseDate, String vacationType, Pageable pageable){
        List<Vacation> content = getApplicationList(staffNum, baseDate, vacationType, pageable);
        long count = getApplicationListCount(staffNum, baseDate, vacationType);
        return new PageResult<>(content, count);
    }

    private List<Vacation> getApplicationList(Long staffNum, String baseDate, String vacationType, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(vacation)
                .where(vacation.staff.staffNum.eq(staffNum)
                        .and(baseYearEq(baseDate))
                        .and(vacationTypeEq(vacationType))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vacation.vacationId.desc())
                .fetch();
    }

    private long getApplicationListCount(Long staffNum, String baseDate, String vacationType){
        return jpaQueryFactory
                .selectFrom(vacation)
                .where(vacation.staff.staffNum.eq(staffNum)
                        .and(baseYearEq(baseDate))
                        .and(vacationTypeEq(vacationType))
                )
                .stream().count();
    }

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
