package com.lab.smartmobility.billie.repository.traffic;

import com.lab.smartmobility.billie.dto.traffic.NonBorrowableTrafficCard;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.lab.smartmobility.billie.entity.QTrafficCard.trafficCard;
import static com.lab.smartmobility.billie.entity.QTrafficCardReservation.trafficCardReservation;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrafficCardRepositoryImpl {
    private final Log log;
    private final JPAQueryFactory jpaQueryFactory;

    public List<NonBorrowableTrafficCard> getNonBorrowableVehicleList(LocalDateTime rentedAt, LocalDateTime returnedAt, Long reservationNum){
        return jpaQueryFactory
                .select(Projections.fields(NonBorrowableTrafficCard.class, trafficCard.cardNum))
                .from(trafficCardReservation)
                .innerJoin(trafficCard)
                .on(trafficCardReservation.trafficCard.cardNum.eq(trafficCard.cardNum))
                .where(Expressions.asBoolean(true).isTrue()
                        .and(reservationNumNe(reservationNum))
                        .and(trafficCardReservation.rentedAt.before(returnedAt))
                        .and(trafficCardReservation.returnedAt.after(rentedAt)))
                .groupBy(trafficCard.cardNum)
                .fetch();
    }

    private BooleanExpression reservationNumNe(Long reservationNum){
        return reservationNum == -1 ? null : trafficCardReservation.reservationNum.ne(reservationNum);
    }
}
