package com.lab.smartmobility.billie.staff.repository;

import com.lab.smartmobility.billie.global.dto.NameDropdownForm;
import com.lab.smartmobility.billie.global.dto.QNameDropdownForm;
import com.lab.smartmobility.billie.staff.dto.DepartmentDTO;
import com.lab.smartmobility.billie.staff.dto.RankDTO;
import com.lab.smartmobility.billie.staff.dto.StaffInfoForm;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static com.lab.smartmobility.billie.staff.domain.QStaff.staff;

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
                        staff.major, staff.researcherNumber, staff.englishName, staff.rank, staff.employeeNumber,
                        staff.vacationCount, staff.overtimeHour))
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

    public List<NameDropdownForm> getNameList(){
        return jpaQueryFactory
                .select(new QNameDropdownForm(staff.name, staff.vacationCount))
                .from(staff)
                .orderBy(staff.employeeNumber.asc())
                .fetch();
    }
}
