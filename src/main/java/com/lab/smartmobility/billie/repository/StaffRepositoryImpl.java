package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.dto.staff.DepartmentDTO;
import com.lab.smartmobility.billie.dto.staff.RankDTO;
import com.lab.smartmobility.billie.dto.staff.StaffInfoForm;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static com.lab.smartmobility.billie.entity.QStaff.staff;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StaffRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;
    private final Log log;

    public List<StaffInfoForm> getStaffInfoList(){
        return jpaQueryFactory
                .select(Projections.fields(StaffInfoForm.class,
                        staff.staffNum, staff.name, staff.role, staff.email, staff.phone, staff.department,
                        staff.birth, staff.hireDate, staff.degree, staff.graduationSchool, staff.graduationYear,
                        staff.major, staff.researcherNumber, staff.englishName))
                .from(staff)
                .fetch();
    }

    public List<DepartmentDTO> getDepartmentNameList(){
        return jpaQueryFactory
                .select(Projections.fields(DepartmentDTO.class, staff.department))
                .from(staff)
                .groupBy(staff.department)
                .fetch();
    }

    public List<RankDTO> getRankList(){
        return jpaQueryFactory
                .select(Projections.fields(RankDTO.class, staff.rank))
                .from(staff)
                .groupBy(staff.rank)
                .fetch();
    }
}
