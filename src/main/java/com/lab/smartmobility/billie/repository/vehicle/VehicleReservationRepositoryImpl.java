package com.lab.smartmobility.billie.repository.vehicle;

import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.entity.VehicleReservation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

import static com.lab.smartmobility.billie.entity.QVehicleReservation.vehicleReservation;

@Repository
@RequiredArgsConstructor
public class VehicleReservationRepositoryImpl {
    private final Log log;
    private final JPAQueryFactory jpaQueryFactory;

    public List<VehicleReservation> findAll(Vehicle vehicle, LocalDateTime startDateTime, LocalDateTime endDateTime, int disposalInfo, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(vehicleReservation)
                .where(vehicleReservation.returnStatusCode.eq(1)
                        .and(vehicleReservation.rentedAt.between(startDateTime, endDateTime))
                        .and(vehicleEq(vehicle))
                        .and(disposalInfoEq(disposalInfo))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vehicleReservation.returnedAt.desc())
                .fetch();
    }

    public List<VehicleReservation> findAll(Vehicle vehicle, int disposalInfo, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(vehicleReservation)
                .where(vehicleReservation.returnStatusCode.eq(1)
                        .and(vehicleEq(vehicle))
                        .and(disposalInfoEq(disposalInfo))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vehicleReservation.returnedAt.desc())
                .fetch();
    }

    public Long countByReturnStatus(Vehicle vehicle, LocalDateTime startDateTime, LocalDateTime endDateTime, int disposalInfo){
        return jpaQueryFactory.select(vehicleReservation.count())
                .from(vehicleReservation)
                .where(vehicleReservation.returnStatusCode.eq(1)
                        .and(vehicleReservation.rentedAt.between(startDateTime, endDateTime))
                        .and(vehicleEq(vehicle))
                        .and(disposalInfoEq(disposalInfo))
                )
                .fetch().get(0);
    }

    public Long countByReturnStatus(Vehicle vehicle, int disposalInfo){
        return jpaQueryFactory.select(vehicleReservation.count())
                .from(vehicleReservation)
                .where(vehicleReservation.returnStatusCode.eq(1)
                        .and(vehicleEq(vehicle))
                        .and(disposalInfoEq(disposalInfo))
                )
                .fetch().get(0);
    }

    public List<VehicleReservation> findAll(Vehicle vehicle, LocalDateTime startDateTime, LocalDateTime endDateTime, int disposalInfo) {
        return jpaQueryFactory
                .selectFrom(vehicleReservation)
                .where(vehicleReservation.returnStatusCode.eq(1)
                        .and(vehicleReservation.rentedAt.between(startDateTime, endDateTime))
                        .and(vehicleEq(vehicle))
                        .and(disposalInfoEq(disposalInfo))
                )
                .fetch();
    }

    public List<VehicleReservation> findAll(Vehicle vehicle, int disposalInfo) {
        return jpaQueryFactory
                .selectFrom(vehicleReservation)
                .where(vehicleReservation.returnStatusCode.eq(1)
                        .and(vehicleEq(vehicle))
                        .and(disposalInfoEq(disposalInfo))
                )
                .fetch();
    }

    private BooleanExpression vehicleEq(Vehicle vehicle) {
        return vehicle != null ? vehicleReservation.vehicle.eq(vehicle) : null;
    }

    private BooleanExpression disposalInfoEq(int disposalInfo) {
        return disposalInfo == 1 ? null : vehicleReservation.vehicle.rentalStatus.ne(99);
    }
}
