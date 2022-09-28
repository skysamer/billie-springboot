package com.lab.smartmobility.billie.overtime.repository;

import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.overtime.domain.ApprovalStatus;
import com.lab.smartmobility.billie.overtime.dto.OvertimeReportForm;
import com.lab.smartmobility.billie.overtime.dto.QOvertimeReportForm;
import com.lab.smartmobility.billie.vacation.dto.QVacationReportForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.overtime.domain.QOvertime.overtime;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OvertimeReportRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    /*추가근무 월별 리포트*/
    public List<OvertimeReportForm> getReport(String baseDate, String department, String name){
        return jpaQueryFactory.
                select(new QOvertimeReportForm(overtime.id, overtime.staff.staffNum, overtime.staff.name,
                        overtime.staff.department, overtime.staff.overtimeHour, overtime.dayOfOvertime,
                        overtime.isMeal, overtime.subTime, overtime.admitTime))
                .from(overtime)
                .where(overtime.approvalStatus.ne(ApprovalStatus.WAITING)
                        .and(baseDateEq(baseDate))
                        .and(departmentEq(department))
                        .and(nameLike(name))
                )
                .fetch();
    }

    private BooleanExpression baseDateEq(String baseYear) {
        LocalDate startDate = dateTimeUtil.getStartDate(baseYear);
        LocalDate endDate = dateTimeUtil.getEndDate(baseYear);
        return overtime.dayOfOvertime.between(startDate, endDate);
    }

    private BooleanExpression departmentEq(String department){
        return department.equals("all") ? null : overtime.staff.department.eq(department);
    }

    private BooleanExpression nameLike(String name) {
        return name.equals("all") ? null : overtime.staff.name.eq(name);
    }
}
