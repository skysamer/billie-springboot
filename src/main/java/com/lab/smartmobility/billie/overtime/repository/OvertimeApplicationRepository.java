package com.lab.smartmobility.billie.overtime.repository;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApplicationListForm;
import com.lab.smartmobility.billie.overtime.dto.QOvertimeApplicationListForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.overtime.domain.QOvertime.overtime;
import static com.lab.smartmobility.billie.vacation.domain.QVacation.vacation;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OvertimeApplicationRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    /*나의 추가근무 신청 목록 조회*/
    public PageResult<OvertimeApplicationListForm> getApplicationListPaging(String email, String baseDate, Pageable pageable){
        List<OvertimeApplicationListForm> content = getApplicationList(email, baseDate, pageable);
        long count = getApplicationCount(email, baseDate);
        return new PageResult<>(content, count);
    }

    private List<OvertimeApplicationListForm> getApplicationList(String email, String baseDate, Pageable pageable){
        return jpaQueryFactory
                .select(new QOvertimeApplicationListForm(overtime.id, overtime.dayOfOvertime,
                        overtime.startTime, overtime.endTime, overtime.isMeal, overtime.content,
                        overtime.approvalStatus, overtime.subTime, overtime.admitTime))
                .from(overtime)
                .where(overtime.staff.email.eq(email)
                        .and(baseDateEq(baseDate))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(overtime.id.desc())
                .fetch();
    }

    private long getApplicationCount(String email, String baseDate){
        return jpaQueryFactory
                .select(new QOvertimeApplicationListForm(overtime.id, overtime.dayOfOvertime,
                        overtime.startTime, overtime.endTime, overtime.isMeal, overtime.content,
                        overtime.approvalStatus, overtime.subTime, overtime.admitTime))
                .from(overtime)
                .where(overtime.staff.email.eq(email)
                        .and(baseDateEq(baseDate))
                )
                .stream().count();
    }

    private BooleanExpression baseDateEq(String baseYear) {
        if(baseYear.equals("all")){
            return null;
        }

        LocalDate startDate = dateTimeUtil.getStartDate(baseYear);
        LocalDate endDate = dateTimeUtil.getEndDate(baseYear);
        return overtime.dayOfOvertime.between(startDate, endDate);
    }
}
