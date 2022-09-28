package com.lab.smartmobility.billie.overtime.repository;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.overtime.domain.ApprovalStatus;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApproveListForm;
import com.lab.smartmobility.billie.overtime.dto.QOvertimeApproveListForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.overtime.domain.QOvertime.overtime;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OvertimeApproveRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    /*부서장의 추가근무 승인 요청 목록 조회*/
    public PageResult<OvertimeApproveListForm> getApproveListPagingByManager(String baseDate, String department, String name, Pageable pageable){
        List<OvertimeApproveListForm> content = getApproveListByManager(baseDate, department, name, pageable);
        long count = getApproveCountByManager(baseDate, department, name);
        return new PageResult<>(content, count);
    }

    private List<OvertimeApproveListForm> getApproveListByManager(String baseDate, String department, String name, Pageable pageable){
        return jpaQueryFactory
                .select(new QOvertimeApproveListForm(overtime.id, overtime.staff.name, overtime.staff.employeeNumber,
                        overtime.dayOfOvertime, overtime.startTime, overtime.endTime, overtime.isMeal, overtime.content,
                        overtime.approvalStatus, overtime.subTime, overtime.admitTime))
                .from(overtime)
                .where(overtime.staff.department.eq(department)
                        .and(baseDateEq(baseDate))
                        .and(nameLike(name))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(overtime.approvalStatus.desc(), overtime.id.desc())
                .fetch();
    }

    private long getApproveCountByManager(String baseDate, String department, String name){
        return jpaQueryFactory
                .select(new QOvertimeApproveListForm(overtime.id, overtime.staff.name, overtime.staff.employeeNumber,
                        overtime.dayOfOvertime, overtime.startTime, overtime.endTime, overtime.isMeal, overtime.content,
                        overtime.approvalStatus, overtime.subTime, overtime.admitTime))
                .from(overtime)
                .where(overtime.staff.department.eq(department)
                        .and(baseDateEq(baseDate))
                        .and(nameLike(name))
                )
                .stream().count();
    }

    /*관리자의 추가근무 승인 요청 목록 조회*/
    public PageResult<OvertimeApproveListForm> getApproveListPagingByAdmin(String name, String department, String baseDate, Pageable pageable){
        List<OvertimeApproveListForm> content = getApproveListByAdmin(name, department, baseDate, pageable);
        long count = getApproveCountByAdmin(name, department, baseDate);
        return new PageResult<>(content, count);
    }

    private List<OvertimeApproveListForm> getApproveListByAdmin(String name, String department, String baseDate, Pageable pageable){
        return jpaQueryFactory
                .select(new QOvertimeApproveListForm(overtime.id, overtime.staff.name, overtime.staff.employeeNumber,
                        overtime.dayOfOvertime, overtime.startTime, overtime.endTime, overtime.isMeal, overtime.content,
                        overtime.approvalStatus, overtime.subTime, overtime.admitTime))
                .from(overtime)
                .where( (overtime.approvalStatus.eq(ApprovalStatus.CONFIRMATION).or(overtime.approvalStatus.eq(ApprovalStatus.FINAL)))
                        .and(baseDateEq(baseDate))
                        .and(departmentEq(department))
                        .and(nameLike(name))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(overtime.approvalStatus.desc(), overtime.id.desc())
                .fetch();
    }

    private long getApproveCountByAdmin(String name, String department, String baseDate){
        return jpaQueryFactory
                .select(new QOvertimeApproveListForm(overtime.id, overtime.staff.name, overtime.staff.employeeNumber,
                        overtime.dayOfOvertime, overtime.startTime, overtime.endTime, overtime.isMeal, overtime.content,
                        overtime.approvalStatus, overtime.subTime, overtime.admitTime))
                .from(overtime)
                .where( (overtime.approvalStatus.eq(ApprovalStatus.CONFIRMATION).or(overtime.approvalStatus.eq(ApprovalStatus.FINAL)))
                        .and(baseDateEq(baseDate))
                        .and(departmentEq(department))
                        .and(nameLike(name))
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

    private BooleanExpression departmentEq(String department){
        return department.equals("all") ? null : overtime.staff.department.eq(department);
    }

    private BooleanExpression nameLike(String name) {
        return name.equals("all") ? null : overtime.staff.name.eq(name);
    }
}
