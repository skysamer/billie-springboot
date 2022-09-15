package com.lab.smartmobility.billie.vacation.service;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vacation.dto.VacationApproveListForm;
import com.lab.smartmobility.billie.vacation.repository.VacationApproveRepository;
import com.lab.smartmobility.billie.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationApproveService {
    private final VacationRepository vacationRepository;
    private final VacationApproveRepository approveRepository;
    private final StaffRepository staffRepository;
    private final Log log;

    /*부서장의 승인 요청 목록 조회*/
    public PageResult<VacationApproveListForm> getApproveListByManager(String baseDate, String email, String keyword, int isToggleOn, Pageable pageable){
        Staff manager = staffRepository.findByEmail(email);
        return approveRepository.getApproveListByManagerResult(baseDate, manager.getDepartment(), keyword, isToggleOn, pageable);
    }

}
