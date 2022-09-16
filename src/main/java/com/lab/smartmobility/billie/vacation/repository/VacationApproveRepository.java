package com.lab.smartmobility.billie.vacation.repository;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.vacation.domain.ApprovalStatus;
import com.lab.smartmobility.billie.vacation.dto.QVacationApproveListForm;
import com.lab.smartmobility.billie.vacation.dto.VacationApproveListForm;
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

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VacationApproveRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    /*부서장의 휴가승인 요청 목록 조회*/
    public PageResult<VacationApproveListForm> getApproveListByManagerResult(String baseDate, String department, String keyword, int isToggleOn, Pageable pageable){
        List<VacationApproveListForm> content = getApproveListByManager(baseDate, department, keyword, isToggleOn, pageable);
        long count = countApproveListByManager(baseDate, department, keyword, isToggleOn);
        return new PageResult<>(content, count);
    }

    private List<VacationApproveListForm> getApproveListByManager(String baseDate, String department, String keyword, int isToggleOn, Pageable pageable){
        return jpaQueryFactory
                .select(new QVacationApproveListForm(vacation.vacationId, vacation.vacationType,
                        vacation.startDate, vacation.endDate, vacation.workAt, vacation.homeAt, vacation.reason,
                        vacation.approvalStatus.stringValue(), vacation.staff.name))
                .from(vacation)
                .where(vacation.staff.department.eq(department)
                        .and(baseDateEq(baseDate))
                        .and(keywordLike(keyword))
                        .and(isToggleOnNe(isToggleOn))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vacation.approvalStatus.desc(), vacation.vacationId.desc())
                .fetch();
    }

    private long countApproveListByManager(String baseDate, String department, String keyword, int isToggleOn){
        return jpaQueryFactory
                .select(new QVacationApproveListForm(vacation.vacationId, vacation.vacationType,
                        vacation.startDate, vacation.endDate, vacation.workAt, vacation.homeAt, vacation.reason,
                        vacation.approvalStatus.stringValue(), vacation.staff.name))
                .from(vacation)
                .where(vacation.staff.department.eq(department)
                        .and(baseDateEq(baseDate))
                        .and(keywordLike(keyword))
                        .and(isToggleOnNe(isToggleOn))
                )
                .stream().count();
    }

    /*부서장 휴가 승인*/
    public void approveByManager(List<Long> vacationIdList){
        jpaQueryFactory
                .update(vacation)
                .set(vacation.approvalStatus, ApprovalStatus.TEAM)
                .where(vacation.vacationId.in(vacationIdList))
                .execute();
    }

    /*관리자 휴가승인 요청관리 내역 조회*/
    private List<VacationApproveListForm> getApproveListByAdmin(String baseDate, String department, String keyword, int isToggleOn, Pageable pageable){
        return jpaQueryFactory
                .select(new QVacationApproveListForm(vacation.vacationId, vacation.vacationType,
                        vacation.startDate, vacation.endDate, vacation.workAt, vacation.homeAt, vacation.reason,
                        vacation.approvalStatus.stringValue(), vacation.staff.name))
                .from(vacation)
                .where(vacation.staff.department.eq(department)
                        .and(baseDateEq(baseDate))
                        .and(keywordLike(keyword))
                        .and(isToggleOnNe(isToggleOn))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vacation.approvalStatus.desc(), vacation.vacationId.desc())
                .fetch();
    }

    private BooleanExpression baseDateEq(String baseYear) {
        if(baseYear.equals("all")){
            return null;
        }

        LocalDate startDate = dateTimeUtil.getStartDate(baseYear);
        LocalDate endDate = dateTimeUtil.getEndDate(baseYear);
        return vacation.startDate.between(startDate, endDate);
    }

    private BooleanExpression departmentEq(String department){
        return department.equals("all") ? null : vacation.staff.department.eq(department);
    }

    private BooleanExpression keywordLike(String keyword) {
        return keyword.equals("all") ? null : vacation.staff.name.eq(keyword);
    }

    private BooleanExpression isToggleOnNe(int isToggleOn){
        return isToggleOn == 0 ?
                vacation.approvalStatus.ne(ApprovalStatus.CANCEL).and(vacation.approvalStatus.ne(ApprovalStatus.CANCEL)) : null;
    }
}
