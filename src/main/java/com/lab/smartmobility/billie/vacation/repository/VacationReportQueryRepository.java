package com.lab.smartmobility.billie.vacation.repository;

import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.vacation.dto.QVacationReportForm;
import com.lab.smartmobility.billie.vacation.dto.VacationReportForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.vacation.domain.QVacationReport.vacationReport;
import static com.lab.smartmobility.billie.vacation.domain.QVacation.vacation;
import static com.lab.smartmobility.billie.staff.domain.QStaff.staff;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VacationReportQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    public List<VacationReportForm> getReport(String baseDate, String department, String name){
        return jpaQueryFactory
                .select(new QVacationReportForm(vacation.count, vacation.startDate, vacation.endDate,
                        vacation.vacationType.as("note"), vacation.reason, staff.staffNum, staff.name, staff.department, staff.vacationCount))
                .from(vacation)
                .innerJoin(staff)
                .on(vacation.staff.eq(staff))
                .where(Expressions.asBoolean(true).isTrue()
                        .and(baseDateEq(baseDate))
                        .and((departmentEq(department)))
                        .and(nameEq(name))
                )
                .fetch();
    }

    private BooleanExpression departmentEq(String department){
        return department.equals("all") ? null : vacationReport.staff.department.eq(department);
    }

    private BooleanExpression nameEq(String name){
        return name.equals("all") ? null : vacationReport.staff.name.eq(name);
    }

    private BooleanExpression baseDateEq(String baseYear) {
        LocalDate startDate = dateTimeUtil.getStartDate(baseYear);
        LocalDate endDate = dateTimeUtil.getEndDate(baseYear);
        return (vacationReport.startDate.between(startDate, endDate).or(vacationReport.endDate.between(startDate, endDate)));
    }
}
