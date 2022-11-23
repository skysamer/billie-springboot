package com.lab.smartmobility.billie.overtime.repository;

import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.overtime.domain.ApprovalStatus;
import com.lab.smartmobility.billie.overtime.dto.OvertimeReportForm;
import com.lab.smartmobility.billie.overtime.dto.QOvertimeReportForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.overtime.domain.QOvertime.overtime;
import static com.lab.smartmobility.billie.staff.domain.QStaff.staff;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OvertimeReportRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;

    /*추가근무 월별 리포트*/
    public List<OvertimeReportForm> getReport(String baseDate, String department, String name){
        return jpaQueryFactory.
                select(new QOvertimeReportForm(overtime.id, staff.staffNum, staff.name,
                        staff.department, staff.overtimeHour, overtime.dayOfOvertime,
                        overtime.isMeal, overtime.subTime, overtime.admitTime, overtime.startTime,
                        overtime.endTime, overtime.content))
                .from(overtime)
                .join(staff).on(overtime.staff.eq(staff))
                .where(overtime.approvalStatus.eq(ApprovalStatus.FINAL)
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
