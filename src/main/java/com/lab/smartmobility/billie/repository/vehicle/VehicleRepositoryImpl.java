package com.lab.smartmobility.billie.repository.vehicle;

import com.lab.smartmobility.billie.dto.vehicle.NonBorrowableVehicle;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.lab.smartmobility.billie.entity.QVehicleReservation.vehicleReservation;
import static com.lab.smartmobility.billie.entity.QVehicle.vehicle;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VehicleRepositoryImpl {
    private final Log log;
    private final JPAQueryFactory jpaQueryFactory;

    public List<NonBorrowableVehicle> getNonBorrowableVehicleList(LocalDateTime rentedAt, LocalDateTime returnedAt){
        return jpaQueryFactory
                .select(Projections.fields(NonBorrowableVehicle.class, vehicle.vehicleName))
                .from(vehicleReservation)
                .innerJoin(vehicle)
                .on(vehicleReservation.vehicle.vehicleNum.eq(vehicle.vehicleNum))
                .where(vehicleReservation.rentedAt.before(returnedAt)
                        .and(vehicleReservation.returnedAt.after(rentedAt)))
                .groupBy(vehicle.vehicleName)
                .fetch();
    }
}
