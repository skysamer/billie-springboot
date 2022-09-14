package com.lab.smartmobility.billie.staff.repository;

import com.lab.smartmobility.billie.staff.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StaffRepository  extends JpaRepository<Staff, Long>{
    Staff findByEmail(String email);
    Staff findByStaffNum(Long staffNum);
    boolean existsByEmail(String email);
    Staff findByDepartmentAndRole(String department, String role);
}
