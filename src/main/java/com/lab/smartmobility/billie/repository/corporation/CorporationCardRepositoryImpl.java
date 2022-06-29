package com.lab.smartmobility.billie.repository.corporation;

import com.lab.smartmobility.billie.entity.corporation.CorporationCard;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.lab.smartmobility.billie.entity.corporation.QCorporationCard.corporationCard;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorporationCardRepositoryImpl {
    private final Log log;
    private final JPAQueryFactory jpaQueryFactory;

    public List<CorporationCard> findAll(int disposalInfo){
        return jpaQueryFactory
                .selectFrom(corporationCard)
                .where(disposalInfoEq(disposalInfo))
                .fetch();
    }

    private BooleanExpression disposalInfoEq(int disposalInfo) {
        return disposalInfo == 1 ? null : corporationCard.rentalStatus.ne(99);
    }
}
