package com.lab.smartmobility.billie.repository.traffic;

import com.lab.smartmobility.billie.entity.TrafficCard;
import com.lab.smartmobility.billie.entity.TrafficCardReservation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.lab.smartmobility.billie.entity.QTrafficCardReservation.trafficCardReservation;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrafficCardReservationRepositoryImpl {
    private final Log log;
    private final JPAQueryFactory jpaQueryFactory;

    public List<TrafficCardReservation> findAll(TrafficCard trafficCard, LocalDateTime startDateTime, LocalDateTime endDateTime, int disposalInfo, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(trafficCardReservation)
                .where(trafficCardReservation.returnStatus.eq(1)
                        .and(trafficCardEq(trafficCard))
                        .and(trafficCardReservation.rentedAt.between(startDateTime, endDateTime))
                        .and(disposalInfoEq(disposalInfo))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(trafficCardReservation.returnedAt.desc())
                .fetch();
    }

    public List<TrafficCardReservation> findAll(TrafficCard trafficCard, int disposalInfo, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(trafficCardReservation)
                .where(trafficCardReservation.returnStatus.eq(1)
                        .and(trafficCardEq(trafficCard))
                        .and(disposalInfoEq(disposalInfo))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(trafficCardReservation.returnedAt.desc())
                .fetch();
    }

    public Long countByReturnStatus(TrafficCard trafficCard, LocalDateTime startDateTime, LocalDateTime endDateTime, int disposalInfo){
        return jpaQueryFactory.select(trafficCardReservation.count())
                .from(trafficCardReservation)
                .where(trafficCardReservation.returnStatus.eq(1)
                        .and(trafficCardReservation.rentedAt.between(startDateTime, endDateTime))
                        .and(trafficCardEq(trafficCard))
                        .and(disposalInfoEq(disposalInfo))
                )
                .fetch().get(0);
    }

    public Long countByReturnStatus(TrafficCard trafficCard, int disposalInfo){
        return jpaQueryFactory.select(trafficCardReservation.count())
                .from(trafficCardReservation)
                .where(trafficCardReservation.returnStatus.eq(1)
                        .and(trafficCardEq(trafficCard))
                        .and(disposalInfoEq(disposalInfo))
                )
                .fetch().get(0);
    }

    public List<TrafficCardReservation> findAll(TrafficCard trafficCard, LocalDateTime startDateTime, LocalDateTime endDateTime, int disposalInfo){
        return jpaQueryFactory
                .selectFrom(trafficCardReservation)
                .where(trafficCardReservation.returnStatus.eq(1)
                        .and(trafficCardEq(trafficCard))
                        .and(trafficCardReservation.rentedAt.between(startDateTime, endDateTime))
                        .and(disposalInfoEq(disposalInfo))
                )
                .fetch();
    }

    public List<TrafficCardReservation> findAll(TrafficCard trafficCard, int disposalInfo){
        return jpaQueryFactory
                .selectFrom(trafficCardReservation)
                .where(trafficCardReservation.returnStatus.eq(1)
                        .and(trafficCardEq(trafficCard))
                        .and(disposalInfoEq(disposalInfo))
                )
                .fetch();
    }

    private BooleanExpression trafficCardEq(TrafficCard trafficCard) {
        return trafficCard != null ? trafficCardReservation.trafficCard.eq(trafficCard) : null;
    }

    private BooleanExpression disposalInfoEq(int disposalInfo) {
        return disposalInfo == 1 ? null : trafficCardReservation.trafficCard.rentalStatus.ne(99);
    }
}
