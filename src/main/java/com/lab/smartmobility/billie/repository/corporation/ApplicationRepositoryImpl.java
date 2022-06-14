package com.lab.smartmobility.billie.repository.corporation;

import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

import static com.lab.smartmobility.billie.entity.corporation.QApplication.application;
import static com.lab.smartmobility.billie.entity.corporation.QCorporationCard.corporationCard;
import static com.lab.smartmobility.billie.entity.QStaff.staff;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl {
    private final Log log;
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil baseDateParser;

    public List<Application> getMyApplicationList(Staff my, String cardName, String baseYear, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(application)
                .where(staff.eq(my)
                        .and(baseYearEq(baseYear))
                        .and(cardNameEq(cardName))
                        .and(cardCompanyEq(cardName))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(application.approvalStatus.desc())
                .orderBy(application.applicationId.desc())
                .fetch();
    }

    public long getMyApplicationCount(Staff my, String cardName, String baseYear){
        return jpaQueryFactory
                .selectFrom(application)
                .where(staff.eq(my)
                        .and(baseYearEq(baseYear))
                        .and(cardNameEq(cardName))
                        .and(cardCompanyEq(cardName))
                )
                .stream().count();
    }

    public List<Application> getApplicationListByManager(String department, String role,
                                                         String cardName, String baseYear, int disposalInfo, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(application)
                .leftJoin(corporationCard)
                .on(application.corporationCard.cardId.eq(corporationCard.cardId))
                .where(staff.department.eq(department).and(staff.role.eq(role))
                        .and(application.isReturned.eq(0))
                        .and(baseYearEq(baseYear))
                        .and(cardNameEq(cardName))
                        .and(cardCompanyEq(cardName))
                        .and(disposalInfoEq(disposalInfo))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(application.approvalStatus.desc())
                .orderBy(application.applicationId.desc())
                .fetch();
    }

    public long getApplicationCountByManager(String department, String role,
                                             String cardName, String baseYear, int disposalInfo){
        return jpaQueryFactory
                .selectFrom(application)
                .leftJoin(corporationCard)
                .on(application.corporationCard.cardId.eq(corporationCard.cardId))
                .where(staff.department.eq(department).and(staff.role.eq(role))
                        .and(application.isReturned.eq(0))
                        .and(baseYearEq(baseYear))
                        .and(cardNameEq(cardName))
                        .and(cardCompanyEq(cardName))
                        .and(disposalInfoEq(disposalInfo))
                )
                .stream().count();
    }

    public List<Application> getApplicationListAdmin(String cardName, String baseYear, int disposalInfo, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(application)
                .leftJoin(corporationCard)
                .on(application.corporationCard.cardId.eq(corporationCard.cardId))
                .where(application.isReturned.eq(0)
                        .and(cardNameEq(cardName))
                        .and(cardCompanyEq(cardName))
                        .and(disposalInfoEq(disposalInfo))
                        .and(baseYearEq(baseYear))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(application.approvalStatus.desc())
                .orderBy(application.applicationId.desc())
                .fetch();
    }

    public long getApplicationCountAdmin(String cardName, String baseYear, int disposalInfo){
        return jpaQueryFactory
                .selectFrom(application)
                .leftJoin(corporationCard)
                .on(application.corporationCard.cardId.eq(corporationCard.cardId))
                .where(application.isReturned.eq(0)
                        .and(baseYearEq(baseYear))
                        .and(cardNameEq(cardName))
                        .and(cardCompanyEq(cardName))
                        .and(disposalInfoEq(disposalInfo))
                )
                .stream().count();
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
        }else if(cardName.equals("개인경비 청구")){
            return application.isClaimedExpense.eq(1);
        }
        String[] cardNumberAndCompany=cardName.split(" ");
        return application.corporationCard.company.eq(cardNumberAndCompany[0]);
    }

    private BooleanExpression cardCompanyEq(String cardName){
        if(cardName.equals("all")){
            return null;
        }else if(cardName.equals("개인경비 청구")){
            return application.isClaimedExpense.eq(1);
        }
        String[] cardNumberAndCompany=cardName.split(" ");
        return application.corporationCard.cardName.eq(cardNumberAndCompany[1]);
    }

    private BooleanExpression disposalInfoEq(int disposalInfo) {
        return disposalInfo == 1 ? null : corporationCard.rentalStatus.ne(99).or(corporationCard.cardId.isNull());
    }
}
