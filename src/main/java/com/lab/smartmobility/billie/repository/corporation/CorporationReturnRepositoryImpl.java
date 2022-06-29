package com.lab.smartmobility.billie.repository.corporation;

import com.lab.smartmobility.billie.dto.corporation.*;
import com.lab.smartmobility.billie.entity.corporation.*;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorporationReturnRepositoryImpl {
    private final Log log;
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil baseDateParser;

    QApplication application=QApplication.application;
    QCorporationCardReturn cardReturn=QCorporationCardReturn.corporationCardReturn;
    QCorporationCardUseCase cardUseCase=QCorporationCardUseCase.corporationCardUseCase;
    QExpenseClaim expenseClaim=QExpenseClaim.expenseClaim;
    QExpenseCase expenseCase=QExpenseCase.expenseCase;

    /*법인카드 반납 이력 상세 조회*/
    public CorporationHistoryForm getCorporationHistory(Long returnId){
        CorporationHistoryForm corporationHistoryForm = jpaQueryFactory
                .select(Projections.bean(CorporationHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        application.corporationCard.cardName, application.corporationCard.company, application.corporationCard.cardNumber, application.corporationCard.rentalStatus,
                        cardReturn.returnId, cardReturn.totalAmountUsed, cardReturn.note))
                .from(application)
                .where(cardReturn.returnId.eq(returnId))
                .innerJoin(cardReturn).on(application.applicationId.eq(cardReturn.application.applicationId))
                .fetchOne();
        if(corporationHistoryForm==null){
            return new CorporationHistoryForm();
        }

        corporationHistoryForm.addCardUseCases(jpaQueryFactory
                .select(Projections.bean(CorporationUseCaseForm.class,
                        cardUseCase.usedAt, cardUseCase.purpose, cardUseCase.amount, cardUseCase.participants))
                .from(cardUseCase)
                .where(cardUseCase.corporationCardReturn.returnId.eq(returnId))
                .fetch());
        return corporationHistoryForm;
    }

    /*경비청구 이력 상세 조회*/
    public ExpenseClaimHistoryForm getExpenseClaimHistory(Long expenseId){
        ExpenseClaimHistoryForm expenseClaimHistoryForm = jpaQueryFactory
                .select(Projections.bean(ExpenseClaimHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        expenseClaim.expenseId, expenseClaim.depositBank, expenseClaim.depositAccountNumber, expenseClaim.totalAmountUsed, expenseClaim.note))
                .from(application)
                .where(expenseClaim.expenseId.eq(expenseId))
                .innerJoin(expenseClaim).on(application.applicationId.eq(expenseClaim.application.applicationId))
                .fetchOne();
        if(expenseClaimHistoryForm==null){
            return new ExpenseClaimHistoryForm();
        }

        expenseClaimHistoryForm.addExpenseCase(jpaQueryFactory
                .select(Projections.bean(ExpenseCaseForm.class,
                        expenseCase.amount, expenseCase.purpose, expenseCase.usedAt))
                .from(expenseCase)
                .where(expenseCase.expenseClaim.expenseId.eq(expenseId))
                .fetch());
        return expenseClaimHistoryForm;
    }

    /*나의 법인카드 반납 이력 목록 조회*/
    public List<CorporationHistoryForm> myReturnHistoryList(Long staffNum, String cardName, String baseYear, Pageable pageable){
        List<CorporationHistoryForm> corporationHistoryFormList = jpaQueryFactory
                .select(Projections.bean(CorporationHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        application.corporationCard.cardName, application.corporationCard.company, application.corporationCard.cardNumber, application.corporationCard.rentalStatus,
                        cardReturn.returnId, cardReturn.totalAmountUsed, cardReturn.note))
                .from(application)
                .where(
                        application.staff.staffNum.eq(staffNum)
                                .and(baseYearEq(baseYear))
                                .and(cardCompanyEq(cardName))
                                .and(cardNameEq(cardName))
                )
                .innerJoin(cardReturn).on(application.applicationId.eq(cardReturn.application.applicationId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(cardReturn.returnId.desc())
                .fetch();
        if(corporationHistoryFormList.size()==0){
            return new ArrayList<>();
        }
        for(CorporationHistoryForm corporationHistoryForm : corporationHistoryFormList){
            corporationHistoryForm.addCardUseCases(jpaQueryFactory
                    .select(Projections.bean(CorporationUseCaseForm.class,
                            cardUseCase.usedAt, cardUseCase.purpose, cardUseCase.amount, cardUseCase.participants))
                    .from(cardUseCase)
                    .where(cardUseCase.corporationCardReturn.returnId.eq(corporationHistoryForm.getReturnId()))
                    .fetch());
        }
        return corporationHistoryFormList;
    }

    /*나의 법인카드 반납 이력 조건별 개수 조회*/
    public int getMyReturnHistoryCount(Long staffNum, String cardName, String baseYear){
        return jpaQueryFactory
                .select(Projections.bean(CorporationHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        application.corporationCard.cardName, application.corporationCard.company, application.corporationCard.cardNumber, application.corporationCard.rentalStatus,
                        cardReturn.returnId, cardReturn.totalAmountUsed, cardReturn.note))
                .from(application)
                .where(
                        application.staff.staffNum.eq(staffNum)
                                .and(baseYearEq(baseYear))
                                .and(cardCompanyEq(cardName))
                                .and(cardNameEq(cardName))
                )
                .innerJoin(cardReturn).on(application.applicationId.eq(cardReturn.application.applicationId))
                .fetch().size();
    }

    /*나의 경비청구 이력 목록 조회*/
    public List<ExpenseClaimHistoryForm> getMyExpenseClaimHistoryList(Long staffNum, String baseYear, Pageable pageable){
        List<ExpenseClaimHistoryForm> expenseClaimHistoryFormList = jpaQueryFactory
                .select(Projections.bean(ExpenseClaimHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        expenseClaim.expenseId, expenseClaim.depositBank, expenseClaim.depositAccountNumber, expenseClaim.totalAmountUsed, expenseClaim.note))
                .from(application)
                .where(
                        application.staff.staffNum.eq(staffNum)
                                .and(baseYearEq(baseYear))
                )
                .innerJoin(expenseClaim).on(application.applicationId.eq(expenseClaim.application.applicationId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(expenseClaim.expenseId.desc())
                .fetch();
        if(expenseClaimHistoryFormList.size()==0){
            return new ArrayList<>();
        }

        for(ExpenseClaimHistoryForm expenseClaimHistoryForm : expenseClaimHistoryFormList){
            expenseClaimHistoryForm.addExpenseCase(jpaQueryFactory
                    .select(Projections.bean(ExpenseCaseForm.class,
                            expenseCase.amount, expenseCase.purpose, expenseCase.usedAt))
                    .from(expenseCase)
                    .where(expenseCase.expenseClaim.expenseId.eq(expenseClaimHistoryForm.getExpenseId()))
                    .fetch());
        }
        return expenseClaimHistoryFormList;
    }

    /*나의 경비청구 이력 조건별 개수*/
    public int getMyExpenseHistoryCount(Long staffNum, String baseYear){
        return jpaQueryFactory
                .select(Projections.bean(ExpenseClaimHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        expenseClaim.expenseId, expenseClaim.depositBank, expenseClaim.depositAccountNumber, expenseClaim.totalAmountUsed, expenseClaim.note))
                .from(application)
                .where(
                        application.staff.staffNum.eq(staffNum)
                                .and(baseYearEq(baseYear))
                )
                .innerJoin(expenseClaim).on(application.applicationId.eq(expenseClaim.application.applicationId))
                .fetch().size();
    }

    /*부서장 법인카드 반납 이력 목록 조회*/
    public List<CorporationHistoryForm> returnHistoryListByManager(String department, String role, int disposalInfo, String cardName, String baseYear, Pageable pageable){
        List<CorporationHistoryForm> corporationHistoryFormList = jpaQueryFactory
                .select(Projections.bean(CorporationHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        application.corporationCard.cardName, application.corporationCard.company, application.corporationCard.cardNumber, application.corporationCard.rentalStatus,
                        cardReturn.returnId, cardReturn.totalAmountUsed, cardReturn.note))
                .from(application)
                .where(
                        application.staff.department.eq(department)
                                .and(application.staff.role.eq(role))
                                .and(disposalInfoEq(disposalInfo))
                                .and(baseYearEq(baseYear))
                                .and(cardCompanyEq(cardName))
                                .and(cardNameEq(cardName))
                )
                .innerJoin(cardReturn).on(application.applicationId.eq(cardReturn.application.applicationId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(cardReturn.returnId.desc())
                .fetch();
        if(corporationHistoryFormList.size()==0){
            return new ArrayList<>();
        }
        for(CorporationHistoryForm corporationHistoryForm : corporationHistoryFormList){
            corporationHistoryForm.addCardUseCases(jpaQueryFactory
                    .select(Projections.bean(CorporationUseCaseForm.class,
                            cardUseCase.usedAt, cardUseCase.purpose, cardUseCase.amount, cardUseCase.participants))
                    .from(cardUseCase)
                    .where(cardUseCase.corporationCardReturn.returnId.eq(corporationHistoryForm.getReturnId()))
                    .fetch());
        }
        return corporationHistoryFormList;
    }

    /*부서장 법인카드 반납 이력 조건별 개수*/
    public int getReturnHistoryCountByManager(String department, String role, int disposalInfo, String cardName, String baseYear){
        return jpaQueryFactory
                .select(Projections.bean(CorporationHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        application.corporationCard.cardName, application.corporationCard.company, application.corporationCard.cardNumber, application.corporationCard.rentalStatus,
                        cardReturn.returnId, cardReturn.totalAmountUsed, cardReturn.note))
                .from(application)
                .where(
                        application.staff.department.eq(department)
                                .and(application.staff.role.eq(role))
                                .and(disposalInfoEq(disposalInfo))
                                .and(baseYearEq(baseYear))
                                .and(cardCompanyEq(cardName))
                                .and(cardNameEq(cardName))
                )
                .innerJoin(cardReturn).on(application.applicationId.eq(cardReturn.application.applicationId))
                .fetch().size();
    }

    /*부서장 경비청구 이력 목록 조회*/
    public List<ExpenseClaimHistoryForm> getExpenseClaimHistoryListByManager(String department, String role, String baseYear, Pageable pageable){
        List<ExpenseClaimHistoryForm> expenseClaimHistoryFormList = jpaQueryFactory
                .select(Projections.bean(ExpenseClaimHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        expenseClaim.expenseId, expenseClaim.depositBank, expenseClaim.depositAccountNumber, expenseClaim.totalAmountUsed, expenseClaim.note))
                .from(application)
                .where(
                        application.staff.department.eq(department)
                                .and(application.staff.role.eq(role))
                                .and(baseYearEq(baseYear))
                )
                .innerJoin(expenseClaim).on(application.applicationId.eq(expenseClaim.application.applicationId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(expenseClaim.expenseId.desc())
                .fetch();
        if(expenseClaimHistoryFormList.size()==0){
            return new ArrayList<>();
        }

        for(ExpenseClaimHistoryForm expenseClaimHistoryForm : expenseClaimHistoryFormList){
            expenseClaimHistoryForm.addExpenseCase(jpaQueryFactory
                    .select(Projections.bean(ExpenseCaseForm.class,
                            expenseCase.amount, expenseCase.purpose, expenseCase.usedAt))
                    .from(expenseCase)
                    .where(expenseCase.expenseClaim.expenseId.eq(expenseClaimHistoryForm.getExpenseId()))
                    .fetch());
        }
        return expenseClaimHistoryFormList;
    }

    /*부서장 경비청구 이력 조건별 개수*/
    public int getExpenseClaimHistoryCountByManager(String department, String role, String baseYear){
        return jpaQueryFactory
                .select(Projections.bean(ExpenseClaimHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        expenseClaim.expenseId, expenseClaim.depositBank, expenseClaim.depositAccountNumber, expenseClaim.totalAmountUsed, expenseClaim.note))
                .from(application)
                .where(
                        application.staff.department.eq(department)
                                .and(application.staff.role.eq(role))
                                .and(baseYearEq(baseYear))
                )
                .innerJoin(expenseClaim).on(application.applicationId.eq(expenseClaim.application.applicationId))
                .fetch().size();
    }

    /*관리자 법인카드 반납 이력 목록 조회*/
    public List<CorporationHistoryForm> getCardReturnHistoryListByAdmin(int disposalInfo, String cardName, String baseYear, Pageable pageable){
        List<CorporationHistoryForm> corporationHistoryFormList = jpaQueryFactory
                .select(Projections.bean(CorporationHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        application.corporationCard.cardName, application.corporationCard.company, application.corporationCard.cardNumber, application.corporationCard.rentalStatus,
                        cardReturn.returnId, cardReturn.totalAmountUsed, cardReturn.note))
                .from(application)
                .where(
                        application.approvalStatus.eq('f')
                                .and(disposalInfoEq(disposalInfo))
                                .and(baseYearEq(baseYear))
                                .and(cardCompanyEq(cardName))
                                .and(cardNameEq(cardName))
                )
                .innerJoin(cardReturn).on(application.applicationId.eq(cardReturn.application.applicationId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(cardReturn.returnId.desc())
                .fetch();
        if(corporationHistoryFormList.size()==0){
            return new ArrayList<>();
        }
        for(CorporationHistoryForm corporationHistoryForm : corporationHistoryFormList){
            corporationHistoryForm.addCardUseCases(jpaQueryFactory
                    .select(Projections.bean(CorporationUseCaseForm.class,
                            cardUseCase.usedAt, cardUseCase.purpose, cardUseCase.amount, cardUseCase.participants))
                    .from(cardUseCase)
                    .where(cardUseCase.corporationCardReturn.returnId.eq(corporationHistoryForm.getReturnId()))
                    .fetch());
        }
        return corporationHistoryFormList;
    }

    /*관리자 법인카드 반납 이력 조건별 개수*/
    public int getCardReturnHistoryCountByAdmin(int disposalInfo, String cardName, String baseYear){
        return jpaQueryFactory
                .select(Projections.bean(CorporationHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        application.corporationCard.cardName, application.corporationCard.company, application.corporationCard.cardNumber, application.corporationCard.rentalStatus,
                        cardReturn.returnId, cardReturn.totalAmountUsed, cardReturn.note))
                .from(application)
                .where(
                        application.approvalStatus.eq('f')
                                .and(disposalInfoEq(disposalInfo))
                                .and(baseYearEq(baseYear))
                                .and(cardCompanyEq(cardName))
                                .and(cardNameEq(cardName))
                )
                .innerJoin(cardReturn).on(application.applicationId.eq(cardReturn.application.applicationId))
                .fetch().size();
    }

    /*관리자 법인카드 반납 이력 엑셀 다운로드*/
    public List<CorporationHistoryForm> excelCardReturnHistoryListByAdmin(int disposalInfo, String cardName, String baseYear){
        List<CorporationHistoryForm> corporationHistoryFormList = jpaQueryFactory
                .select(Projections.bean(CorporationHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        application.corporationCard.cardName, application.corporationCard.company, application.corporationCard.cardNumber,
                        cardReturn.returnId, cardReturn.totalAmountUsed, cardReturn.note))
                .from(application)
                .where(
                        application.approvalStatus.eq('f')
                                .and(disposalInfoEq(disposalInfo))
                                .and(baseYearEq(baseYear))
                                .and(cardCompanyEq(cardName))
                                .and(cardNameEq(cardName))
                )
                .innerJoin(cardReturn).on(application.applicationId.eq(cardReturn.application.applicationId))
                .fetch();
        if(corporationHistoryFormList.size()==0){
            return new ArrayList<>();
        }
        for(CorporationHistoryForm corporationHistoryForm : corporationHistoryFormList){
            corporationHistoryForm.addCardUseCases(jpaQueryFactory
                    .select(Projections.bean(CorporationUseCaseForm.class,
                            cardUseCase.usedAt, cardUseCase.purpose, cardUseCase.amount, cardUseCase.participants))
                    .from(cardUseCase)
                    .where(cardUseCase.corporationCardReturn.returnId.eq(corporationHistoryForm.getReturnId()))
                    .fetch());
        }
        return corporationHistoryFormList;
    }

    /*관리자 경비청구 이력 목록 조회*/
    public List<ExpenseClaimHistoryForm> getExpenseClaimHistoryListByAdmin(String baseYear, Pageable pageable){
        List<ExpenseClaimHistoryForm> expenseClaimHistoryFormList = jpaQueryFactory
                .select(Projections.bean(ExpenseClaimHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        expenseClaim.expenseId, expenseClaim.depositBank, expenseClaim.depositAccountNumber, expenseClaim.totalAmountUsed, expenseClaim.note))
                .from(application)
                .where(
                        application.approvalStatus.eq('f')
                                .and(baseYearEq(baseYear))
                )
                .innerJoin(expenseClaim).on(application.applicationId.eq(expenseClaim.application.applicationId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(expenseClaim.expenseId.desc())
                .fetch();
        if(expenseClaimHistoryFormList.size()==0){
            return new ArrayList<>();
        }

        for(ExpenseClaimHistoryForm expenseClaimHistoryForm : expenseClaimHistoryFormList){
            expenseClaimHistoryForm.addExpenseCase(jpaQueryFactory
                    .select(Projections.bean(ExpenseCaseForm.class,
                            expenseCase.amount, expenseCase.purpose, expenseCase.usedAt))
                    .from(expenseCase)
                    .where(expenseCase.expenseClaim.expenseId.eq(expenseClaimHistoryForm.getExpenseId()))
                    .fetch());
        }
        return expenseClaimHistoryFormList;
    }

    /*관리자 경비청구 이력 조건별 개수 조회*/
    public int getExpenseClaimHistoryCountByAdmin(String baseYear){
        return jpaQueryFactory
                .select(Projections.bean(ExpenseClaimHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        expenseClaim.expenseId, expenseClaim.depositBank, expenseClaim.depositAccountNumber, expenseClaim.totalAmountUsed, expenseClaim.note))
                .from(application)
                .where(
                        application.approvalStatus.eq('f')
                                .and(baseYearEq(baseYear))
                )
                .innerJoin(expenseClaim).on(application.applicationId.eq(expenseClaim.application.applicationId))
                .fetch().size();
    }

    /*관리자 경비청구 이력 엑셀 다운로드*/
    public List<ExpenseClaimHistoryForm> excelExpenseClaimHistoryList(String baseYear){
        List<ExpenseClaimHistoryForm> expenseClaimHistoryFormList = jpaQueryFactory
                .select(Projections.bean(ExpenseClaimHistoryForm.class,
                        application.staff.name, application.staff.department,
                        application.startDate, application.startTime, application.endDate, application.endTime, application.content, application.isClaimedExpense,
                        expenseClaim.expenseId, expenseClaim.depositBank, expenseClaim.depositAccountNumber, expenseClaim.totalAmountUsed, expenseClaim.note))
                .from(application)
                .where(
                        application.approvalStatus.eq('f')
                                .and(baseYearEq(baseYear))
                )
                .innerJoin(expenseClaim).on(application.applicationId.eq(expenseClaim.application.applicationId))
                .fetch();
        if(expenseClaimHistoryFormList.size()==0){
            return new ArrayList<>();
        }

        for(ExpenseClaimHistoryForm expenseClaimHistoryForm : expenseClaimHistoryFormList){
            expenseClaimHistoryForm.addExpenseCase(jpaQueryFactory
                    .select(Projections.bean(ExpenseCaseForm.class,
                            expenseCase.amount, expenseCase.purpose, expenseCase.usedAt))
                    .from(expenseCase)
                    .where(expenseCase.expenseClaim.expenseId.eq(expenseClaimHistoryForm.getExpenseId()))
                    .fetch());
        }
        return expenseClaimHistoryFormList;
    }

    private BooleanExpression baseYearEq(String baseYear) {
        if(baseYear.equals("all")){
            return null;
        }

        LocalDate startDate=baseDateParser.getStartDate(baseYear);
        LocalDate endDate=baseDateParser.getEndDate(baseYear);
        return application.startDate.between(startDate, endDate);
    }

    private BooleanExpression cardNameEq(String cardName){
        if(cardName.equals("all")){
            return null;
        }
        String[] cardNumberAndCompany=cardName.split(" ");
        return application.corporationCard.company.eq(cardNumberAndCompany[0]);
    }

    private BooleanExpression cardCompanyEq(String cardName){
        if(cardName.equals("all")){
            return null;
        }
        String[] cardNumberAndCompany=cardName.split(" ");
        return application.corporationCard.cardName.eq(cardNumberAndCompany[1]);
    }

    private BooleanExpression disposalInfoEq(int disposalInfo) {
        return disposalInfo == 1 ? null : application.corporationCard.rentalStatus.ne(99);
    }
}
