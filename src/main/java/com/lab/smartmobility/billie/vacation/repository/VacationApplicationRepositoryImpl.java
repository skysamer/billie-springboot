package com.lab.smartmobility.billie.vacation.repository;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.vacation.dto.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.vacation.domain.QVacation.vacation;
import static com.lab.smartmobility.billie.staff.domain.QStaff.staff;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VacationApplicationRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    public PageResult<VacationApplicationListForm> getMyApplicationList(Long staffNum, String baseDate, String vacationType, Pageable pageable){
        List<VacationApplicationListForm> content = getApplicationList(staffNum, baseDate, vacationType, pageable);
        long count = getApplicationCount(staffNum, baseDate, vacationType);
        return new PageResult<>(content, count);
    }

    /*나의 신청 목록 조회*/
    private List<VacationApplicationListForm> getApplicationList(Long staffNum, String baseDate, String vacationType, Pageable pageable){
        return jpaQueryFactory
                .select(new QVacationApplicationListForm(vacation.vacationId, vacation.startDate, vacation.endDate,
                        vacation.reason, vacation.vacationType, vacation.approvalStatus))
                .from(vacation)
                .leftJoin(staff)
                .on(vacation.staff.staffNum.eq(staff.staffNum))
                .where(staff.staffNum.eq(staffNum)
                        .and(baseYearEq(baseDate))
                        .and(vacationTypeEq(vacationType))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vacation.vacationId.desc())
                .fetch();
    }

    private long getApplicationCount(Long staffNum, String baseDate, String vacationType){
        return jpaQueryFactory
                .select(new QVacationApplicationListForm(vacation.vacationId, vacation.startDate, vacation.endDate,
                        vacation.reason, vacation.vacationType, vacation.approvalStatus))
                .from(vacation)
                .leftJoin(staff)
                .on(vacation.staff.staffNum.eq(staff.staffNum))
                .where(staff.staffNum.eq(staffNum)
                        .and(baseYearEq(baseDate))
                        .and(vacationTypeEq(vacationType))
                )
                .stream().count();
    }

    /*나의 신청 목록 상세 조회*/
    public VacationApplicationDetailsForm findById(Long vacationId){
        return jpaQueryFactory
                .select(new QVacationApplicationDetailsForm(vacation.vacationId, staff.name, staff.department,
                        vacation.startDate, vacation.endDate, vacation.workAt, vacation.homeAt, vacation.contact,
                        vacation.reason, vacation.vacationType, vacation.approvalStatus))
                .from(vacation)
                .innerJoin(staff)
                .on(vacation.staff.eq(staff))
                .where(vacation.vacationId.eq(vacationId))
                .fetchFirst();
    }

    /*나의 최근 휴가 신청 목록*/
    public List<MyRecentVacationForm> findMyRecentVacationList(Long staffNum){
        return jpaQueryFactory
                .select(new QMyRecentVacationForm(vacation.vacationId, vacation.startDate, vacation.endDate,
                        vacation.workAt, vacation.homeAt, vacation.vacationType, vacation.approvalStatus))
                .from(vacation)
                .leftJoin(staff)
                .on(vacation.staff.staffNum.eq(staff.staffNum))
                .where(staff.staffNum.eq(staffNum))
                .limit(4)
                .orderBy(vacation.startDate.desc())
                .fetch();
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
