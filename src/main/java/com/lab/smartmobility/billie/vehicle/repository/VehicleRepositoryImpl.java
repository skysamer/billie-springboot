package com.lab.smartmobility.billie.vehicle.repository;

import com.lab.smartmobility.billie.vehicle.dto.NonBorrowableVehicle;
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

import static com.lab.smartmobility.billie.vehicle.domain.QVehicleReservation.vehicleReservation;
import static com.lab.smartmobility.billie.vehicle.domain.QVehicle.vehicle;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VehicleRepositoryImpl {
    private final Log log;
    private final JPAQueryFactory jpaQueryFactory;

    public List<NonBorrowableVehicle> getNonBorrowableVehicleList(LocalDateTime rentedAt, LocalDateTime returnedAt, Long rentNum){
        return jpaQueryFactory
                .select(Projections.fields(NonBorrowableVehicle.class, vehicle.vehicleName))
                .from(vehicleReservation)
                .innerJoin(vehicle)
                .on(vehicleReservation.vehicle.vehicleNum.eq(vehicle.vehicleNum))
                .where(Expressions.asBoolean(true).isTrue()
                        .and(rentNumNe(rentNum))
                        .and(vehicleReservation.rentedAt.before(returnedAt))
                        .and(vehicleReservation.returnedAt.after(rentedAt))
                )
                .groupBy(vehicle.vehicleName)
                .fetch();
    }

    private BooleanExpression rentNumNe(Long rentNum){
        return rentNum == -1 ? null : vehicleReservation.rentNum.ne(rentNum);
    }
}
