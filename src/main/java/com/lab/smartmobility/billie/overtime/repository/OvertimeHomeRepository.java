package com.lab.smartmobility.billie.overtime.repository;

import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.overtime.dto.OvertimeMonthlyForm;
import com.lab.smartmobility.billie.overtime.dto.QOvertimeMonthlyForm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.overtime.domain.ApprovalStatus.COMPANION;
import static com.lab.smartmobility.billie.overtime.domain.ApprovalStatus.WAITING;
import static com.lab.smartmobility.billie.overtime.domain.QOvertime.overtime;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OvertimeHomeRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    public List<OvertimeMonthlyForm> getOvertimeMonthly(LocalDate startDate, LocalDate endDate) {
        return jpaQueryFactory
                .select(new QOvertimeMonthlyForm(overtime.id, overtime.dayOfOvertime,
                        overtime.staff.name, overtime.staff.department))
                .from(overtime)
                .where(overtime.dayOfOvertime.between(startDate, endDate)
                        .and(overtime.approvalStatus.ne(WAITING))
                        .and(overtime.approvalStatus.ne(COMPANION))
                )
                .fetch();
    }
}
