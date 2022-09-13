package com.lab.smartmobility.billie.vehicle.repository;

import com.lab.smartmobility.billie.vehicle.domain.Vehicle;
import com.lab.smartmobility.billie.vehicle.domain.VehicleReservation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.lab.smartmobility.billie.vehicle.domain.QVehicleReservation.vehicleReservation;

@Repository
@Transactional(readOnly = true)
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

    /*중복 예약시간 체크*/
    public long checkTimeIsDuplicated(Long rentNum, LocalDateTime rentedAt, LocalDateTime returnedAt, Vehicle vehicle){
        return jpaQueryFactory
                .selectFrom(vehicleReservation)
                .where(vehicleReservation.rentedAt.before(returnedAt)
                        .and(vehicleReservation.returnedAt.after(rentedAt))
                        .and(rentNumEq(rentNum))
                        .and(vehicleReservation.vehicle.eq(vehicle))
                )
                .stream().count();
    }

    /*월단위 차량 예약 조회*/
    public List<VehicleReservation> findAllByMonthly(LocalDateTime startDateTime, LocalDateTime endDateTime){
        return jpaQueryFactory
                .selectFrom(vehicleReservation)
                .where(vehicleReservation.rentedAt.between(startDateTime, endDateTime)
                        .or(vehicleReservation.returnedAt.between(startDateTime, endDateTime))
                )
                .fetch();
    }

    private BooleanExpression vehicleEq(Vehicle vehicle) {
        return vehicle != null ? vehicleReservation.vehicle.eq(vehicle) : null;
    }

    private BooleanExpression rentNumEq(Long rentNum) {
        return rentNum != null ? vehicleReservation.rentNum.ne(rentNum) : null;
    }

    private BooleanExpression disposalInfoEq(int disposalInfo) {
        return disposalInfo == 1 ? null : vehicleReservation.vehicle.rentalStatus.ne(99);
    }
}
