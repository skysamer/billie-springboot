package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.time.LocalDate;

@Transactional
@Repository
public interface StaffRepository  extends JpaRepository<Staff, Long>{
    Staff findByEmail(String email);
    Staff findByStaffNum(Long staffNum);
    boolean existsByEmail(String email);
    Staff findByDepartmentAndRole(String department, String role);
}
