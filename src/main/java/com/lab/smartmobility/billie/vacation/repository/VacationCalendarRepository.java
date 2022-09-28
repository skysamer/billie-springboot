package com.lab.smartmobility.billie.vacation.repository;

import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.vacation.dto.QVacationCalendarForm;
import com.lab.smartmobility.billie.vacation.dto.VacationCalendarForm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.vacation.domain.ApprovalStatus.*;
import static com.lab.smartmobility.billie.vacation.domain.QVacation.vacation;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VacationCalendarRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    public List<VacationCalendarForm> getCalendarList(LocalDate startDate, LocalDate endDate){
        return jpaQueryFactory
                .select(new QVacationCalendarForm(vacation.vacationId, vacation.vacationType,
                        vacation.startDate, vacation.endDate, vacation.staff.name, vacation.staff.department))
                .from(vacation)
                .where((vacation.startDate.between(startDate, endDate)
                        .or(vacation.endDate.between(startDate, endDate)))
                        .and(vacation.approvalStatus.ne(WAITING))
                        .and(vacation.approvalStatus.ne(CANCEL))
                        .and(vacation.approvalStatus.ne(COMPANION))
                )
                .fetch();
    }
}
