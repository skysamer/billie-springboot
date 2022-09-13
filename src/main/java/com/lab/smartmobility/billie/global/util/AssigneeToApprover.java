package com.lab.smartmobility.billie.global.util;

import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.user.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssigneeToApprover {
    private final StaffRepository staffRepository;
    private static final Long ADMIN_ID = 4L;

    public Staff assignApproval(Staff applicant){
        if(applicant.getDepartment().equals("관리부") || applicant.getRole().equals("ROLE_MANAGER")){
            return staffRepository.findByStaffNum(ADMIN_ID);
        }
        return staffRepository.findByDepartmentAndRole(applicant.getDepartment(), "ROLE_MANAGER");
    }
}
