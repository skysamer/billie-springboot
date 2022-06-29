package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Equipment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static com.lab.smartmobility.billie.entity.QEquipment.equipment;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EquipmentRepositoryImpl implements EquipmentRepository{
    private final Log log = LogFactory.getLog(getClass());
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Equipment> findAll(String department) {
        return jpaQueryFactory
                .selectFrom(equipment)
                .where(departmentEq(department))
                .fetch();
    }

    private BooleanExpression departmentEq(String department) {
        return department != null ? equipment.department.eq(department) : null;
    }
}
